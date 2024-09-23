package com.example.demo.service.interf;

import com.example.demo.domain.Class;
import com.example.demo.domain.request.AssignClassRequest;
import com.example.demo.domain.response.AssignClassResponse;

import java.util.List;

public interface ClassService {

    List<Class> getAllClass();

    AssignClassResponse assignClass(AssignClassRequest assignClassRequest);
}
