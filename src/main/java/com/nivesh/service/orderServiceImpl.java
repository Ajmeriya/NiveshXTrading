package com.nivesh.service;

import com.nivesh.domain.OrderStatus;
import com.nivesh.domain.OrderType;
import com.nivesh.model.Coin;
import com.nivesh.model.Order;
import com.nivesh.model.OrderItem;
import com.nivesh.model.User;
import com.nivesh.repository.OrderItemRepositoty;
import com.nivesh.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;

@Service
public class orderServiceImpl implements orderService{


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepositoty orderItemRepositoty;

    @Autowired
    private WalletService walletService;

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType)
    {
        double price=orderItem.getCoin().getCurrentPrice()* orderItem.getQuantity();


        Order order=new Order();
        order.setOrderItem(orderItem);
        order.setPrice(BigDecimal.valueOf(price));
        order.setOrderType(orderType);
        order.setUser(user);
        order.setTimeStamp(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) throws Exception {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("order not found"));
    }


    @Override
    public List<Order> getAllOrderOfUser(Long userId, Order orderType, String assestSymbol) {
        return orderRepository.findByUserId(userId);

    }


    private OrderItem creatOrderItem(Coin coin,double quatity,double buyPrice,double sellPrice)
    {
        OrderItem orderItem=new OrderItem();

        orderItem.setCoin(coin);
        orderItem.setQuantity(quatity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        orderItem.setQuantity(quatity);

        return orderItemRepositoty.save(orderItem);
    }


    @Transactional
    public Order buyAsset(Coin coin,double quantity,User user) throws Exception {
        if(quantity<=0)
        {
            throw new Exception("quantity should be more then o");
        }

        double buyPrice=coin.getCurrentPrice();

        OrderItem orderItem=creatOrderItem(coin,quantity,buyPrice,0);

        Order order=createOrder(user,orderItem,OrderType.BUY);
        orderItem.setOrder(order);

        walletService.payOrderPayment(order,user);

        order.setOrderStatus(OrderStatus.SUCCESFULL);
        order.setOrderType(OrderType.BUY);

        Order saveOrder=orderRepository.save(order);



        //  create asset

        return saveOrder;

    }


    @Transactional
    public Order sellAsset(Coin coin,double quantity,User user) throws Exception {
        if(quantity<=0)
        {
            throw new Exception("quantity should be more then o");
        }

        double sellPrice=coin.getCurrentPrice();

        OrderItem orderItem=creatOrderItem(coin,quantity,0,sellPrice);

        Order order=createOrder(user,orderItem,OrderType.SELL);
        orderItem.setOrder(order);

        if(assetToSell.getQuntity()>=quantity)
        {
            walletService.payOrderPayment(order,user);

            Asset updatedAsset=assetSerice.updateAsset(assetTosell.getI(),-quantity);
        }

        order.setOrderStatus(OrderStatus.SUCCESFULL);
        order.setOrderType(OrderType.SELL);

        Order saveOrder=orderRepository.save(order);



        //  create asset

        return saveOrder;

    }


    @Override
    @Transactional
    public Order processOrder(Coin coin, double quanlity, OrderType orderType, User user) throws Exception {

        if(orderType.equals(OrderType.BUY))
        {
            return buyAsset(coin,quanlity,user);
        }
        else if(orderType.equals(OrderType.SELL))
        {
            return sellAsset(coin,quanlity,user);
        }
        throw new Exception("invelid order type");
    }
}
