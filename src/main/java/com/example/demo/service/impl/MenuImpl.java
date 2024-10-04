package com.example.demo.service.impl;

import com.example.demo.dao.MenuMapper;
import com.example.demo.dao.UserMapper;
import com.example.demo.domain.User;
import com.example.demo.domain.request.RequestEditPriceMenu;
import com.example.demo.domain.request.RequestMenu;
import com.example.demo.domain.response.ResponseMenu;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final UserMapper userMapper;


    @Override
    public void addDishToMenu(RequestMenu requestMenu) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userMapper.findUserByUserName(authentication.getName());
        requestMenu.setUserID(user.getUserID());
        menuMapper.addDishToMenu(requestMenu);
    }

    @Override
    public List<ResponseMenu> getMenuByDate(LocalDate serveDate) {
        return menuMapper.getMenuByDate(serveDate);
    }

    @Override
    public List<ResponseMenu> getListMenuForAccountant() {
        return menuMapper.getListMenuForAccountant();
    }

    @Override
    public void updatePriceOfDish(RequestEditPriceMenu requestEditPriceMenu) {
        try {
            menuMapper.updatePriceOfDish(requestEditPriceMenu);
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
