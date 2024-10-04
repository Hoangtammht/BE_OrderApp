package com.example.demo.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserRole;
import com.example.demo.domain.request.LoginUser;
import com.example.demo.domain.request.RequestAccount;
import com.example.demo.domain.request.RequestEditAccount;
import com.example.demo.domain.response.ResponseUser;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.RoleService;
import com.example.demo.service.interf.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
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
            Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUserName(), loginUser.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String name = authentication.getName();
            User user = userService.findUserByUserName(name);
            UserRole userRole = roleService.findRoleByUserName(user.getUserName());
            String access_token = JWT.create()
                    .withSubject(user.getUserName())
                    // .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("role", userRole.getRoleName())
                    .sign(algorithm);
            ResponseUser responseUser = userService.findResponseUserByUserName(user.getUserName());
            responseUser.setRoleName(userRole.getRoleName());
            Map<String, Object> tokens = new HashMap<>();
            tokens.put("access_token", access_token);
            tokens.put("User", responseUser);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (ApiRequestException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", "Tài khoản hoặc mật khẩu không chính xác");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }


    @PostMapping("/registerUser")
    public ResponseEntity<ResponseUser> createAccount(@RequestBody RequestAccount user) throws Exception {
        try{
            userService.createAccount(user);
            return ResponseEntity.ok(userService.findResponseUserByUserName(user.getUserName()));
        }catch (ApiRequestException e){
            throw e;
        }
    }

    @GetMapping("/getListUserByRole")
    public ResponseEntity<List<ResponseUser>> getListUserByRole(@RequestParam int roleID){
        try {
            return ResponseEntity.ok(userService.getListUserByRole(roleID));
        }catch (Exception e) {
            throw e;
        }
    }

    @PutMapping("/editAccount")
    public ResponseEntity<ResponseUser> editAccount(@RequestBody RequestEditAccount requestEditAccount) throws Exception {
        try{
            userService.editAccount(requestEditAccount);
            return ResponseEntity.ok(userService.findResponseUserByUserName(requestEditAccount.getUserName()));
        }catch (ApiRequestException e){
            throw e;
        }
    }

    @PutMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestParam String userName) throws Exception {
        try{
            userService.deleteAccount(userName);
            return ResponseEntity.ok(userService.findResponseUserByUserName(userName));
        }catch (ApiRequestException e){
            throw e;
        }
    }

}
