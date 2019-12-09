package com.example.relaxtodrinking.googlepay;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.relaxtodrinking.Common;
import com.example.relaxtodrinking.data.Order;
import com.example.relaxtodrinking.data.OrderItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ApiUtil {
    private static final String TAG = "TAG_ApiUtil";
    // 開啟MyTask (AsyncTask) 將交易資訊送至TapPay測試區
    @SuppressLint("DefaultLocale")
    public static String generatePayByPrimeCURLForSandBox(String prime, String partnerKey, String merchantId, Order order, String email, List<OrderItem> orderItems) {
        JSONObject paymentJO = new JSONObject();
        try {
            paymentJO.put("partner_key", partnerKey);
            paymentJO.put("prime", prime);
            paymentJO.put("merchant_id", merchantId);
            paymentJO.put("amount", 10);
            paymentJO.put("currency", "TWD");
            paymentJO.put("order_number", "SN0001");

            String details = "";
            for (OrderItem orderItem : orderItems)
            {
                details += String.format("『%s %d杯 %s %s %s』 ",orderItem.getPro_name(),orderItem.getPro_quantity(),orderItem.getPro_capacity(),orderItem.getPro_temperature(),orderItem.getPro_sweetness());
            }

            paymentJO.put("details",details );
            JSONObject cardHolderJO = new JSONObject();
            cardHolderJO.put("phone_number", order.getUser_phone());
            cardHolderJO.put("name", order.getUser_name());
            cardHolderJO.put("email", email);

            paymentJO.put("cardholder", cardHolderJO);
            Log.d(TAG, "paymentJO: " + paymentJO.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TapPay測試區網址
        String url = Common.TAPPAY_DOMAIN_SANDBOX + Common.TAPPAY_PAY_BY_PRIME_URL;
        MyTask myTask = new MyTask(url, paymentJO.toString(), partnerKey);
        String result = "";
        try {
            result = myTask.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }

}
