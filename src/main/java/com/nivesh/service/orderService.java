package com.nivesh.service;

import com.nivesh.domain.OrderType;
import com.nivesh.model.Coin;
import com.nivesh.model.Order;
import com.nivesh.model.OrderItem;
import com.nivesh.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface orderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId) throws Exception;

    List<Order> getAllOrderOfUser(Long userId,Order orderType,String assestSymbol);

    Order processOrder(Coin coin,double quanlity,OrderType orderType,User user) throws Exception;

}
