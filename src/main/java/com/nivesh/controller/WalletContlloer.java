package com.nivesh.controller;


import com.nivesh.model.Order;
import com.nivesh.model.User;
import com.nivesh.model.Wallet;
import com.nivesh.model.WalletTransaction;
import com.nivesh.repository.WalletRepository;
import com.nivesh.service.UserService;
import com.nivesh.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletContlloer {


    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;



    @GetMapping("/api/wallet")
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization")String jwt) throws Exception {

        User user=userService.findUserByJwt(jwt);

        Wallet wallet=walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/wallet/{walletId}/transfer")
   public ResponseEntity<Wallet> walletToWalletTranfer(@RequestHeader("Authorization")String jwt,
                                                       @PathVariable Long walletId,
                                                       @RequestBody WalletTransaction walletTransaction) throws Exception {
       User senderUser = userService.findUserByJwt(jwt);

       Wallet reciverWallet = walletService.findWalletById(walletId);
//       Wallet senderWallet = walletService.getUserWallet(senderUser);

       Wallet wallet = walletService.walletTOWalletTransfer(senderUser, reciverWallet, walletTransaction.getAmount());

       return new ResponseEntity<>(wallet,HttpStatus.OK);
   }

    @PutMapping("/api/wallet/oder/{orderId}/pay")
    public ResponseEntity<Wallet> payOrderPayment(@RequestHeader("Authorization")String jwt,
                                                        @PathVariable Long orderId) throws Exception {

        User senderUser = userService.findUserByJwt(jwt);
        Order order =OrderService.getOrderById(orderId);


        Wallet wallet=walletService.payOrderPayment(order,senderUser);

        return new ResponseEntity<>(wallet,HttpStatus.OK);
    }




}
