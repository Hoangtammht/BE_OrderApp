package com.example.demo.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {
    private int tokenID;
    private int userID;
    private String refreshToken;
    private Date createdAt;
    private Date updatedAt;

}

