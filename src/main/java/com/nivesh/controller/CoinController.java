package com.nivesh.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nivesh.model.Coin;
import com.nivesh.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {


    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<Coin>>getCoinList(@RequestParam("page")int page) throws Exception {
        List<Coin> coins=coinService.getCoinList(page);
        return new ResponseEntity<>(coins,HttpStatus.ACCEPTED);
    }

    @GetMapping("/{coinId}chart")
    ResponseEntity<JsonNode>getMarketChart(
            @PathVariable String coinId,
            @RequestParam("days")int days
    ) throws Exception {
        String response=coinService.getMarketChart(coinId,days);
        JsonNode jsonNode=objectMapper.readTree(response);

        return new ResponseEntity<>(jsonNode,HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws Exception
    {
        // ✅ Call service to search coin using keyword
        String coin = coinService.searchCoin(keyword);

        // ✅ Convert JSON string to JsonNode
        JsonNode jsonNode = objectMapper.readTree(coin);

        return ResponseEntity.ok(jsonNode); // ✅ Return search result
    }

    // 👉 Get top 50 coins sorted by market cap
    @GetMapping("/top50")
    ResponseEntity<JsonNode> getTop50CoinByMarketCapRank() throws JsonProcessingException
    {
        // ✅ Call service to get top 50 coins
        String coin = coinService.getTop50CoinsByMarketCap();

        // ✅ Parse the JSON string to JsonNode
        JsonNode jsonNode = objectMapper.readTree(coin);

        return ResponseEntity.ok(jsonNode); // ✅ Return top 50 list
    }

    // 👉 Get trending coins
    @GetMapping("/trading")
    ResponseEntity<JsonNode> getTreadingCoin() throws JsonProcessingException
    {
        // ✅ Call service to get trending coins
        String coin = coinService.getTradingCoins();

        // ✅ Parse the JSON response into JsonNode
        JsonNode jsonNode = objectMapper.readTree(coin);

        return ResponseEntity.ok(jsonNode); // ✅ Return trending coins list
    }

    // 👉 Get full details of a specific coin by ID
    @GetMapping("/details/{coinId}")
    ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception
    {
        // ✅ Call service to fetch detailed info of the coin
        String coin = coinService.getCoinDetails(coinId);

        // ✅ Convert raw JSON string to JsonNode
        JsonNode jsonNode = objectMapper.readTree(coin);

        return ResponseEntity.ok(jsonNode); // ✅ Return coin details
    }
}
