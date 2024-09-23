package com.example.demo.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAccount {
    private String userName;
    private String password;
    private String fullName;
    private int roleID;
    private String createdAt;
    private String updatedAt;

}
