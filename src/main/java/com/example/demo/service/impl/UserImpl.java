package com.example.demo.service.impl;

import com.example.demo.dao.UserMapper;
import com.example.demo.domain.User;
import com.example.demo.domain.UserRole;
import com.example.demo.domain.request.RequestAccount;
import com.example.demo.domain.request.RequestEditAccount;
import com.example.demo.domain.response.ResponseUser;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.RoleService;
import com.example.demo.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImpl implements UserDetailsService, UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findUserByUserName(username);
        if (user == null) {
            log.error("Không tìm thấy người dùng trong cơ sở dữ liệu: {}", username);
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("Đã tìm thấy người dùng trong cơ sở dữ liệu: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserRole userRole = roleService.findRoleByUserName(user.getUserName());
        authorities.add(new SimpleGrantedAuthority(userRole.getRoleName()));
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }

    @Override
    public User findUserByUserName(String userName) {
        try {
            User user = userMapper.findUserByUserName(userName);
            if (user == null) {
                log.warn("Không tìm thấy người dùng: {}", userName);
                throw new ApiRequestException("Tài khoản hoặc mật khẩu không chính xác");
            }
            if (user.getStatus() == 1) {
                log.warn("Tài khoản của người dùng đã bị khóa: {}", userName);
                throw new ApiRequestException("Tài khoản của bạn đã bị block");
            }
            log.info("Đã tìm thấy người dùng: {}", userName);
            return user;
        } catch (ApiRequestException e) {
            log.error("Lỗi khi tìm kiếm người dùng với tên đăng nhập: {}", userName, e);
            throw e;
        }
    }

    @Override
    public ResponseUser findResponseUserByUserName(String userName) {
        log.info("Đang tìm kiếm thông tin người dùng theo tài khoản: {}", userName);
        return userMapper.findResponseUserByUserName(userName);
    }

    @Override
    public void createAccount(RequestAccount user) throws Exception {
        log.info("Đang tạo tài khoản cho tên đăng nhập: {}", user.getUserName());
        if (userMapper.findUserByUserName(user.getUserName()) == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(String.valueOf(LocalDateTime.now()));
            userMapper.createAccount(user);
            log.info("Tài khoản đã được tạo thành công cho tên đăng nhập: {}", user.getUserName());
        } else {
            log.warn("Tài khoản đã tồn tại cho tên đăng nhập: {}", user.getUserName());
            throw new ApiRequestException("Tài khoản đã tồn tại. Vui lòng chọn tài khoản khác");
        }
    }

    @Override
    public List<ResponseUser> getListUserByRole(int roleID) {
        log.info("Đang lấy danh sách người dùng theo ID vai trò: {}", roleID);
        return userMapper.getListUserByRole(roleID);
    }

    @Override
    public void editAccount(RequestEditAccount requestEditAccount) {
        log.info("Đang chỉnh sửa tài khoản cho tên đăng nhập: {}", requestEditAccount.getUserName());
        try {
            User user = userMapper.findUserByUserName(requestEditAccount.getUserName());
            if (user != null) {
                requestEditAccount.setUserID(user.getUserID());
                requestEditAccount.setPassword(passwordEncoder.encode(requestEditAccount.getPassword()));
                requestEditAccount.setUpdatedAt(String.valueOf(LocalDateTime.now()));
                userMapper.editAccount(requestEditAccount);
                log.info("Tài khoản đã được cập nhật thành công cho tên đăng nhập: {}", requestEditAccount.getUserName());
            } else {
                log.warn("Không tìm thấy người dùng khi cố gắng chỉnh sửa: {}", requestEditAccount.getUserName());
                throw new ApiRequestException("User not found");
            }
        } catch (ApiRequestException e) {
            log.error("Lỗi khi chỉnh sửa tài khoản cho tên đăng nhập: {}", requestEditAccount.getUserName(), e);
            throw e;
        }
    }

    @Override
    public void deleteAccount(String userName) {
        log.info("Đang xóa tài khoản cho tên đăng nhập: {}", userName);
        User user = userMapper.findUserByUserName(userName);
        if (user != null) {
            userMapper.deleteAccount(user.getUserID());
            log.info("Tài khoản đã được xóa thành công cho tên đăng nhập: {}", userName);
        } else {
            log.warn("Không tìm thấy người dùng khi cố gắng xóa: {}", userName);
        }
    }

    @Override
    public void saveRefreshToken(int userID, String refresh_token) {
        log.info("Đang lưu mã thông báo làm mới cho ID người dùng: {}", userID);
        userMapper.saveRefreshToken(userID, refresh_token);
        log.info("Mã thông báo làm mới đã được lưu thành công cho ID người dùng: {}", userID);
    }

    @Override
    public boolean isRefreshTokenValid(int userID, String refresh_token) {
        log.info("Đang kiểm tra xem mã thông báo làm mới có hợp lệ cho ID người dùng: {}", userID);
        boolean isValid = userMapper.isRefreshTokenValid(userID, refresh_token);
        log.info("Tính hợp lệ của mã thông báo làm mới cho ID người dùng {}: {}", userID, isValid);
        return isValid;
    }

    public void deleteRefreshToken(int userID, String refreshToken) {
        log.info("Đang xóa mã thông báo làm mới cho ID người dùng: {}", userID);
        userMapper.deleteRefreshToken(userID, refreshToken);
        log.info("Mã thông báo làm mới đã được xóa thành công cho ID người dùng: {}", userID);
    }
}
