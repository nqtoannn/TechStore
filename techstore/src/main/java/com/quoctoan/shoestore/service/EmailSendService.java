package com.quoctoan.shoestore.service;

import com.quoctoan.shoestore.respository.imp.EmailServiceRepositoryImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailSendService {
    @Autowired
    EmailServiceRepositoryImp emailServiceRepositoryImp;
    public String sendMail(String to, String[] cc, String subject, Map<String, Object> body) {
        return emailServiceRepositoryImp.sendMail(to, cc, subject, body);
    }

}
