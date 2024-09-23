package com.example.demo.dao;

import com.example.demo.domain.Class;
import com.example.demo.domain.response.AssignClassResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassMapper {

    List<Class> getAllClass();

    AssignClassResponse getInformationClassByClassID(int classID);

    void assignClass(int classID, int userID);

}
