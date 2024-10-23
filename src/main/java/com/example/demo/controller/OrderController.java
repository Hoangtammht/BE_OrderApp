package com.example.demo.controller;

import com.example.demo.domain.request.RequestEditOrder;
import com.example.demo.domain.request.RequestOrder;
import com.example.demo.domain.request.RequestUpdateConfirm;
import com.example.demo.domain.response.ResponseOrder;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@RequestBody RequestOrder requestOrder) {
        try {
            log.info("Yêu cầu tạo đơn hàng: {}", requestOrder);
            orderService.createOrder(requestOrder);
            log.info("Đơn hàng đã được tạo thành công.");
            return ResponseEntity.ok().body("Order created successfully");
        } catch (ApiRequestException e) {
            log.error("Lỗi khi tạo đơn hàng: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/editOrder")
    public ResponseEntity<?> editOrder(@RequestBody RequestEditOrder requestEditOrder) {
        try {
            log.info("Yêu cầu chỉnh sửa đơn hàng: {}", requestEditOrder);
            orderService.editOrder(requestEditOrder);
            log.info("Đơn hàng đã được chỉnh sửa thành công.");
            return ResponseEntity.ok().body("Order edited successfully");
        } catch (ApiRequestException e) {
            log.error("Lỗi khi chỉnh sửa đơn hàng: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/getListOrdersByDate")
    public ResponseEntity<?> getOrdersByDateRange(
            @RequestParam String from,
            @RequestParam String to) {
        try {
            log.info("Yêu cầu lấy danh sách đơn hàng từ {} đến {}", from, to);
            List<ResponseOrder> orders = orderService.getOrdersByDateRange(from, to);
            log.info("Lấy danh sách đơn hàng thành công: {}", orders.size());
            return ResponseEntity.ok(orders);
        } catch (ApiRequestException e) {
            log.error("Lỗi khi lấy danh sách đơn hàng: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/getOrderByTeacherName")
    public ResponseEntity<?> getOrdersByTeacher() {
        try {
            log.info("Yêu cầu lấy danh sách đơn hàng theo tên giáo viên.");
            List<ResponseOrder> orders = orderService.getOrdersByTeacher();
            log.info("Lấy danh sách đơn hàng theo giáo viên thành công: {}", orders.size());
            return ResponseEntity.ok(orders);
        } catch (ApiRequestException e) {
            log.error("Lỗi khi lấy danh sách đơn hàng theo giáo viên: {}", e.getMessage());
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
