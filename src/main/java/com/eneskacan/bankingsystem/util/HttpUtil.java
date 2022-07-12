package com.eneskacan.bankingsystem.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

    private static HttpURLConnection httpConn;

    private HttpUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static void establishGetConnection(String requestUrl, String accessToken) throws IOException {
        URL url = new URL(requestUrl);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
        httpConn.setRequestProperty(HttpHeaders.AUTHORIZATION, accessToken);
    }

    public static JSONObject sendGetRequest(String requestUrl, String accessToken) throws JSONException, IOException {
        establishGetConnection(requestUrl, accessToken);

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream()))) {
            String inputLine;
            while ((inputLine = br.readLine()) != null) response.append(inputLine);
        }

        return new JSONObject(response.toString());
    }
}
