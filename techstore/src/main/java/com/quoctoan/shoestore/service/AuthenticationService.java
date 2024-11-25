package com.quoctoan.shoestore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quoctoan.shoestore.Enum.Role;
import com.quoctoan.shoestore.Enum.TokenType;
import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.model.*;
import com.quoctoan.shoestore.respository.CartRepository;
import com.quoctoan.shoestore.respository.TokenRepository;
import com.quoctoan.shoestore.respository.UserRepository;
import com.quoctoan.shoestore.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final EmailSendService emailSendService;



  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .status("DEACTIVE")
            .phoneNumber("null")
            .fullName("null")
        .build();
    var savedUser = userRepository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    String url = "http://localhost:8080/techstore/api/account/activeUser/" + user.getId().toString();
    Map<String, Object> model = new HashMap<>();
    model.put("url", url);
    String[] cc = {};
    emailSendService.sendMail(user.getEmail(), cc,"Xác nhận tài khoản của bạn" ,model);
    return AuthenticationResponse.builder()
            .role(savedUser.getRole().toString())
            .accountId(savedUser.getId())
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = userRepository.findByEmail(request.getEmail())
        .orElseThrow();
    if (user.getStatus().equals("DEACTIVE")) {
      return AuthenticationResponse.builder()
              .accessToken("")
              .refreshToken("")
              .role("")
              .build();
    }
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    UserModel userModel = new UserModel();
    BeanUtils.copyProperties(user,userModel);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .role(String.valueOf(user.getRole()))
            .accountId(user.getId())
        .build();
  }

  public ResponseEntity<ResponseObject> rePassword(String json){
    JsonNode jsonNode;
    JsonMapper jsonMapper = new JsonMapper();
    try {
      jsonNode = jsonMapper.readTree(json);
      Integer userId = jsonNode.get("userId") != null ? jsonNode.get("userId").asInt() : 0;
      String password = jsonNode.get("password") != null ? jsonNode.get("password").asText() : "null";
      if(userId == 0 || password.equals("null")){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", "Invalid information", ""));
      }
      else {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
          User user = optionalUser.get();
          user.setPassword(passwordEncoder.encode(password));
          userRepository.save(user);
          return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", ""));
        }
        else {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", "Invalid information", ""));
        }
      }
    } catch (JsonProcessingException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", e.getMessage(), ""));
    }
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public AuthenticationResponse addEmployee(String json) {
    JsonNode jsonNode;
    JsonMapper jsonMapper = new JsonMapper();
    try {
      jsonNode = jsonMapper.readTree(json);
      Random random = new Random();
      var password = String.format("%08d", random.nextInt(100000000));
      String fullName = jsonNode.get("fullName") != null ? jsonNode.get("fullName").asText() : "";
      String email = jsonNode.get("email") != null ? jsonNode.get("email").asText() : "";
      String phoneNumber = jsonNode.get("phoneNumber") != null ? jsonNode.get("phoneNumber").asText() : "";
      var user = User.builder()
              .email(email)
              .password(passwordEncoder.encode(password))
              .role(Role.EMPLOYEE)
              .fullName(fullName)
              .phoneNumber(phoneNumber)
              .status("ACTIVE")
              .build();
      var savedUser = userRepository.save(user);
      if (savedUser.getPassword() != null) {
        String[] cc = {};
        Map<String, Object> model = new HashMap<>();
        model.put("userName", savedUser.getUsername());
        model.put("password", password);
        emailSendService.sendMail(savedUser.getEmail(), cc, "Tài khoản truy cập TechStore của bạn đã được tạo", model);
      }
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      saveUserToken(savedUser, jwtToken);
      return AuthenticationResponse.builder()
              .accessToken(jwtToken)
              .refreshToken(refreshToken)
              .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.userRepository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  public Boolean activeUser(Integer userId){
    Optional<User> optionalUser = userRepository.findById(userId);
    if(optionalUser.isPresent()){
      User user = optionalUser.get();
      user.setStatus("ACTIVE");
      userRepository.save(user);
      return true;
    } else {
      return false;
    }
  }

  public ResponseEntity<ResponseObject> forgotPassword(String json){
    JsonNode jsonNode;
    JsonMapper jsonMapper = new JsonMapper();
    try {
      jsonNode = jsonMapper.readTree(json);
      String email = jsonNode.get("email") != null ? jsonNode.get("email").asText() : "";
      Optional<User> optionalUser = userRepository.findUserByEmail(email);
      if(optionalUser.isPresent()) {
        User user = optionalUser.get();
        String url = "http://localhost:3000/auth/repassword/" + user.getId().toString();
        Map<String, Object> model = new HashMap<>();
        model.put("url", url);
        String[] cc = {};
        emailSendService.sendMail(user.getEmail(), cc, "Đặt lại mật khẩu cho trang web ShoeStore", model);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", url));
        }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Error", "Error", ""));
      } catch (JsonProcessingException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject("500", e.getMessage(), ""));
    }
  }

  public AuthenticationResponse processGrantCode(String code) {
    String accessToken = getOauthAccessTokenGoogle(code);
    User googleUser = getProfileDetailsGoogle(accessToken);
    Optional<User> userOptional = userRepository.findByEmail(googleUser.getEmail());
    if(userOptional.isPresent()){
      User user = userOptional.get();
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      saveUserToken(user, jwtToken);
      return AuthenticationResponse.builder()
              .role(user.getRole().toString())
              .accountId(user.getId())
              .accessToken(jwtToken)
              .refreshToken(refreshToken)
              .build();
    }else {
      var user = User.builder()
              .email(googleUser.getEmail())
              .role(Role.CUSTOMER)
              .status("ACTIVE")
              .phoneNumber(googleUser.getPhoneNumber())
              .fullName(googleUser.getFullName())
              .build();
      var savedUser = userRepository.save(user);
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      saveUserToken(savedUser, jwtToken);
      return AuthenticationResponse.builder()
              .role(user.getRole().toString())
              .accountId(user.getId())
              .accessToken(jwtToken)
              .refreshToken(refreshToken)
              .build();
    }
  }

  private String getOauthAccessTokenGoogle(String code) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", code);
    params.add("redirect_uri", "http://localhost:8080/techstore/api/auth/oauth2/callback/google");
    params.add("client_id", "733911543037-n7inen8gp4a2iko643jpdo25ogoejuv1.apps.googleusercontent.com");
    params.add("client_secret", "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");
    params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile");
    params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
    params.add("scope", "openid");
    params.add("grant_type", "authorization_code");

    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);

    String url = "https://oauth2.googleapis.com/token";
    String response = restTemplate.postForObject(url, requestEntity, String.class);
    JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

    return jsonObject.get("access_token").toString().replace("\"", "");
  }
  private User getProfileDetailsGoogle(String accessToken) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(accessToken);

    HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

    String url = "https://www.googleapis.com/oauth2/v2/userinfo";
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
    JsonObject jsonObject = new Gson().fromJson(response.getBody(), JsonObject.class);

    User user = new User();
    user.setEmail(jsonObject.get("email").toString().replace("\"", ""));
    String name = jsonObject.get("name").toString().replace("\"", "");
    String given_name = jsonObject.get("given_name").toString().replace("\"", "");
    user.setFullName(name + " "+ given_name);
    user.setPassword(UUID.randomUUID().toString());
    return user;
  }
}
