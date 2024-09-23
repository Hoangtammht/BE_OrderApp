package com.example.demo.service.interf;

import com.example.demo.domain.User;
import com.example.demo.domain.request.RequestAccount;
import com.example.demo.domain.response.ResponseUser;

import java.util.List;

public interface UserService {

    User findUserByUserName(String userName);

    ResponseUser findResponseUserByUserName(String userName);

    void createAccount(RequestAccount user) throws Exception;

    List<User> getListUserByRole(int roleID);

}
