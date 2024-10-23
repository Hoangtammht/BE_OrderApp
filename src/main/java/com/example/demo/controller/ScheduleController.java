package com.example.demo.controller;

import com.example.demo.domain.Schedule;
import com.example.demo.service.interf.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/getListSchedule")
    public ResponseEntity<List<Schedule>> getListSchedule() {
        try {
            log.info("Đang lấy danh sách lịch trình");
            return ResponseEntity.ok(scheduleService.getListSchedule());
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách lịch trình", e);
            throw e;
        }
    }

}
