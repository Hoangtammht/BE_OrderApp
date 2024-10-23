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
import lombok.extern.slf4j.Slf4j; // Import Slf4j
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        log.info("Đang kiểm tra xem món ăn có trong thực đơn không: {}", requestMenu.getDishName());
        Integer dishExists = menuMapper.checkDishInMenu(requestMenu);
        if (dishExists > 0) {
            log.info("Món ăn đã có, đang cập nhật số lượng: {}", requestMenu.getDishName());
            menuMapper.updateDishQuantityInMenu(requestMenu);
        } else {
            log.info("Món ăn không có, đang thêm vào thực đơn: {}", requestMenu.getDishName());
            menuMapper.addDishToMenu(requestMenu);
        }
    }

    @Override
    public List<ResponseMenu> getMenuByDate(LocalDate serveDate) {
        log.info("Đang lấy thực đơn cho ngày: {}", serveDate);
        return menuMapper.getMenuByDate(serveDate);
    }

    @Override
    public List<ResponseMenu> getListMenuForAccountant() {
        log.info("Đang lấy danh sách thực đơn cho kế toán");
        return menuMapper.getListMenuForAccountant();
    }

    @Override
    public void updatePriceOfDish(RequestEditPriceMenu requestEditPriceMenu) {
        try {
            menuMapper.updatePriceOfDish(requestEditPriceMenu);
        } catch (ApiRequestException e) {
            log.error("Lỗi khi cập nhật giá cho món ăn: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void importDishesFromExcel(MultipartFile file) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userMapper.findUserByUserName(authentication.getName());
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            log.info("Đang nhập các món ăn từ file Excel: {}", file.getOriginalFilename());
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
                log.info("Đang kiểm tra xem món ăn có trong thực đơn không: {}", dishName);
                Integer dishExists = menuMapper.checkDishInMenu(requestMenu);
                if (dishExists > 0) {
                    log.info("Món ăn đã có, đang cập nhật số lượng: {}", dishName);
                    menuMapper.updateDishQuantityInMenu(requestMenu);
                } else {
                    log.info("Món ăn không có, đang thêm vào thực đơn: {}", dishName);
                    menuMapper.addDishToMenu(requestMenu);
                }
            }
            log.info("Đã nhập thành công món ăn từ file: {}", file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Lỗi khi nhập món ăn từ file Excel: {}", e.getMessage());
            throw new ApiRequestException("Lỗi khi nhập món ăn từ file Excel");
        }
    }

    private int convertScheduleNameToId(String scheduleName) {
        switch (scheduleName.toLowerCase()) {
            case "buổi sáng":
                return 1;
            case "buổi trưa":
                return 2;
            case "buổi tối":
                return 3;
            default:
                return 0;
        }
    }
}
