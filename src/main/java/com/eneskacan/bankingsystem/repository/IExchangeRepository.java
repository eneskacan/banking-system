package com.eneskacan.bankingsystem.repository;

import org.json.JSONException;

import java.io.IOException;

public interface IExchangeRepository {
    double getUsdTryExchangeRate() throws JSONException, IOException;
    double getXauTryExchangeRate() throws JSONException, IOException;
}
