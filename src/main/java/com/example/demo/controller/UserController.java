package com.example.demo.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.domain.User;
import com.example.demo.domain.UserRole;
import com.example.demo.domain.request.LoginUser;
import com.example.demo.domain.request.RequestAccount;
import com.example.demo.domain.request.RequestEditAccount;
import com.example.demo.domain.response.ResponseUser;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.RoleService;
import com.example.demo.service.interf.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final RoleService roleService;

    private final AuthenticationManager authenticationManager;

    @Value("${SECRET_KEY}")
    private String secret;

    @PostMapping("/loginUser")
    public void loginUser(@RequestBody LoginUser loginUser,
                          HttpServletResponse response,
                          HttpServletRequest request) throws IOException {
        try {
            User user = userService.findUserByUserName(loginUser.getUserName());
            if (user.getStatus() == 1) {
                log.warn("Tài khoản bị khoá: {}", loginUser.getUserName());
                throw new ApiRequestException("Tài khoản của bạn đã bị block");
            }
            Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUserName(), loginUser.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String name = authentication.getName();
            UserRole userRole = roleService.findRoleByUserName(name);
            String access_token = JWT.create()
                    .withSubject(user.getUserName())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("role", userRole.getRoleName())
                    .sign(algorithm);

            String refresh_token = JWT.create()
                    .withSubject(user.getUserName())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 60L * 24 * 60 * 60 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .sign(algorithm);

            userService.saveRefreshToken(user.getUserID(), refresh_token);

            ResponseUser responseUser = userService.findResponseUserByUserName(user.getUserName());
            responseUser.setRoleName(userRole.getRoleName());
            Map<String, Object> tokens = new HashMap<>();
            tokens.put("access_token", access_token);
            tokens.put("refresh_token", refresh_token);
            tokens.put("User", responseUser);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            log.info("Người dùng {} đăng nhập thành công", user.getUserName());
        } catch (ApiRequestException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } catch (BadCredentialsException e) {
            log.error("Lỗi khi đăng nhập: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", "Tài khoản hoặc mật khẩu không chính xác");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } catch (Exception e) {
            log.error("Lỗi không mong đợi xảy ra: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }

    @PostMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.findUserByUserName(username);
                boolean isValid = userService.isRefreshTokenValid(user.getUserID(), refresh_token);
                if (!isValid) {
                    log.warn("Refresh token không hợp lệ cho người dùng: {}", username);
                    throw new RuntimeException("Refresh token is invalid");
                }

                UserRole userRole = roleService.findRoleByUserName(user.getUserName());

                String access_token = JWT.create()
                        .withSubject(user.getUserName())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("role", userRole.getRoleName())
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                log.info("Refresh token thành công cho người dùng: {}", username);
            } catch (Exception e) {
                log.error("Lỗi khi refresh token: {}", e.getMessage());
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            log.warn("Refresh token bị thiếu");
            throw new RuntimeException("Refresh token is missing");
        }
    }



    @PostMapping("/registerUser")
    public ResponseEntity<ResponseUser> createAccount(@RequestBody RequestAccount user) throws Exception {
        try{
            userService.createAccount(user);
            log.info("Người dùng đã đăng ký thành công: {}", user.getUserName());
            return ResponseEntity.ok(userService.findResponseUserByUserName(user.getUserName()));
        }catch (ApiRequestException e){
            throw e;
        }
    }

    @GetMapping("/getListUserByRole")
    public ResponseEntity<List<ResponseUser>> getListUserByRole(@RequestParam int roleID){
        try {
            List<ResponseUser> users = userService.getListUserByRole(roleID);
            log.info("Tìm thấy {} người dùng với role ID: {}", users.size(), roleID);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Lỗi khi tìm người dùng theo role: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/editAccount")
    public ResponseEntity<ResponseUser> editAccount(@RequestBody RequestEditAccount requestEditAccount) throws Exception {
        try {
            userService.editAccount(requestEditAccount);
            log.info("Cập nhật tài khoản thành công cho người dùng: {}", requestEditAccount.getUserName());
            return ResponseEntity.ok(userService.findResponseUserByUserName(requestEditAccount.getUserName()));
        } catch (ApiRequestException e) {
            log.error("Lỗi khi cập nhật tài khoản cho người dùng: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestParam String userName) throws Exception {
        try {
            userService.deleteAccount(userName);
            log.info("Xoá tài khoản thành công cho người dùng: {}", userName);
            return ResponseEntity.ok("Account deleted successfully");
        } catch (ApiRequestException e) {
            log.error("Lỗi khi xoá tài khoản cho người dùng: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.findUserByUserName(username);
                userService.deleteRefreshToken(user.getUserID(), refresh_token);
                log.info("Người dùng {} đã đăng xuất thành công", username);
                response.setStatus(HttpServletResponse.SC_OK);
                Map<String, String> responseMessage = new HashMap<>();
                responseMessage.put("message", "Logged out successfully");
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), responseMessage);
            } catch (Exception e) {
                log.error("Lỗi khi đăng xuất: {}", e.getMessage());
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            log.warn("Không tìm thấy refresh token trong header");
            throw new RuntimeException("Refresh token is missing");
        }
    }



}
