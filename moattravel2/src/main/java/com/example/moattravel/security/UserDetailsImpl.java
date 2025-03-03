package com.example.moattravel.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.moattravel.entity.User;

public class UserDetailsImpl implements UserDetails { //SpringSecurityが提供するUserDetailsを実装
	private final User user;
	private final Collection<GrantedAuthority> authorities;
	
	public UserDetailsImpl(User user, Collection<GrantedAuthority> authorities) {
		this.user = user;
		this.authorities = authorities;
	}
	
	public User getUser() {
		return user;
	}
	
	//ハッシュ化済みのパスワードを返す
	@Override
	public String getPassword() {
		return user.getPassword();
	}
	
	//ログイン時に利用するユーザー名（メールアドレス）を返す
	@Override
	public String getUsername() {
		return user.getEmail();
	}
	
	//ロールのコレクションを返す
	@Override
	//<? extends GrantedAuthority> は[GrantedAuthorityまたはそのサブタイプ全て]という意味
	//サブタイプ　ある型から派生した型の事。親クラス,インターフェースを継承,実装したクラスのこと
	public Collection<? extends GrantedAuthority> getAuthorities() {//GrantedAuthorityはユーザーに割り当てられたロール（権限）を表すインターフェース
		return authorities;
	}
	
	//アカウントが期限切れでなければtrueを返す
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	//ユーザーがロックされていなければtrueを返す
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	//ユーザーのパスワードが期限切れでなければtrueを返す
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	//ユーザーが有効であればtrueを返す
	@Override
	public boolean isEnabled() {//メール認証済みの場合にのみログインできるようにisEnable()でユーザーの有効性をチェック
		return user.getEnabled();
	}
}
