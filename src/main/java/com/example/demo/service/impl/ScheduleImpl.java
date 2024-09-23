package com.example.demo.service.impl;

import com.example.demo.dao.ScheduleMapper;
import com.example.demo.domain.Schedule;
import com.example.demo.service.interf.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;

    @Override
    public List<Schedule> getListSchedule() {
        return scheduleMapper.getListSchedule();
    }


}
