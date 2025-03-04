package com.example.moattravel.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.moattravel.entity.User;
import com.example.moattravel.service.VerificationTokenService;

@Component
public class SignupEventListener {
	private final VerificationTokenService verificationTokenService;
	private final JavaMailSender javaMailSender;
	
	public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
		this.verificationTokenService = verificationTokenService;
		this.javaMailSender = mailSender;
	}
	
	@EventListener //イベント発生時に実行されるアノテーション
	private void onSignupEvent(SignupEvent signupEvent) { //引数でどのイベント発生時か指定。今回はSignupEventクラスから通知を受けたときに実行される
		User user = signupEvent.getUser();
		String token = UUID.randomUUID().toString();//tokenをUUIDで生成
		verificationTokenService.create(user,  token); //createメソッドでインスタンス化してsetしている
		
		String recipientAddress = user.getEmail();
		String subject = "メール認証";
		String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token; //生成したトークンをメール認証用のURLにパラメータとして埋め込み、アクセス時にデータベースの値と一致するか確認できる
		String message = "以下のリンクをクリックして会員登録を完了してください。";
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(recipientAddress);
		mailMessage.setSubject(subject);
		mailMessage.setText(message + "\n" + confirmationUrl);
		javaMailSender.send(mailMessage);
	}
}
