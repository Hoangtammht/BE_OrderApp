package com.example.demo.controller;

import com.example.demo.domain.Schedule;
import com.example.demo.service.interf.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/getListSchedule")
    public ResponseEntity<List<Schedule>> getListSchedule(){
        try {
            return ResponseEntity.ok(scheduleService.getListSchedule());
        }catch (Exception e) {
            throw e;
        }
    }

}
