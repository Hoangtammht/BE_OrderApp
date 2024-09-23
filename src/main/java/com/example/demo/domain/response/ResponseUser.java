package com.example.demo.domain.response;

import com.example.demo.utils.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUser {
    private String userName;
    private String password;
    private String fullName;
    private int roleID;
    private String roleName;
    private String createdAt;
    private String updatedAt;
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = DateTimeUtil.formatLocalDateTime(createdAt);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = DateTimeUtil.formatLocalDateTime(updatedAt);
    }
}
