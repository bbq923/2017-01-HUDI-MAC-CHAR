package com.mapia.dao;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mapia.model.User;

// 원격 mysql이 연결이 안 돼서 잠시 사용하는 가짜 DB
public class Database {
	private static Map<String, User> users = Maps.newHashMap();
	
	public static void addUser(User user) {
		users.put(user.getEmail(), user);
	}
	
	public static User findUserByEmail(String email) {
		return users.get(email);
	}
	
	public static Collection<User> findAll() {
		return users.values();
	}
}
