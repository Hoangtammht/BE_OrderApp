package com.example.demo.domain.response;

import com.example.demo.utils.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMenu {
    private int menuID;
    private int userID;
    private int scheduleID;
    private String dishName;
    private double price;
    private String serveDate;

    public void serveDate(LocalDateTime serveDate) {
        this.serveDate = DateTimeUtil.formatLocalDateTime(serveDate);
    }

}
