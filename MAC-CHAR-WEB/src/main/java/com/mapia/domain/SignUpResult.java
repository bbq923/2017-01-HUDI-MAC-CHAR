package com.mapia.domain;

import com.mapia.utils.Result;

public class SignUpResult implements Result {
	private Status statusCode;
	private String errorMessage;
	
	private SignUpResult(Status statusCode, String errorMessage) {
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
	}
	
	public Status getStatusCode() {
		return statusCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public static SignUpResult ok() {
		return new SignUpResult(Status.Ok, null);
	}
	
	public static SignUpResult emailExist(String errorMessage) {
		return new SignUpResult(Status.EmailExists, errorMessage);
	}
	
	public static SignUpResult nicknameExist(String errorMessage) {
		return new SignUpResult(Status.NicknameExists, errorMessage);
	}
	
	// 이건 언제 써야 할까? 쓸일이 생길 수 있을까?
	public static SignUpResult unexpectedError(String errorMessage) {
		return new SignUpResult(Status.Error, errorMessage);
	}
}
