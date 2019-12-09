package com.example.relaxtodrinking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.relaxtodrinking.ShoppingcartListFragment.paymentData;

public class MainActivity extends AppCompatActivity {

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // 取得支付資訊
                    paymentData = PaymentData.getFromIntent(data);
                    if (paymentData != null) {
                        showPaymentInfo(paymentData);
                        ShoppingcartListFragment.btGooglePay_ShoppingCart.setVisibility(View.GONE);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Common.showToast(this,"使用者關閉視窗");
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    if (status != null) {
                        String text = "錯誤代碼: " + status.getStatusCode() +
                                " , 原因: " + status.getStatusMessage();
                        Common.showToast(this,text);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    private void showPaymentInfo(PaymentData paymentData) {
        try {
            JSONObject paymentDataJO = new JSONObject(paymentData.toJson());
            String cardDescription = paymentDataJO.getJSONObject("paymentMethodData").getString
                    ("description");
            Common.showToast(this,cardDescription);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
