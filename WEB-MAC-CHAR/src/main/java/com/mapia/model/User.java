package com.mapia.model;

public class User {
	private String id;
	private String email;
	private String password;
	private String nickname;
	
	public User (String id, String email, String password, String nickname) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
	}
	
	public User (String email, String password, String nickname) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
	}
	
	public User (String email, String password) {
		this.email = email;
		this.password = password;
	}
	
//	public User(){} // default constructor
	
	public String getUserId() {
		return id;
	}
	
	public void setUserId(String id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public boolean matchPassword(User user) {
		return this.password.equals(user.getPassword());
	}

	@Override
	public String toString() {
		return "User [userId=" + id + ", email=" + email + ", password=" + password + ", nickname=" + nickname
				+ "]";
	}
}
