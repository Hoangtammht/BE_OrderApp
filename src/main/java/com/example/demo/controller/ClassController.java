package com.example.demo.controller;

import com.example.demo.domain.Class;
import com.example.demo.domain.request.AssignClassRequest;
import com.example.demo.domain.response.AssignClassResponse;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.ClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
@Slf4j
public class ClassController {

    private final ClassService classService;

    @GetMapping("/getAllClass")
    public ResponseEntity<List<Class>> getAllClass() {
        try {
            log.info("Đang lấy danh sách tất cả các lớp học.");
            List<Class> classes = classService.getAllClass();
            log.info("Đã lấy thành công {} lớp học.", classes.size());
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách lớp học: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/assignClass")
    public ResponseEntity<AssignClassResponse> assignClass(@RequestBody AssignClassRequest assignClassRequest) {
        try {
            log.info("Đang gán lớp học cho người dùng: {}", assignClassRequest.getUserName());
            AssignClassResponse response = classService.assignClass(assignClassRequest);
            log.info("Gán lớp học thành công cho người dùng: {}", assignClassRequest.getUserName());
            return ResponseEntity.ok(response);
        } catch (ApiRequestException e) {
            log.error("Lỗi khi gán lớp học: {}", e.getMessage());
            throw e;
        }
    }

}
