package com.example.demo.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserRole;
import com.example.demo.domain.request.LoginUser;
import com.example.demo.domain.request.RequestAccount;
import com.example.demo.domain.response.ResponseUser;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.RoleService;
import com.example.demo.service.interf.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUserName(), loginUser.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String name = authentication.getName();
            User user = userService.findUserByUserName(name);
            UserRole userRole = roleService.findRoleByUserName(user.getUserName());
            String access_token = JWT.create()
                    .withSubject(user.getUserName())
//                    .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("role", userRole.getRoleName())
                    .sign(algorithm);
            ResponseUser responseUser = userService.findResponseUserByUserName(user.getUserName());
            responseUser.setRoleName(userRole.getRoleName());
            Map<String, Object> tokens = new HashMap<>();
            tokens.put("access_token", access_token);
            tokens.put("User", responseUser);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }catch (Exception e){
            response.setHeader("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
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
    public ResponseEntity<List<User>> getListUserByRole(@RequestParam int roleID){
        try {
            return ResponseEntity.ok(userService.getListUserByRole(roleID));
        }catch (Exception e) {
            throw e;
        }
    }


}
