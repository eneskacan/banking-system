package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.util.HttpUtil;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
@Qualifier("CollectApiExchangeRepository")
public class CollectApiExchangeRepository implements IExchangeRepository{

    @Value("${collect.api.token}")
    private String accessToken;

    @Override
    public double getUsdTryExchangeRate() throws JSONException, IOException {
        String url = "https://api.collectapi.com/economy/singleCurrency?int=1&tag=USD";
        return HttpUtil.sendGetRequest(url, accessToken)
                .getJSONArray("result")
                .getJSONObject(0)
                .getDouble("selling");
    }

    @Override
    public double getXauTryExchangeRate() throws JSONException, IOException {
        String url = "https://api.collectapi.com/economy/goldPrice";
        return HttpUtil.sendGetRequest(url, accessToken)
                .getJSONArray("result")
                .getJSONObject(0)
                .getDouble("selling");
    }
}
