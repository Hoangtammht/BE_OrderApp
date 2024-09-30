package com.example.demo.service.interf;

import com.example.demo.domain.request.RequestOrder;
import com.example.demo.domain.request.RequestUpdateConfirm;
import com.example.demo.domain.response.ResponseOrder;

import java.util.List;

public interface OrderService {
    void createOrder(RequestOrder requestOrder);
    List<ResponseOrder> getOrdersByDateRange(String from, String to);

    List<ResponseOrder> getOrdersByTeacher();

    void confirmOrder(RequestUpdateConfirm requestUpdateConfirm);

}
