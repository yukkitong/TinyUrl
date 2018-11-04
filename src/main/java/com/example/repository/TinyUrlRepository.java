package com.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TinyUrlRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public String findOne(String hash) {
    try {
      return jdbcTemplate.queryForObject("SELECT LONG_URL FROM TINY_URL WHERE ID='" + hash + "'", String.class);
    } catch(EmptyResultDataAccessException e) {
      return null;
    }
  }

  public int insert(String hash, String longUrl) {
    return jdbcTemplate.update("INSERT INTO TINY_URL (ID, LONG_URL) VALUES (?, ?)", hash, longUrl);
  }
}