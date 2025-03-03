package com.example.moattravel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //springの設定クラスであることを示す。@Beanを使ってBeanを定義できる
@EnableWebSecurity //Spring Securityを適用するために必要。カスタマイズするときに明示的につける
@EnableMethodSecurity //メソッドレベルでセキュリティを有効化することができる
public class WebSecurityConfig {
	@Bean//メソッド単位でDIすることができる。@Configrationクラスの中で使うことでカスタム設定のオブジェクトを登録
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
					// /**　を使用することでその後のパスは全てが対象になる
					.requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/", "/signup/**").permitAll()//全てのユーザーにアクセスを許可するURL
					.requestMatchers("/admin/**").hasRole("ADMIN") //管理者にのみアクセスを許可するURL
					.anyRequest().authenticated()//上記以外のURLはログインが必要（会員または管理者のどちらでもOK）
					)
					.formLogin((form) -> form
							.loginPage("/login") //ログインページのURL
							.loginProcessingUrl("/login")//ログインフォームの送信先URL
							.defaultSuccessUrl("/?loggedIn")//ログイン成功時のリダイレクト先URL
							.failureUrl("/login?error")//ログイン失敗時のリダイレクト先URL
							.permitAll()
							)
					.logout((logout) -> logout
							.logoutSuccessUrl("/?loggedOut")//ログアウト時のリダイレクト先URL
							.permitAll()
							);
					return http.build();//DIコンテナに登録される
					
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		//BCryptはパスワード用のハッシュ値を生成してくれる
		return new BCryptPasswordEncoder();//DIコンテナに登録される
	}
}
