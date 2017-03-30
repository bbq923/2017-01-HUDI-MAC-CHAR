package com.mapia.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapia.model.User;

@Repository
public class UserRepository {
	
	@Autowired
	JdbcTemplate template; // 사용하려다 실패, 사용하는 법을 알면 리뷰 부탁... 
						   // 모른다면 무시하고 넘어가 주세요.
	
	public List<User> listForBeanPropertyRowMapper() {
        String query = "SELECT * FROM hello";
        return template.query(query, new BeanPropertyRowMapper(User.class));
    }
}
