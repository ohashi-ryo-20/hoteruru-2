package com.example.moattravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel.form.SignupForm;
import com.example.moattravel.service.UserService;

@Controller
public class AuthController {
	private UserService userService;
	
	public AuthController(UserService userService) {
		this.userService = userService;
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
	public String signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
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
		
		userService.create(signupForm);
		redirectAttributes.addFlashAttribute("successMessage", "会員登録が完了しました。");
		
		return "redirect:/";
	}
}
