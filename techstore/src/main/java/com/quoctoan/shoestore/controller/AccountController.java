package com.quoctoan.shoestore.controller;


import com.quoctoan.shoestore.model.AuthenticationRequest;
import com.quoctoan.shoestore.model.AuthenticationResponse;
import com.quoctoan.shoestore.model.RegisterRequest;
import com.quoctoan.shoestore.model.ResponseObject;
import com.quoctoan.shoestore.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/techstore/api/account")
@RequiredArgsConstructor
public class AccountController {
  @Autowired
  AuthenticationService service;

  @GetMapping("/activeUser/{userId}")
    public ResponseEntity<String> activeUser(@PathVariable Integer userId) {
      if (service.activeUser(userId)) {
        String successHtml = "<html>\n" +
                "  <head>\n" +
                "    <link href=\"https://fonts.googleapis.com/css?family=Nunito+Sans:400,400i,700,900&display=swap\" rel=\"stylesheet\">\n" +
                "  </head>\n" +
                "    <style>\n" +
                "      body {\n" +
                "        text-align: center;\n" +
                "        padding: 40px 0;\n" +
                "        background: #EBF0F5;\n" +
                "      }\n" +
                "        h1 {\n" +
                "          color: #88B04B;\n" +
                "          font-family: \"Nunito Sans\", \"Helvetica Neue\", sans-serif;\n" +
                "          font-weight: 900;\n" +
                "          font-size: 40px;\n" +
                "          margin-bottom: 10px;\n" +
                "        }\n" +
                "        p {\n" +
                "          color: #404F5E;\n" +
                "          font-family: \"Nunito Sans\", \"Helvetica Neue\", sans-serif;\n" +
                "          font-size:20px;\n" +
                "          margin: 0;\n" +
                "        }\n" +
                "      i {\n" +
                "        color: #9ABC66;\n" +
                "        font-size: 100px;\n" +
                "        line-height: 200px;\n" +
                "        margin-left:-15px;\n" +
                "      }\n" +
                "      .card {\n" +
                "        background: white;\n" +
                "        padding: 60px;\n" +
                "        border-radius: 4px;\n" +
                "        box-shadow: 0 2px 3px #C8D0D8;\n" +
                "        display: inline-block;\n" +
                "        margin: 0 auto;\n" +
                "      }\n" +
                "    </style>\n" +
                "    <body>\n" +
                "      <div class=\"card\">\n" +
                "      <div style=\"border-radius:200px; height:200px; width:200px; background: #F8FAF5; margin:0 auto;\">\n" +
                "        <i class=\"checkmark\">✓</i>\n" +
                "      </div>\n" +
                "        <h1>Xác minh thành công!</h1> \n" +
                "        <p>Bạn đã có thể đăng nhập trang web<br/> Cảm ơn bạn đã tin tưởng sử dụng dịch vụ của chúng tôi! </p>\n" +
                "      </div>\n" +
                "    </body>\n" +
                "</html>";
        return new ResponseEntity<>(successHtml, HttpStatus.OK);
      }
     else {
      String errorHtml = "<html>\n" +
              "  <head>\n" +
              "    <link href=\"https://fonts.googleapis.com/css?family=Nunito+Sans:400,400i,700,900&display=swap\" rel=\"stylesheet\">\n" +
              "  </head>\n" +
              "    <style>\n" +
              "      body {\n" +
              "        text-align: center;\n" +
              "        padding: 40px 0;\n" +
              "        background: #EBF0F5;\n" +
              "      }\n" +
              "        h1 {\n" +
              "          color: #e42525;\n" +
              "          font-family: \"Nunito Sans\", \"Helvetica Neue\", sans-serif;\n" +
              "          font-weight: 700;\n" +
              "          font-size: 20px;\n" +
              "          margin-bottom: 10px;\n" +
              "        }\n" +
              "        p {\n" +
              "          color: #404F5E;\n" +
              "          font-family: \"Nunito Sans\", \"Helvetica Neue\", sans-serif;\n" +
              "          font-size:20px;\n" +
              "          margin: 0;\n" +
              "        }\n" +
              "      i {\n" +
              "        color: #dd2722;\n" +
              "        font-size: 100px;\n" +
              "        line-height: 200px;\n" +
              "        margin-left:-15px;\n" +
              "      }\n" +
              "      .card {\n" +
              "        background: white;\n" +
              "        padding: 60px;\n" +
              "        border-radius: 4px;\n" +
              "        box-shadow: 0 2px 3px #C8D0D8;\n" +
              "        display: inline-block;\n" +
              "        margin: 0 auto;\n" +
              "      }\n" +
              "    </style>\n" +
              "    <body>\n" +
              "      <div class=\"card\">\n" +
              "      <div style=\"border-radius:200px; height:200px; width:200px; background: #f5bebe; margin:0 auto;\">\n" +
              "        <i class=\"checkmark\">✘</i>\n" +
              "      </div>\n" +
              "        <h1>Xác minh thất bại!</h1> \n" +
              "        <p>Liên kết của bạn có vẻ không hợp lệ<br/> \n" +
              "        Vui lòng thử lại sau</p>\n" +
              "      </div>\n" +
              "    </body>\n" +
              "</html>";
      return new ResponseEntity<>(errorHtml, HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping("/resetPassword")
    public ResponseEntity<ResponseObject> rePassword(@RequestBody String json){
      return service.rePassword(json);
  }

  @PostMapping("/forgotpassword")
  public ResponseEntity<ResponseObject> forgotPassword(@RequestBody String json){
    return service.forgotPassword(json);
  }

}
