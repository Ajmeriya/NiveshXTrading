package com.nivesh.service;

import com.nivesh.domain.OrderType;
import com.nivesh.model.User;
import com.nivesh.model.Wallet;
import com.nivesh.repository.WalletRepository;
import jakarta.persistence.criteria.Expression;
//import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;
import com.nivesh.model.Order;// Use your domain Order class


@Service
public class WalletServiceImpl implements WalletService {


    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet getUserWallet(User user) {

        Wallet wallet=walletRepository.findByUserId(user.getId());
        if(wallet==null)
        {
            wallet=new Wallet();

            wallet.setUser(user);
        }
        return wallet;
    }

    @Override
    public Wallet addBalanceToWallet(Wallet wallet, Double amount) {
            BigDecimal CurrentBalance=wallet.getBalance();
            BigDecimal newBalance=CurrentBalance.add(BigDecimal.valueOf(amount));

            wallet.setBalance(newBalance);

            return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletById(Long id) throws Exception {
        Optional<Wallet> wallet=walletRepository.findById(id);

        if(wallet.isPresent())
        {
            return wallet.get();
        }

        throw new Exception("Wallet Not Found");

    }

    @Override
    public Wallet walletTOWalletTransfer(User sender, Wallet reciverWallet, Double amount) throws Exception {

        Wallet walletOfSender=walletRepository.findByUserId(sender.getId());

        BigDecimal senderCurrentBalance=walletOfSender.getBalance();
        BigDecimal reciverCurrentBalnce=reciverWallet.getBalance();


        if(senderCurrentBalance.compareTo(BigDecimal.valueOf(amount)) > 0)
        {
            BigDecimal senderNewBalance=senderCurrentBalance.subtract(BigDecimal.valueOf(amount));
            BigDecimal reciverNewBalance=reciverCurrentBalnce.add(BigDecimal.valueOf(amount));

            walletOfSender.setBalance(senderNewBalance);
            reciverWallet.setBalance(reciverNewBalance);

            walletRepository.save(walletOfSender);
            walletRepository.save(reciverWallet);
            return walletOfSender;
        }

        throw new Exception("Not Sufficient Balance");
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {

        Wallet wallet=walletRepository.findByUserId(user.getId());

        if(order.getOrderType().equals(OrderType.BUY))
        {
            BigDecimal newBalance=wallet.getBalance().subtract(order.getPrice());

            if(newBalance.compareTo(order.getPrice())<0)
            {
                throw new Exception("Insufficient funds for this transaction");
            }

            wallet.setBalance(newBalance);
        }
        else
        {
            BigDecimal newBalance=wallet.getBalance().add(order.getPrice());
            wallet.setBalance(newBalance);
        }

        walletRepository.save(wallet);

        return wallet;

    }

}
