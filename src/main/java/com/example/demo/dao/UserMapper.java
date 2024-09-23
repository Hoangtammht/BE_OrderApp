package com.example.demo.dao;

import com.example.demo.domain.User;
import com.example.demo.domain.request.RequestAccount;
import com.example.demo.domain.response.ResponseUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User findUserByUserName(String userName);

    ResponseUser findResponseUserByUserName(String userName);

    void createAccount(RequestAccount user);

    List<User> getListUserByRole(int roleID);

}
