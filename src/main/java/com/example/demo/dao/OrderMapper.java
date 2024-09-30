package com.example.demo.dao;

import com.example.demo.domain.request.RequestOrder;
import com.example.demo.domain.request.RequestUpdateConfirm;
import com.example.demo.domain.response.ResponseOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    void createOrder(RequestOrder requestOrder);

    List<ResponseOrder> getOrdersByDateRange(String from, String to);

    List<ResponseOrder> getOrdersByTeacher(String userName);

    void confirmOrder(RequestUpdateConfirm requestUpdateConfirm);

}
