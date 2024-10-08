package com.example.demo.service.impl;

import com.example.demo.dao.ClassMapper;
import com.example.demo.dao.UserMapper;
import com.example.demo.domain.Class;
import com.example.demo.domain.User;
import com.example.demo.domain.request.AssignClassRequest;
import com.example.demo.domain.response.AssignClassResponse;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.ClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClassImpl implements ClassService {

    private final ClassMapper classMapper;
    private final UserMapper userMapper;

    @Override
    public List<Class> getAllClass() {
        return classMapper.getAllClass();
    }

    @Override
    public AssignClassResponse assignClass(AssignClassRequest assignClassRequest) {
        User user = userMapper.findUserByUserName(assignClassRequest.getUserName());
        List<Class> allClass = classMapper.getAllClass();
        boolean userInClass = allClass.stream()
                .anyMatch(c -> c.getUserID() == user.getUserID());
        if (userInClass) {
            Class currentClass = classMapper.getClassByUserID(user.getUserID());
            classMapper.clearUserIDFromClass(currentClass.getClassID());
            classMapper.assignClass(user.getUserID(), assignClassRequest.getClassID());
        }else {
            classMapper.assignClass(user.getUserID(), assignClassRequest.getClassID());
        }
        return classMapper.getInformationClassByClassID(assignClassRequest.getClassID());
    }
}
