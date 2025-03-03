package com.example.moattravel.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.moattravel.entity.User;
import com.example.moattravel.repository.UserRepository;

@Service
//クラスの役割　UserDetailsImplクラスのインスタンス生成
public class UserDetailsServiceImpl implements UserDetailsService {//SpringSecurityが提供するUserDetailsServiceインターフェース
	private final UserRepository userRepository;
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	//定義されている抽象メソッドはloadUserByUsername()のみ
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			User user = userRepository.findByEmail(email);//emailからユーザーを特定する
			String userRoleName = user.getRole().getName();//ユーザーネームとロール（権限）を取得
			Collection<GrantedAuthority> authorities = new ArrayList<>();//Spring Security では、ユーザーの権限は 複数持つ可能性があるため、「順番を保持できて、重複を許すコレクション（リスト）」が適しています。
			authorities.add(new SimpleGrantedAuthority(userRoleName));//Spring Security では、UserDetails の実装クラスで ユーザーに付与する権限 をリストとして持たせる。「ユーザーにどんな権限を持たせるか」を設定するとき、SimpleGrantedAuthority を使うのが基本的な流れ
			
			return new UserDetailsImpl(user, authorities);//インスタンスを生成
		} catch (Exception e) {
			throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");//ユーザーが見つからないときの処理
		}
	}
	
	
}
