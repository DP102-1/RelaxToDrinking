package com.example.relaxtodrinking;
/***************************************************************/

//找不到action Bar
/***************************************************************/

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.relaxtodrinking.qrcode.Contents;
import com.example.relaxtodrinking.qrcode.QRCodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;


public class OrderQRCodeActivity extends AppCompatActivity {
    private String TAG = "訂單QRCode";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private ImageView ivOrderQRCode_OrderQRcode,ivExit_OrderQRcode;
    String order_id = "";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋訂單資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void loadOrderData() {
        Intent QRcodeIntent = getIntent();
        order_id = QRcodeIntent.getStringExtra("order_id");
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋訂單資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_qrcode);
    //    getSupportActionBar().hide();
     //   getActionBar().hide();
//        setTitle("訂單QRCode");
        setTitle("");

        loadOrderData();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝產生訂單QRCode並顯示＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivOrderQRCode_OrderQRcode = findViewById(R.id.ivOrderQRCode_OrderQRcode);
        int dimension = getResources().getDisplayMetrics().widthPixels;
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(order_id, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                dimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ivOrderQRCode_OrderQRcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e(TAG, e.toString());
        }
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝產生訂單QRCode並顯示＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_OrderQRcode = findViewById(R.id.ivExit_OrderQRcode);
        ivExit_OrderQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }
}
