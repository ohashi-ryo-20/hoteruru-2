package com.example.moattravel.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.moattravel.entity.User;

@Component
public class SignupEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;
	
	public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) { //springのイベント発行のためにDIしている
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	public void publishSignupEvent(User user, String requestUrl) {
		//thisでこのクラスのインスタンスをイベント発生元（source）として指定
		//user, requestUrlをイベントのデータとして渡す
		applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
	}
}
