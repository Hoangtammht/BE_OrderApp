package com.example.demo.dao;

import com.example.demo.domain.request.RequestMenu;
import com.example.demo.domain.response.ResponseMenu;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MenuMapper {
    void addDishToMenu(RequestMenu requestMenu);
    List<ResponseMenu> getMenuByDate(LocalDate serveDate);
    ResponseMenu getMenuByID(int menuID);
}
