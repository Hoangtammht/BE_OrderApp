package com.example.demo.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignClassResponse {
    private int classID;
    private String fullName;
    private String className;
    private int ageGroup;
    private int studentCount;

}
