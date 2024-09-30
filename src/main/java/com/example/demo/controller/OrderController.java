package com.example.demo.controller;

import com.example.demo.domain.request.RequestOrder;
import com.example.demo.domain.request.RequestUpdateConfirm;
import com.example.demo.domain.response.ResponseOrder;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@RequestBody RequestOrder requestOrder) {
        try {
            orderService.createOrder(requestOrder);
            return ResponseEntity.ok().body("Order created successfully");
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/getListOrdersByDate")
    public ResponseEntity<?> getOrdersByDateRange(
            @RequestParam String from,
            @RequestParam String to) {
        try {
            List<ResponseOrder> orders = orderService.getOrdersByDateRange(from, to);
            return ResponseEntity.ok(orders);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @GetMapping("/getOrderByTeacherName")
    public ResponseEntity<?> getOrdersByTeacher() {
        try {
            List<ResponseOrder> orders = orderService.getOrdersByTeacher();
            return ResponseEntity.ok(orders);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @PutMapping("/confirmOrder")
    public ResponseEntity<?> confirmOrder(@RequestBody RequestUpdateConfirm requestUpdateConfirm) {
        try {
            orderService.confirmOrder(requestUpdateConfirm);
            return ResponseEntity.ok().body("Updated confirm order successfully");
        } catch (ApiRequestException e) {
            throw e;
        }
    }



}
