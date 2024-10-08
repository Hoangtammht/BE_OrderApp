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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        requestMenu.setUserID(user.getUserID());
        Integer dishExists = menuMapper.checkDishInMenu(requestMenu);
        if (dishExists > 0) {
            menuMapper.updateDishQuantityInMenu(requestMenu);
        } else {
            menuMapper.addDishToMenu(requestMenu);
        }
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
    private int convertScheduleNameToId(String scheduleName) {
        switch (scheduleName.toLowerCase()) {
            case "buổi sáng":
                return 1;
            case "buổi trưa":
                return 2;
            case "buổi chiều":
                return 3;
            default:
                throw new IllegalArgumentException("Invalid schedule name: " + scheduleName);
        }
    }

    @Override
    public void importDishesFromExcel(MultipartFile file) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userMapper.findUserByUserName(authentication.getName());
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String dishName = row.getCell(0).getStringCellValue().trim();
                String scheduleName = row.getCell(1).getStringCellValue().trim();
                int scheduleID = convertScheduleNameToId(scheduleName);
                int quantity = (int) row.getCell(2).getNumericCellValue();

                String serveDateString;
                Cell serveDateCell = row.getCell(3);
                if (serveDateCell.getCellType() == CellType.NUMERIC) {
                    LocalDateTime date = serveDateCell.getLocalDateTimeCellValue();
                    serveDateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } else {
                    serveDateString = serveDateCell.getStringCellValue().trim();
                }
                LocalDate serveDate = LocalDate.parse(serveDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                RequestMenu requestMenu = new RequestMenu();
                requestMenu.setUserID(user.getUserID());
                requestMenu.setDishName(dishName);
                requestMenu.setScheduleID(scheduleID);
                requestMenu.setQuantity(quantity);
                requestMenu.setServeDate(String.valueOf(serveDate));
                Integer dishExists = menuMapper.checkDishInMenu(requestMenu);
                if (dishExists > 0) {
                    menuMapper.updateDishQuantityInMenu(requestMenu);
                } else {
                    menuMapper.addDishToMenu(requestMenu);
                }
            }
        } catch (IOException e) {
            throw new Exception("Failed to parse Excel file", e);
        }
    }


}
