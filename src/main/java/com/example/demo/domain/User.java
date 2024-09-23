package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int userID;
    private String userName;
    private String password;
    private String fullName;
    private int roleID;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
