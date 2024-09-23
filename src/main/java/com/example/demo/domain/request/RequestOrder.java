package com.example.demo.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrder {
    private int userID;
    private int menuID;
    private int classID;
    private int quantity;
    private double totalPrice;
    private String createdAt;


}

