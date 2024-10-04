package com.example.demo.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEditOrder {
    private int orderID;
    private int userID;
    private int menuID;
    private int classID;
    private int quantity;
    private String updatedAt;
}
