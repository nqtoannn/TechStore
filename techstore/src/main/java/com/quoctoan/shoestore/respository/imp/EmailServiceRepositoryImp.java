package com.quoctoan.shoestore.respository.imp;

import com.quoctoan.shoestore.respository.IEmailServiceRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

@Transactional
@Repository
public class EmailServiceRepositoryImp implements IEmailServiceRepository {

    private String fromEmail = "nguyentoan.13.9.2002@gmail.com";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration config;

    @Override
    public String sendMail(String to, String[] cc, String subject, Map<String, Object> model) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setCc(cc);
            mimeMessageHelper.setSubject(subject);
            Template t;
            if (subject.equals("Tài khoản truy cập TechStore của bạn đã được tạo")) {
                t = config.getTemplate("register.ftl");
            }
            else if (subject.equals("Thông báo sản phẩm mới")) {
                t = config.getTemplate("email-template.ftl");
            }
            else if (subject.equals("Đặt lại mật khẩu cho trang web TechStore")) {
                t = config.getTemplate("forgetPassword.ftl");
            }
            else if (subject.equals("Xác nhận tài khoản của bạn")) {
                t = config.getTemplate("confirmEmail.ftl");
            }
            else {
                t = config.getTemplate("order.ftl");
            }
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessageHelper.setText(html, true);
            javaMailSender.send(mimeMessage);
            return "mail send";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
