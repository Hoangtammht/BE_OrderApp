package com.example.demo.service.impl;

import com.example.demo.dao.ClassMapper;
import com.example.demo.domain.Class;
import com.example.demo.domain.request.AssignClassRequest;
import com.example.demo.domain.response.AssignClassResponse;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.ClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassImpl implements ClassService {

    private final ClassMapper classMapper;

    @Override
    public List<Class> getAllClass() {
        return classMapper.getAllClass();
    }

    @Override
    public AssignClassResponse assignClass(AssignClassRequest assignClassRequest) {
        if(assignClassRequest.getRoleID() != 2){
            throw  new ApiRequestException("User is not teacher");
        }else{
            classMapper.assignClass(assignClassRequest.getClassID(), assignClassRequest.getClassID());
        }
        return classMapper.getInformationClassByClassID(assignClassRequest.getClassID());
    }
}
