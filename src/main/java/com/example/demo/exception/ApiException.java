package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiException {
    private String title;
    private int status;
    private String message;
    private ZonedDateTime timestamp;

}
