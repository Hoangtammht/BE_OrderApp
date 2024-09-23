package com.example.demo.domain;


import com.example.demo.utils.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Class {

    private int classID;
    private int userID;
    private String className;
    private int ageGroup;

    private int studentCount;

    private String createdAt;
    private String updatedAt;

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = DateTimeUtil.formatLocalDateTime(createdAt);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = DateTimeUtil.formatLocalDateTime(updatedAt);
    }

}
