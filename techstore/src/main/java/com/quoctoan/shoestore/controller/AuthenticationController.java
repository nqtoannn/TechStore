package com.quoctoan.shoestore.controller;


import com.quoctoan.shoestore.entity.Order;
import com.quoctoan.shoestore.entity.OrderStatus;
import com.quoctoan.shoestore.model.AuthenticationRequest;
import com.quoctoan.shoestore.model.AuthenticationResponse;
import com.quoctoan.shoestore.model.RegisterRequest;
import com.quoctoan.shoestore.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/shoestore/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService service;
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }
  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @PostMapping("/activeUser/{userId")
    public ResponseEntity<String> activeUser(@PathVariable Integer userId) {
      if (service.activeUser(userId)) {
        String successHtml = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Xác minh tài khoản thành công</title>"
                + "</head>"
                + "<body>"
                + "<h1>Cảm ơn bạn đã xác minh tài khoản của mình. Bạn đã có thể </h1>"
                + "<p>Cảm ơn bạn đã thanh toán. Trang này sẽ tự động đóng lại sau vài giây.</p>"
                + "<script>"
                + "setTimeout(function() { window.close(); }, 5000);"
                + "</script>"
                + "</body>"
                + "</html>";
        return new ResponseEntity<>(successHtml, HttpStatus.OK);
      }
     else {
      String errorHtml = "<!DOCTYPE html>"
              + "<html>"
              + "<head>"
              + "<title>Thanh toán thất bại</title>"
              + "</head>"
              + "<body>"
              + "<h1>Thanh toán thất bại</h1>"
              + "<p>Đã có lỗi xảy ra trong quá trình thanh toán.</p>"
              + "</body>"
              + "</html>";
      return new ResponseEntity<>(errorHtml, HttpStatus.BAD_REQUEST);
    }
  }


}
