package com.example.demo.domain.response;

import com.example.demo.utils.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseOrder {
    private int orderID;
    private String userName;
    private String dishName;
    private String className;
    private int quantity;
    private double totalPrice;
    private String createdAt;
    private String updatedAt;

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = DateTimeUtil.formatLocalDateTime(createdAt);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = DateTimeUtil.formatLocalDateTime(updatedAt);
    }

}
