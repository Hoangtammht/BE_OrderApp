package com.example.demo.service.interf;

import com.example.demo.domain.request.RequestEditPriceMenu;
import com.example.demo.domain.request.RequestMenu;
import com.example.demo.domain.response.ResponseMenu;

import java.time.LocalDate;
import java.util.List;

public interface MenuService {

    void addDishToMenu(RequestMenu requestMenu);

    List<ResponseMenu> getMenuByDate(LocalDate serveDate);

    List<ResponseMenu> getListMenuForAccountant();

    void updatePriceOfDish(RequestEditPriceMenu requestEditPriceMenu);

}
