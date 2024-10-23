package com.example.demo.controller;

import com.example.demo.domain.request.RequestEditPriceMenu;
import com.example.demo.domain.request.RequestMenu;
import com.example.demo.domain.response.ResponseMenu;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/addDishToMenu")
    public ResponseEntity<?> addDishToMenu(@RequestBody RequestMenu requestMenu) {
        try {
            log.info("Đang thêm món ăn vào thực đơn: {}", requestMenu);
            menuService.addDishToMenu(requestMenu);
            log.info("Món ăn đã được thêm thành công: {}", requestMenu.getDishName());
            return ResponseEntity.ok().body("Đã thêm món ăn vào thực đơn");
        } catch (ApiRequestException e) {
            log.error("Lỗi khi thêm món ăn vào thực đơn: {}", e.getMessage());
            throw e;
        }
    }


    @GetMapping("/getMenuByDate")
    public ResponseEntity<List<ResponseMenu>> getMenuByDate(@RequestParam("serveDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serveDate) {
        try {
            log.info("Đang lấy thực đơn cho ngày: {}", serveDate);
            List<ResponseMenu> menuList = menuService.getMenuByDate(serveDate);
            log.info("Đã lấy {} món cho ngày: {}", menuList.size(), serveDate);
            return ResponseEntity.ok().body(menuList);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thực đơn theo ngày: {}: {}", serveDate, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getListMenuForAccountant")
    public ResponseEntity<List<ResponseMenu>> getListMenuForAccountant() {
        try {
            log.info("Đang lấy danh sách thực đơn cho kế toán");
            List<ResponseMenu> menuList = menuService.getListMenuForAccountant();
            log.info("Đã lấy {} món cho kế toán", menuList.size());
            return ResponseEntity.ok().body(menuList);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách thực đơn cho kế toán: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/updatePriceOfDish")
    public ResponseEntity<?> updatePriceOfDish(@RequestBody RequestEditPriceMenu requestEditPriceMenu) {
        try {
            log.info("Đang cập nhật giá cho món ăn: {}", requestEditPriceMenu.getMenuID());
            menuService.updatePriceOfDish(requestEditPriceMenu);
            log.info("Giá đã được cập nhật thành công cho món ăn: {}", requestEditPriceMenu.getMenuID());
            return ResponseEntity.ok().body("Đã chỉnh sửa giá của thực đơn");
        } catch (ApiRequestException e) {
            log.error("Lỗi khi cập nhật giá cho món ăn: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/importExcel")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Đang nhập các món ăn từ file Excel: {}", file.getOriginalFilename());
            menuService.importDishesFromExcel(file);
            log.info("Các món ăn đã được thêm vào thực đơn thành công từ file: {}", file.getOriginalFilename());
            return ResponseEntity.ok().body("Các món ăn đã được thêm vào thực đơn thành công.");
        } catch (ApiRequestException e) {
            log.error("Lỗi khi nhập các món ăn từ Excel: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi nhập các món ăn từ Excel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
