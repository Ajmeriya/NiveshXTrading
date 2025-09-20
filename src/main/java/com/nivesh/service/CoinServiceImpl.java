package com.nivesh.service;

import com.fasterxml.jackson.core.type.TypeReference; // ✅ correct import
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nivesh.model.Coin;
import com.nivesh.repository.CoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class CoinServiceImpl implements CoinService {

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<Coin> getCoinList(int page) throws Exception {
        // Step 1: Build API URL for fetching coin list (10 per page)
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=10&page=" + page;

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Step 2: Prepare request headers
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            // Step 3: Call API using RestTemplate
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Step 4: Convert JSON response into List<Coin>
            List<Coin> coinList = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<Coin>>() {}
            );

            // Step 5: Return the coin list
            return coinList;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Step 6: Handle client/server errors
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getCoinDetails(String coinId) throws Exception {
        // Step 1: Build API URL for fetching details of a specific coin
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Step 2: Prepare request headers
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            // Step 3: Call API
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Step 4: Parse JSON response
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            // Step 5: Create new Coin object and set values
            Coin coin = new Coin();
            coin.setId(jsonNode.get("id").asText());
            coin.setSymbol(jsonNode.get("symbol").asText());
            coin.setName(jsonNode.get("name").asText());
            coin.setImage(jsonNode.get("image").get("large").asText());

            // Step 6: Extract market data
            JsonNode marketData = jsonNode.get("market_data");
            coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
            coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
            coin.setMarketCapRank(marketData.get("market_cap_rank").asInt());
            coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
            coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());
            coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
            coin.setPriceChange24h(marketData.get("price_change_24h").asDouble());
            coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
            coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").asLong());
            coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asDouble());
            coin.setTotalSupply(marketData.get("total_supply").asLong());

            // Step 7: Save coin details in database
            coinRepository.save(coin);

            // Step 8: Return raw JSON response
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Step 9: Handle errors
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getMarketChart(String coinId, int days) throws Exception {
        // Step 1: Build API URL for fetching market chart of a coin
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId + "/market_chart?vs_currency=inr&days=" + days;

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Step 2: Prepare headers
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            // Step 3: Call API
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Step 4: Return JSON response
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Step 5: Handle errors
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Coin findById(String CoinId) throws Exception {
        // Step 1: Search coin in repository
        Optional<Coin> coin = coinRepository.findById(CoinId);

        // Step 2: If not found, throw exception
        if (coin.isEmpty()) {
            throw new Exception("coin not found");
        }

        // Step 3: Return found coin
        return coin.get();
    }

    @Override
    public String searchCoin(String keyword) throws Exception {
        // Step 1: Build API URL for searching coins by keyword
        String url = "https://api.coingecko.com/api/v3/search?query=" + keyword;

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Step 2: Prepare headers
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            // Step 3: Call API
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Step 4: Return JSON response
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Step 5: Handle errors
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getTop50CoinsByMarketCap() {
        // Step 1: Build API URL for fetching top 50 coins by market cap
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=50&page=1&sparkline=false";

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Step 2: Prepare headers
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            // Step 3: Call API
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Step 4: Return JSON response
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Step 5: Handle errors
            return e.getMessage();
        }
    }

    @Override
    public String getTradingCoins() {
        // Step 1: Build API URL for trending coins
        String url = "https://api.coingecko.com/api/v3/search/trending";

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Step 2: Prepare headers
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            // Step 3: Call API
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Step 4: Return JSON response
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Step 5: Handle errors
            return e.getMessage();
        }
    }
}
