package com.quoctoan.shoestore.service;

import com.quoctoan.shoestore.respository.imp.EmailServiceRepositoryImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailSendService {
    @Autowired
    EmailServiceRepositoryImp emailServiceRepositoryImp;
//    public String sendMail(String to, String[] cc, String subject, Map<String, Object> body) {
//        return emailServiceRepositoryImp.sendMail(to, cc, subject, body);
//    }
    @Async
    public CompletableFuture<String> sendMail(String to, String[] cc, String subject, Map<String, Object> body) {
        String result = emailServiceRepositoryImp.sendMail(to, cc, subject, body);
        return CompletableFuture.completedFuture(result);
    }

}
