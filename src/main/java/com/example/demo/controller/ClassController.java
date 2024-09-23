package com.example.demo.controller;

import com.example.demo.domain.Class;
import com.example.demo.domain.request.AssignClassRequest;
import com.example.demo.domain.response.AssignClassResponse;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping("/getAllClass")
    public ResponseEntity<List<Class>> getAllClass(){
        try {
            return ResponseEntity.ok(classService.getAllClass());
        }catch (Exception e) {
            throw e;
        }
    }

    @PutMapping("/assignClass")
    public ResponseEntity<AssignClassResponse> assignClass(@RequestBody AssignClassRequest assignClassRequest){
        try{
            return ResponseEntity.ok(classService.assignClass(assignClassRequest));
        }catch (ApiRequestException e){
            throw e;
        }
    }

}
