package com.springboot.boxo.service;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
}