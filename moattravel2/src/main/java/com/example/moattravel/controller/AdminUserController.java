package com.example.moattravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.moattravel.entity.User;
import com.example.moattravel.repository.UserRepository;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	private final UserRepository userRepository;
	
	public AdminUserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@GetMapping
	//required 必須かどうか（必須にしてしまうと検索しない状態（デフォルト）がエラーになる）
	public String index(@RequestParam(name = "keyword", required = false)String keyword, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC)Pageable pageable, Model model) {
		Page<User> userPage;
		
		if(keyword != null && !keyword.isEmpty()) { //nullではなく空でもない場合
			userPage = userRepository.findByNameLikeOrFuriganaLike("%" + keyword + "%","%" + keyword + "%", pageable); //キーワードに一致している情報を表示
		} else {
			userPage = userRepository.findAll(pageable);//検索していない場合、全てのユーザー情報を表示
		}
		
		model.addAttribute("userPage", userPage);//検索結果をビューに渡す
		model.addAttribute("keyword", keyword);//検索フォームに検索した内容を保持するため（渡さなかったら、検索後、検索した内容が検索欄から消えるので何を検索したかわからなくなる）
		
		return "admin/users/index";
	}
}
