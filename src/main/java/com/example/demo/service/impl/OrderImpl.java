package com.example.demo.service.impl;

import com.example.demo.dao.ClassMapper;
import com.example.demo.dao.MenuMapper;
import com.example.demo.dao.OrderMapper;
import com.example.demo.dao.UserMapper;
import com.example.demo.domain.Class;
import com.example.demo.domain.User;
import com.example.demo.domain.request.RequestEditOrder;
import com.example.demo.domain.request.RequestOrder;
import com.example.demo.domain.request.RequestUpdateConfirm;
import com.example.demo.domain.response.ResponseMenu;
import com.example.demo.domain.response.ResponseOrder;
import com.example.demo.exception.ApiRequestException;
import com.example.demo.service.interf.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final MenuMapper menuMapper;
    private final UserMapper userMapper;
    private final ClassMapper classMapper;

    @Override
    public void createOrder(RequestOrder requestOrder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            log.info("Người dùng {} đang tạo đơn hàng: {}", authentication.getName(), requestOrder);
            ResponseMenu dishName = menuMapper.getMenuByID(requestOrder.getMenuID());
            if (dishName.getQuantity() < requestOrder.getQuantity()) {
                log.error("Món ăn không còn đủ số lượng để cung cấp: {}", dishName.getMenuID());
                throw new ApiRequestException("Món ăn không còn đủ số lượng để cung cấp.");
            }
            User user = userMapper.findUserByUserName(authentication.getName());
            Class classInfo = classMapper.getClassByUserID(user.getUserID());
            List<Class> allClass = classMapper.getAllClass();
            boolean userInClass = allClass.stream()
                    .anyMatch(c -> c.getUserID() == user.getUserID());
            if (!userInClass) {
                log.error("Giáo viên {} chưa được phân lớp.", user.getUserName());
                throw new ApiRequestException("Giáo viên chưa được phân lớp.");
            }
            requestOrder.setUserID(user.getUserID());
            requestOrder.setClassID(classInfo.getClassID());
            orderMapper.createOrder(requestOrder);
            int updatedQuantity = dishName.getQuantity() - requestOrder.getQuantity();
            menuMapper.updateQuantityOfMenu(requestOrder.getMenuID(), updatedQuantity);
            log.info("Đơn hàng đã được tạo thành công cho người dùng: {}", user.getUserName());
        } catch (ApiRequestException e) {
            log.error("Lỗi khi tạo đơn hàng: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<ResponseOrder> getOrdersByDateRange(String from, String to) {
        log.info("Yêu cầu lấy danh sách đơn hàng từ {} đến {}", from, to);
        return orderMapper.getOrdersByDateRange(from, to);
    }

    @Override
    public List<ResponseOrder> getOrdersByTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Người dùng {} yêu cầu lấy danh sách đơn hàng.", authentication.getName());
        return orderMapper.getOrdersByTeacher(authentication.getName());
    }

    @Override
    public void confirmOrder(RequestUpdateConfirm requestUpdateConfirm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (requestUpdateConfirm.getIsConfirm() == 2) {
                ResponseOrder order = orderMapper.getOrdersByOrderID(requestUpdateConfirm.getOrderID());
                ResponseMenu menu = menuMapper.getMenuByID(order.getMenuID());
                int updatedQuantity = menu.getQuantity() + order.getQuantity();
                menuMapper.updateQuantityOfMenu(order.getMenuID(), updatedQuantity);
                orderMapper.confirmOrder(requestUpdateConfirm);
                log.info("Đơn hàng {} đã được xác nhận hủy bỏ.", requestUpdateConfirm.getOrderID());
            } else if (requestUpdateConfirm.getIsConfirm() == 1) {
                ResponseOrder order = orderMapper.getOrdersByOrderID(requestUpdateConfirm.getOrderID());
                ResponseMenu menu = menuMapper.getMenuByID(order.getMenuID());
                double totalPrice = menu.getPrice() * order.getQuantity();
                orderMapper.updateTotalPrice(requestUpdateConfirm.getOrderID(), totalPrice);
                orderMapper.confirmOrder(requestUpdateConfirm);
                log.info("Đơn hàng {} đã được xác nhận thành công.", requestUpdateConfirm.getOrderID());
            } else {
                orderMapper.confirmOrder(requestUpdateConfirm);
                log.info("Đơn hàng {} đã được xác nhận.", requestUpdateConfirm.getOrderID());
            }
        } catch (ApiRequestException e) {
            log.error("Lỗi khi xác nhận đơn hàng: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void editOrder(RequestEditOrder requestEditOrder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            log.info("Người dùng {} yêu cầu chỉnh sửa đơn hàng: {}", authentication.getName(), requestEditOrder);
            ResponseMenu newDish = menuMapper.getMenuByID(requestEditOrder.getMenuID());
            int currentNewQuantity = newDish.getQuantity();
            ResponseOrder responseOrder = orderMapper.getOrdersByOrderID(requestEditOrder.getOrderID());
            ResponseMenu oldDish = menuMapper.getMenuByID(responseOrder.getMenuID());

            if (newDish.getMenuID() == oldDish.getMenuID()) {
                int previousQuantity = responseOrder.getQuantity();
                int quantityChange = requestEditOrder.getQuantity() - previousQuantity;
                if (currentNewQuantity < quantityChange) {
                    log.error("Món ăn không còn đủ số lượng để cung cấp: {}", newDish.getDishName());
                    throw new ApiRequestException("Món ăn không còn đủ số lượng để cung cấp.");
                }
                User user = userMapper.findUserByUserName(authentication.getName());
                Class classInfo = classMapper.getClassByUserID(user.getUserID());
                requestEditOrder.setUserID(user.getUserID());
                requestEditOrder.setClassID(classInfo.getClassID());
//                requestEditOrder.setTotalPrice(newDish.getPrice() * requestEditOrder.getQuantity());
                int updatedQuantity = currentNewQuantity - quantityChange;
                menuMapper.updateQuantityOfMenu(requestEditOrder.getMenuID(), updatedQuantity);
                orderMapper.editOrder(requestEditOrder);
            } else {
                int updatedOldQuantity = oldDish.getQuantity() + responseOrder.getQuantity();
                menuMapper.updateQuantityOfMenu(oldDish.getMenuID(), updatedOldQuantity);
                if (currentNewQuantity < requestEditOrder.getQuantity()) {
                    log.error("Món ăn không còn đủ số lượng để cung cấp: {}", newDish.getDishName());
                    throw new ApiRequestException("Món ăn không còn đủ số lượng để cung cấp.");
                }
                User user = userMapper.findUserByUserName(authentication.getName());
                Class classInfo = classMapper.getClassByUserID(user.getUserID());
                requestEditOrder.setUserID(user.getUserID());
                requestEditOrder.setClassID(classInfo.getClassID());
//                requestEditOrder.setTotalPrice(newDish.getPrice() * requestEditOrder.getQuantity());
                int editQuantity = currentNewQuantity - requestEditOrder.getQuantity();
                menuMapper.updateQuantityOfMenu(requestEditOrder.getMenuID(), editQuantity);
                orderMapper.editOrder(requestEditOrder);
                log.info("Đơn hàng đã được chỉnh sửa thành công cho người dùng: {}", user.getUserName());
            }
        } catch (ApiRequestException e) {
            log.error("Lỗi khi chỉnh sửa đơn hàng: {}", e.getMessage());
            throw e;
        }
    }


}
