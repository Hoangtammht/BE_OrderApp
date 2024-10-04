package com.example.demo.controller;

import com.example.demo.domain.request.RequestEditPriceMenu;
import com.example.demo.domain.request.RequestMenu;
import com.example.demo.domain.response.ResponseMenu;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/addDishToMenu")
    public ResponseEntity<?> addDishToMenu(@RequestBody RequestMenu requestMenu){
        try {
            menuService.addDishToMenu(requestMenu);
            return ResponseEntity.ok().body("Add dish to menu");
        }catch (ApiRequestException e){
            throw e;
        }
    }


    @GetMapping("/getMenuByDate")
    public ResponseEntity<List<ResponseMenu>> getMenuByDate(@RequestParam("serveDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serveDate){
        try {
            List<ResponseMenu> menuList = menuService.getMenuByDate(serveDate);
            return ResponseEntity.ok().body(menuList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getListMenuForAccountant")
    public ResponseEntity<List<ResponseMenu>> getListMenuForAccountant(){
        try {
            List<ResponseMenu> menuList = menuService.getListMenuForAccountant();
            return ResponseEntity.ok().body(menuList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/updatePriceOfDish")
    public ResponseEntity<?> updatePriceOfDish(@RequestBody RequestEditPriceMenu requestEditPriceMenu){
        try {
            menuService.updatePriceOfDish(requestEditPriceMenu);
            return ResponseEntity.ok().body("Edit price of menu");
        }catch (ApiRequestException e){
            throw e;
        }
    }

}
