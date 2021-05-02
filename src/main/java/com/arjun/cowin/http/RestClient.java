package com.arjun.cowin.http;

import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RestClient {

    private OkHttpClient httpClient;
    private String baseUrl;

    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient();
    }

    private OkHttpClient httpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(60, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(5, TimeUnit.MINUTES);
        okHttpClientBuilder.writeTimeout(60, TimeUnit.SECONDS);
        okHttpClientBuilder.connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES));
        return okHttpClientBuilder.build();
    }

    public String get(String uri, Map<String, String> queryParams) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(StringUtils.join(new String[]{baseUrl, uri})).newBuilder();
        if (MapUtils.isNotEmpty(queryParams)) {
            for (Map.Entry<String, String> entry : queryParams.entrySet())
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        String urlWithParams = urlBuilder.build().toString();
        Request request = new Request.Builder().url(urlWithParams).build();
        return executeRequest(request);
    }

    private String executeRequest(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : null;
        } catch (IOException e) {
            String errorMsg = "Error while connecting to url " + request.url().encodedPath() + ". Error occurred:" + e.getMessage();
            throw new RuntimeException(errorMsg);
        }
    }

}
