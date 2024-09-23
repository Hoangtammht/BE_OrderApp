package com.example.demo.dao;

import com.example.demo.domain.Role;
import com.example.demo.domain.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    UserRole findRoleByUserName(String userName);

    List<Role> getAllRoles();

}
