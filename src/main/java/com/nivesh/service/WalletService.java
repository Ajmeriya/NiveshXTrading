package com.nivesh.service;

import com.nivesh.model.Order;
import com.nivesh.model.User;
import com.nivesh.model.Wallet;
import org.springframework.stereotype.Service;

@Service
public interface WalletService {

    Wallet getUserWallet(User user);

    Wallet addBalanceToWallet(Wallet wallet,Double amount);

    Wallet findWalletById(Long id) throws Exception;

    Wallet walletTOWalletTransfer(User sender,Wallet reciverWallet,Double amount) throws Exception;

    Wallet payOrderPayment(Order order, User user) throws Exception;
}
