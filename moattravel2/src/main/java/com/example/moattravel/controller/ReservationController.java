package com.example.moattravel.controller;

import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel.entity.House;
import com.example.moattravel.entity.Reservation;
import com.example.moattravel.entity.User;
import com.example.moattravel.form.ReservationInputForm;
import com.example.moattravel.form.ReservationRegisterForm;
import com.example.moattravel.repository.HouseRepository;
import com.example.moattravel.repository.ReservationRepository;
import com.example.moattravel.security.UserDetailsImpl;
import com.example.moattravel.service.ReservationService;
import com.example.moattravel.service.StripeService;

@Controller
public class ReservationController {
	private final ReservationRepository reservationRepository;
	private final HouseRepository houseRepository;
	private final ReservationService reservationService;
	private final StripeService stripeService;
	
	public ReservationController(ReservationRepository reservationRepository, HouseRepository houseRepository, ReservationService reservationService, StripeService stripeService) {
		this.reservationRepository = reservationRepository;
		this.houseRepository = houseRepository;
		this.reservationService = reservationService;
		this.stripeService = stripeService;
	}
	
	@GetMapping("/reservations")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC)Pageable pageable, Model model) {
		User user = userDetailsImpl.getUser();
		Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
		
		model.addAttribute("reservationPage", reservationPage);
		
		return "reservations/index";
	}
	
	@GetMapping("/houses/{id}/reservations/input")
	public String input(@PathVariable(name = "id")Integer id, @ModelAttribute @Validated ReservationInputForm reservationInputForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		House house = houseRepository.getReferenceById(id);//予約したい宿のIDを取得
		Integer numberOfPeople = reservationInputForm.getNumberOfPeople();//宿泊人数を取得
		Integer capacity = house.getCapacity();//宿の宿泊人数の定員を取得
		
		if (numberOfPeople != null) {//宿泊人数のnullチェック
			if (!reservationService.isWithinCapacity(numberOfPeople, capacity)) {//定員オーバーの場合処理
				FieldError fieldError = new FieldError(bindingResult.getObjectName(),"numberOfPeople", "宿泊人数が定員を超えています。");
				bindingResult.addError(fieldError);//エラーを追加
			}
		}
		
		if (bindingResult.hasErrors()) {//エラーがあるかチェック
			model.addAttribute("house", house);//画面に宿の情報を渡す
			model.addAttribute("errorMessage", "予約に不備があります。");
			
			return "houses/show";//エラーがあれば宿の詳細に戻す
		}
		
		redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);
		
		return "redirect:/houses/{id}/reservations/confirm";
	}
	
	@GetMapping("/houses/{id}/reservations/confirm")
	public String confirm(@PathVariable(name = "id")Integer id, @ModelAttribute ReservationInputForm reservationInputForm, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model, HttpServletRequest httpServletRequest) {
		House house = houseRepository.getReferenceById(id); //予約する宿の情報を取得
		User user = userDetailsImpl.getUser(); //現在ログインしているユーザーの情報を取得
		
		//チェックイン日とチェックアウト日を取得する
		LocalDate checkinDate = reservationInputForm.getCheckinDate();
		LocalDate checkoutDate = reservationInputForm.getCheckoutDate();
		
		//宿泊料金を計算する
		Integer price = house.getPrice();
		Integer amount = reservationService.calculateAmount(checkinDate, checkoutDate, price);
		
		//宿の情報やユーザー情報を渡した、reservationRegisterFormをインスタンス生成
		//ReservationRegisterFormのコンストラクタはString checkinDate でこのクラスではcheckinDateはLocalDate型なので、toString()で文字列を渡している
		ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(house.getId(),user.getId(),checkinDate.toString(),checkoutDate.toString(), reservationInputForm.getNumberOfPeople(), amount);
		String sessionId = stripeService.createStripeSession(house.getName(), reservationRegisterForm, httpServletRequest);
		
		model.addAttribute("house", house);
		model.addAttribute("reservationRegisterForm", reservationRegisterForm);
		model.addAttribute("sessionId", sessionId);
		
		return "reservations/confirm";
	}
	
//	@PostMapping("/houses/{id}/reservations/create")
//	public String create(@ModelAttribute ReservationRegisterForm reservationREgisterForm) {
//		reservationService.create(reservationREgisterForm);//予約情報を登録する
//		
//		return "redirect:/reservations?reserved";//リダイレクトで二重登録を防ぐ
//	}
	
}
