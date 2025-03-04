package com.example.moattravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.moattravel.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
	public VerificationToken findByToken(String token);//データベースのtoken列に一致するデータがあるか調べる。見つかった場合VerificationTokenオブジェクトを返す。なかった場合nullを返す
}
