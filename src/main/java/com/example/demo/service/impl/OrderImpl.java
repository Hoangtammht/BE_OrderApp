package com.example.demo.service.impl;

import com.example.demo.dao.MenuMapper;
import com.example.demo.dao.OrderMapper;
import com.example.demo.domain.request.RequestOrder;
import com.example.demo.domain.response.ResponseMenu;
import com.example.demo.domain.response.ResponseOrder;
import com.example.demo.service.interf.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final MenuMapper menuMapper;

    @Override
    public void createOrder(RequestOrder requestOrder) {
        ResponseMenu dishName = menuMapper.getMenuByID(requestOrder.getMenuID());
        requestOrder.setTotalPrice(dishName.getPrice() * requestOrder.getQuantity());
        requestOrder.setCreatedAt(String.valueOf(LocalDateTime.now()));
        orderMapper.createOrder(requestOrder);
    }

    @Override
    public List<ResponseOrder> getOrdersByDateRange(String from, String to) {
        return orderMapper.getOrdersByDateRange(from, to);
    }

    @Override
    public List<ResponseOrder> getOrdersByTeacher(String userName) {
        return orderMapper.getOrdersByTeacher(userName);
    }
}
