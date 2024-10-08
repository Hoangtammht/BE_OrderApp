package com.example.demo.domain.request;

import com.example.demo.utils.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestMenu {
    private int userID;
    private int scheduleID;
    private String dishName;
    private int quantity;
    private String serveDate;


}
