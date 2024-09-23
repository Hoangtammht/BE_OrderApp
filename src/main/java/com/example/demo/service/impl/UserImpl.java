package com.example.demo.service.impl;

import com.example.demo.dao.UserMapper;
import com.example.demo.domain.User;
import com.example.demo.domain.UserRole;
import com.example.demo.domain.request.RequestAccount;
import com.example.demo.domain.response.ResponseUser;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.RoleService;
import com.example.demo.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImpl implements UserDetailsService, UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final RoleService roleService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findUserByUserName(username);
        if(user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserRole userRole = roleService.findRoleByUserName(user.getUserName());
        authorities.add(new SimpleGrantedAuthority(userRole.getRoleName()));
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }


    @Override
    public User findUserByUserName(String userName) {
        return userMapper.findUserByUserName(userName);
    }

    @Override
    public ResponseUser findResponseUserByUserName(String userName) {
        return userMapper.findResponseUserByUserName(userName);
    }

    @Override
    public void createAccount(RequestAccount user) throws Exception {
        if(userMapper.findUserByUserName(user.getUserName()) == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(String.valueOf(LocalDateTime.now()));
        }else{
            throw new ApiRequestException("Username " + user.getUserName() + " already exists.");
        }
        userMapper.createAccount(user);
    }

    @Override
    public List<User> getListUserByRole(int roleID) {
        return userMapper.getListUserByRole(roleID);
    }


}
