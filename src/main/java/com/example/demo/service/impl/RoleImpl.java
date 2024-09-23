package com.example.demo.service.impl;

import com.example.demo.dao.RoleMapper;
import com.example.demo.domain.Role;
import com.example.demo.domain.UserRole;
import com.example.demo.service.interf.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleImpl implements RoleService {
    private final RoleMapper roleMapper;

    @Override
    public UserRole findRoleByUserName(String phoneNumber) {
        return roleMapper.findRoleByUserName(phoneNumber);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleMapper.getAllRoles();
    }
}
