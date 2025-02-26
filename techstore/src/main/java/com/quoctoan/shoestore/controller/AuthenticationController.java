package com.quoctoan.shoestore.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/techstore/api/auth")
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
              + "<title> Xác minh thất bại</title>"
              + "</head>"
              + "<body>"
              + "<h1>Xác minh thất bại</h1>"
              + "<p>Đã có lỗi xảy ra trong quá trình xác minh.</p>"
              + "</body>"
              + "</html>";
      return new ResponseEntity<>(errorHtml, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/login-url")
  public ResponseEntity<String> getGoogleLoginUrl() {
    String loginUrl = "https://accounts.google.com/o/oauth2/v2/auth?redirect_uri=http://localhost:8080/techstore/api/auth/oauth2/callback/google&response_type=code&client_id=733911543037-n7inen8gp4a2iko643jpdo25ogoejuv1.apps.googleusercontent.com&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid&access_type=offline";
    return ResponseEntity.ok(loginUrl);
  }

  @GetMapping("/oauth2/callback/google")
  public ResponseEntity<Void> grantCode(@RequestParam("code") String code, @RequestParam("scope") String scope, @RequestParam("authuser") String authUser, @RequestParam("prompt") String prompt) throws JsonProcessingException {
    AuthenticationResponse authenticationResponse = service.processGrantCode(code);
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResponse = objectMapper.writeValueAsString(authenticationResponse);
    String encodedResponse = URLEncoder.encode(jsonResponse, StandardCharsets.UTF_8);
    String url = "http://localhost:3000/oauth?authResponse=" + encodedResponse;
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
  }

}
