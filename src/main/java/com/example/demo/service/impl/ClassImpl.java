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
        log.info("Đang lấy tất cả các lớp học từ cơ sở dữ liệu.");
        List<Class> classes = classMapper.getAllClass();
        log.info("Đã lấy {} lớp học từ cơ sở dữ liệu.", classes.size());
        return classes;
    }

    @Override
    public AssignClassResponse assignClass(AssignClassRequest assignClassRequest) {
        User user = userMapper.findUserByUserName(assignClassRequest.getUserName());
        if (user == null) {
            log.error("Không tìm thấy người dùng với tên: {}", assignClassRequest.getUserName());
            throw new ApiRequestException("Không tìm thấy người dùng.");
        }
        List<Class> allClass = classMapper.getAllClass();
        boolean userInClass = allClass.stream()
                .anyMatch(c -> c.getUserID() == user.getUserID());
        if (userInClass) {
            log.info("Người dùng đã có lớp học. Đang xóa lớp học hiện tại.");
            Class currentClass = classMapper.getClassByUserID(user.getUserID());
            classMapper.clearUserIDFromClass(currentClass.getClassID());
            classMapper.assignClass(user.getUserID(), assignClassRequest.getClassID());
        }else {
            log.info("Đang gán lớp học với ID: {}", assignClassRequest.getClassID());
            classMapper.assignClass(user.getUserID(), assignClassRequest.getClassID());
        }
        log.info("Đã gán lớp học thành công với ID: {}", assignClassRequest.getClassID());
        return classMapper.getInformationClassByClassID(assignClassRequest.getClassID());
    }
}
