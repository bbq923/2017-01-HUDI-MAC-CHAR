package com.mapia;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mapia.dao.UserRepository;

public class UserRepositoryTest {

	@Test
	public void test() {
		UserRepository db = new UserRepository();
		
		// jdbcTemplate test 하려다 실패... 
		assertEquals("bbq923", db.listForBeanPropertyRowMapper().get(0).getNickname());
	}

}
