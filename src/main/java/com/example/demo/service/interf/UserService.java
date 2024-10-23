package com.example.demo.service.interf;

import com.example.demo.domain.User;
import com.example.demo.domain.request.RequestAccount;
import com.example.demo.domain.request.RequestEditAccount;
import com.example.demo.domain.response.ResponseUser;

import java.util.List;

public interface UserService {

    User findUserByUserName(String userName);


    ResponseUser findResponseUserByUserName(String userName);

    void createAccount(RequestAccount user) throws Exception;

    List<ResponseUser> getListUserByRole(int roleID);

    void editAccount(RequestEditAccount user);

    void deleteAccount(String userName);

    void saveRefreshToken(int userID, String refresh_token);
    boolean isRefreshTokenValid(int userID, String refresh_token);

    void deleteRefreshToken(int userID, String refreshToken);

}
