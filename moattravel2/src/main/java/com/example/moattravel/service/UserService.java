package com.example.moattravel.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.moattravel.entity.Role;
import com.example.moattravel.entity.User;
import com.example.moattravel.form.SignupForm;
import com.example.moattravel.repository.RoleRepository;
import com.example.moattravel.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public User create(SignupForm signupForm) {//SignupFormで入力されたパラメータを受け取る
		User user = new User();//新しい会員を作るためにインスタンスを生成
		Role role = roleRepository.findByName("ROLE_GENERAL"); //会員登録ページから登録するのは会員のみを想定しているためROLE_GENERALとなる
		
		user.setName(signupForm.getName());
		user.setFurigana(signupForm.getFurigana());
		user.setPostalCode(signupForm.getPostalCode());
		user.setAddress(signupForm.getAddress());
		user.setPhoneNumber(signupForm.getPhoneNumber());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));//PasswordEncoderのencodeメソッドでハッシュ化
		user.setRole(role);
		user.setEnabled(true);
		
		return userRepository.save(user);
	}
	
	//メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}
	
	//パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}
}
