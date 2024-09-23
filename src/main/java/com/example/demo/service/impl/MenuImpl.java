package com.example.demo.service.impl;

import com.example.demo.dao.MenuMapper;
import com.example.demo.domain.request.RequestMenu;
import com.example.demo.domain.response.ResponseMenu;
import com.example.demo.service.interf.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

    @Override
    public void addDishToMenu(RequestMenu requestMenu) {
        requestMenu.setServeDate(String.valueOf(LocalDateTime.now()));
        menuMapper.addDishToMenu(requestMenu);
    }

    @Override
    public List<ResponseMenu> getMenuByDate(LocalDate serveDate) {
        return menuMapper.getMenuByDate(serveDate);
    }
}
