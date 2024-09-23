package com.example.demo.dao;

import com.example.demo.domain.Schedule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduleMapper {

    List<Schedule> getListSchedule();

}
