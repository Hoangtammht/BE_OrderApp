package com.example.demo.controller;

import com.example.demo.domain.Role;
import com.example.demo.service.interf.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/getAllRoles")
    public ResponseEntity<List<Role>> getAllRoles(HttpServletRequest request) {
        try {
            log.info("Đang lấy tất cả vai trò");
            return ResponseEntity.ok(roleService.getAllRoles());
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả vai trò", e);
            throw e;
        }
    }
}
