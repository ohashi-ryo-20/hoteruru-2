package com.example.moattravel.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel.entity.User;
import com.example.moattravel.entity.VerificationToken;
import com.example.moattravel.event.SignupEventPublisher;
import com.example.moattravel.form.SignupForm;
import com.example.moattravel.service.UserService;
import com.example.moattravel.service.VerificationTokenService;

@Controller
public class AuthController {
	private final UserService userService;
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;
	
	
	public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService) {
		this.userService = userService;
		this.signupEventPublisher = signupEventPublisher;
		this.verificationTokenService = verificationTokenService;
	}
	
	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("signupForm", new SignupForm());
		return "auth/signup";
	}
	
	@PostMapping("/signup")
	//HttpServletRequest　httpリクエストに関するさまざまな情報を提供するインターフェース
	public String signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
		// メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if (userService.isEmailRegistered(signupForm.getEmail())) { //isEmailRegisteredメソッドで入力されたemailが登録済みか確認
			//bindingResult.getObjectName() はバリデーション対象のフォームオブジェクトの名前を取得。今回はsignupFormになる
			FieldError fieldError = new FieldError(bindingResult.getObjectName(),"email", "すでに登録済みのメールアドレスです。"); //trueの場合、エラーメッセージを表示するようにする
			bindingResult.addError(fieldError); //インスタンスを追加
		}
		
		// パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
		if(!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(),"password","パスワードが一致しません。");
			bindingResult.addError(fieldError);
		}
		
		if(bindingResult.hasErrors()) { //エラーがある場合
			return "auth/signup";
		}
		
		User createdUser = userService.create(signupForm);
		String requestUrl = new String(httpServletRequest.getRequestURL()); //getRequestURLメソッドでリクエストURLを所得している
		signupEventPublisher.publishSignupEvent(createdUser, requestUrl); //ユーザーの会員登録が完了したタイミングでイベントを発行
		redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
		
		return "redirect:/";
	}
	
	@GetMapping("/signup/verify")
	public String verify(@RequestParam(name = "token") String token, Model model) { //@RequestParamでURLのtokenのパラメータを取得
		VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);//トークンがデータベースに存在するか調べる
		if (verificationToken != null) { //トークンが存在すれば実行
			User user = verificationToken.getUser(); //ユーザーを取得
			userService.enableUser(user); //メール認証成功したため、enableをtrueにする
			String successMessage = "会員登録が完了しました。";
			model.addAttribute("successMessage", successMessage);
		} else {
			String errorMessage = "トークンが無効です。";
			model.addAttribute("errorMessage", errorMessage);
		}
		
		return "auth/verify";
	}
}
