package com.mapia.web;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapia.dao.Database;
import com.mapia.domain.LoginResult;
import com.mapia.domain.SignUpResult;
import com.mapia.model.User;
import com.mapia.utils.HttpSessionUtils;

@RestController
@RequestMapping("/api")
public class ApiUserController {
	private static final Logger logger = LoggerFactory.getLogger(ApiUserController.class);
	
	@PostMapping("/login")
	public LoginResult login(@RequestBody User loginUser, HttpSession session) {
		logger.info("loginUser email : {}", loginUser.getEmail());
		logger.info("loginUser password : {}", loginUser.getPassword());
		
		User user = Database.findUserByEmail(loginUser.getEmail());
		
		if (user==null) {
			return LoginResult.emailNotFound("가입되지 않은 이메일 입니다.");
		}
		
		if (!user.matchPassword(loginUser)) {
			return LoginResult.invalidPassword("잘못된 비밀번호입니다.");
		}
		
		session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
		return LoginResult.ok();
	}
	
	@PostMapping("/signup")
	public SignUpResult signup(@RequestBody User newUser) {
		logger.info("newUser : {}", newUser);
		
		User user = Database.findUserByEmail(newUser.getEmail());
		
		if (user==null) {
			Database.addUser(newUser);
			return SignUpResult.ok();
		}
		
		return SignUpResult.emailExist("이미 존재하는 이메일 입니다.");
	}
}