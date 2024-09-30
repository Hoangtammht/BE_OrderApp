package com.example.demo.service.impl;

import com.example.demo.dao.ClassMapper;
import com.example.demo.dao.MenuMapper;
import com.example.demo.dao.OrderMapper;
import com.example.demo.dao.UserMapper;
import com.example.demo.domain.Class;
import com.example.demo.domain.User;
import com.example.demo.domain.request.RequestOrder;
import com.example.demo.domain.request.RequestUpdateConfirm;
import com.example.demo.domain.response.ResponseMenu;
import com.example.demo.domain.response.ResponseOrder;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final MenuMapper menuMapper;
    private final UserMapper userMapper;
    private final ClassMapper classMapper;

    @Override
    public void createOrder(RequestOrder requestOrder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            ResponseMenu dishName = menuMapper.getMenuByID(requestOrder.getMenuID());
            if (dishName.getQuantity() < requestOrder.getQuantity()) {
                throw new ApiRequestException("Món ăn không còn đủ số lượng để cung cấp.");
            }
            User user = userMapper.findUserByUserName(authentication.getName());
            Class classInfo = classMapper.getClassByUserID(user.getUserID());
            requestOrder.setUserID(user.getUserID());
            requestOrder.setClassID(classInfo.getClassID());
            requestOrder.setTotalPrice(dishName.getPrice() * requestOrder.getQuantity());
            orderMapper.createOrder(requestOrder);
            int updatedQuantity = dishName.getQuantity() - requestOrder.getQuantity();
            menuMapper.updateQuantityOfMenu(requestOrder.getMenuID(), updatedQuantity);
        }catch (ApiRequestException e){
            throw e;
        }
    }

    @Override
    public List<ResponseOrder> getOrdersByDateRange(String from, String to) {
        return orderMapper.getOrdersByDateRange(from, to);
    }

    @Override
    public List<ResponseOrder> getOrdersByTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return orderMapper.getOrdersByTeacher(authentication.getName());
    }

    @Override
    public void confirmOrder(RequestUpdateConfirm requestUpdateConfirm) {
        orderMapper.confirmOrder(requestUpdateConfirm);
    }
}
