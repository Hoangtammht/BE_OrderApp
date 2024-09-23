package com.example.demo.service.interf;

import com.example.demo.domain.Role;
import com.example.demo.domain.UserRole;

import java.util.List;

public interface RoleService {

    UserRole findRoleByUserName(String userName);

    List<Role> getAllRoles();

}
