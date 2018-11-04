package com.example.controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import com.example.repository.TinyUrlRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class GeneratorController {

  @Autowired
  private TinyUrlRepository tinyUrlRepository;

  private final static int HASH_LEN_LIMIT = 6;

  @PostMapping(path="/gen", consumes = "application/json", produces = "application/json")
  public String generate(@RequestBody Map<String, Object> payload) {
    final String url = payload.get("url").toString();
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.update(url.getBytes("UTF-8"));

      byte byteData[] = md.digest();
      StringBuffer sb = new StringBuffer(); 
      for(int i = 0; i < byteData.length; i++) {
          sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
      }

      String hash = sb.substring(0, HASH_LEN_LIMIT);
      String longUrl = tinyUrlRepository.findOne(hash);
      if (longUrl != null && longUrl.equals(url)) {
        return outputJson(url, hash(hash));
      }
      tinyUrlRepository.insert(hash, url);
      return outputJson(url, hash(hash));
    } catch(NoSuchAlgorithmException e){
      e.printStackTrace();
    } catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return "";
  }

  @PostMapping(path="/gen", consumes = "application/x-www-form-urlencoded", produces = "application/json")
  public String generate(@RequestParam String url) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.update(url.getBytes("UTF-8"));

      byte byteData[] = md.digest();
      StringBuffer sb = new StringBuffer(); 
      for(int i = 0; i < byteData.length; i++) {
          sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
      }

      String hash = sb.substring(0, HASH_LEN_LIMIT);
      String longUrl = tinyUrlRepository.findOne(hash);
      if (longUrl != null && longUrl.equals(url)) {
        return outputJson(url, hash(hash));
      }
      tinyUrlRepository.insert(hash, url);
      return outputJson(url, hash(hash));
    } catch(NoSuchAlgorithmException e){
      e.printStackTrace();
    } catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return "";
  }

  private String hash(String hash) {
    return "http://ms.c/" + hash;
  }

  private String outputJson(String intput, String output) {
    return "{" +
        "\"input\":\"" + intput + "\"," +
        "\"output\":\"" + output + "\"" +
      "}";
  }
}