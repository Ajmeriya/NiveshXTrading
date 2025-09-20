package com.nivesh.service;

import com.nivesh.model.Coin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CoinService {

    List<Coin>getCoinList(int page) throws Exception;

    String getMarketChart(String CoinId,int days) throws Exception;

    String getCoinDetails(String CoinId) throws Exception;

    Coin findById(String CoinId) throws Exception;

    String searchCoin(String keyword) throws Exception;

    String getTop50CoinsByMarketCap();

    String getTradingCoins();
}
