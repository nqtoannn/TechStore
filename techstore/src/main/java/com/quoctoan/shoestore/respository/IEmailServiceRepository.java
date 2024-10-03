package com.quoctoan.shoestore.respository;

import java.util.Map;

public interface IEmailServiceRepository {
    String sendMail(String to, String[] cc, String subject, Map<String, Object> model);
}
