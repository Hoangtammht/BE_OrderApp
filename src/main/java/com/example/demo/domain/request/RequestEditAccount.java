package com.example.demo.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEditAccount {
    private int userID;
    private String userName;
    private String password;
    private String fullName;
    private int roleID;
    private String updatedAt;

}
