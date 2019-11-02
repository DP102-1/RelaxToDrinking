package com.example.relaxtodrinking;

/***************************************************************/
//清空購物車功能
//取餐時間小於系統時間
/***************************************************************/

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.relaxtodrinking.data.Order;
import com.example.relaxtodrinking.data.OrderItem;
import com.example.relaxtodrinking.data.User;
import com.example.relaxtodrinking.googlepay.ApiUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.cherri.tpdirect.api.TPDCardInfo;
import tech.cherri.tpdirect.api.TPDConsumer;
import tech.cherri.tpdirect.api.TPDGooglePay;
import tech.cherri.tpdirect.api.TPDMerchant;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDGooglePayListener;
import tech.cherri.tpdirect.callback.TPDTokenFailureCallback;
import tech.cherri.tpdirect.callback.TPDTokenSuccessCallback;

import static android.content.Context.MODE_PRIVATE;
import static com.example.relaxtodrinking.Common.CARD_TYPES;

public class ShoppingcartListFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {
    private String TAG = "購物車";
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 101;

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private SharedPreferences preferences_shoppingCart;
    private SharedPreferences preferences_user;
    private TPDGooglePay tpdGooglePay;
    private PaymentData paymentData;

    private RecyclerView rvShoppingCartList_ShoppingCart;
    private TextView tvTotal_ShoppingCart, tvCup_ShoppingCart, tvShoppingCartCount_ShoppingCart, tvAddress_ShoppingCart, tvTime_ShoppingCart, tvArrived_ShoppingCart, tvTakeMeal_ShoppingCart,tvCheckOutInfo_ShoppingCart;
    private ImageView ivExit_ShoppingCart;
    private Button btYourSelfPickUp_ShoppingCart, btOrderIn_ShoppingCart, btCheckOut_ShoppingCart;

    private List<OrderItem> orderItems;
    private int count = 0; //購物車裡面的品項數
    private int cup = 0; //購物車的總杯數
    private int total = 0; //購物車的總金額
    private String take_meal_mode = "";//取餐方式
    private String time = "";//顧客到店取餐時間
    private String user_id; //使用者ID
    private String user_name = ""; //使用者名稱
    private String user_phone = ""; //使用者電話
    private String user_address = "";//取餐地址
    private User user;
    Calendar calendar = Calendar.getInstance();
    private int year, month, day, hour, minute;
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝偏好設定載入使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void saveShoppingCartData() {
        preferences_shoppingCart = activity.getSharedPreferences("order_item", MODE_PRIVATE);
        SharedPreferences.Editor order_item_editor = activity.getSharedPreferences("order_item", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String order_item_json = gson.toJson(orderItems);
        order_item_editor.putString("order_item_json", order_item_json);
        order_item_editor.apply();
    }
    private void loadShoppingCartData() {
        preferences_shoppingCart = activity.getSharedPreferences("order_item", MODE_PRIVATE);
        String order_item_json = preferences_shoppingCart.getString("order_item_json", null);
        if (order_item_json != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<OrderItem>>(){}.getType();
            orderItems = gson.fromJson(order_item_json, type);
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝偏好設定載入使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void loadUserData() {
        preferences_user = activity.getSharedPreferences("user", MODE_PRIVATE);
        user_id = preferences_user.getString("user","ALJVuIeu4UWRH4TSLghiz2gu7M32" );//假資料
        db.collection("User").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    user = document.toObject(User.class);
                    user_name = user.getUser_name();
                    user_phone = user.getUser_phone();
                    user_address = user.getUser_address(); //取得使用者地址 但是點選了外送才顯示
                } else {
                    Toast.makeText(activity, "找不到使用者資料", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("購物車");
        return inflater.inflate(R.layout.fragment_shoppingcart_list, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserData();
        loadShoppingCartData();
        tvTakeMeal_ShoppingCart = view.findViewById(R.id.tvTakeMeal_ShoppingCart);
        tvArrived_ShoppingCart = view.findViewById(R.id.tvArrived_ShoppingCart);
        tvAddress_ShoppingCart = view.findViewById(R.id.tvAddress_ShoppingCart);
        tvTime_ShoppingCart = view.findViewById(R.id.tvTime_ShoppingCart);
        btYourSelfPickUp_ShoppingCart = view.findViewById(R.id.btYourSelfPickUp_ShoppingCart);
        btOrderIn_ShoppingCart = view.findViewById(R.id.btOrderIn_ShoppingCart);
        tvShoppingCartCount_ShoppingCart = view.findViewById(R.id.tvShoppingCartCount_ShoppingCart);
        tvTotal_ShoppingCart = view.findViewById(R.id.tvTotal_ShoppingCart);
        tvCup_ShoppingCart = view.findViewById(R.id.tvCup_ShoppingCart);


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvShoppingCartList_ShoppingCart = view.findViewById(R.id.rvShoppingCartList_ShoppingCart);
        rvShoppingCartList_ShoppingCart.setLayoutManager(new LinearLayoutManager(activity));
        showNow();
        showShoppingCartAll();
        showTakeMealMode("");
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_ShoppingCart = view.findViewById(R.id.ivExit_ShoppingCart);
        ivExit_ShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊自取按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btYourSelfPickUp_ShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTakeMealMode("自取");
                new TimePickerDialog(
                        activity,
                        ShoppingcartListFragment.this,
                        hour, minute, true)
                        .show();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊自取按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊外送按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btOrderIn_ShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_address.equals("")) {
                    //請使用者完善地址資料
                } else {
                    showTakeMealMode("外送");
                    tvAddress_ShoppingCart.setText(user_address);
                    new TimePickerDialog(
                            activity,
                            ShoppingcartListFragment.this,
                            hour, minute, true)
                            .show();
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊外送按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊結帳按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        tvCheckOutInfo_ShoppingCart = view.findViewById(R.id.tvCheckOutInfo_ShoppingCart);
        btCheckOut_ShoppingCart = view.findViewById(R.id.btCheckOut_ShoppingCart);
        btCheckOut_ShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //＝＝＝＝＝＝處理訂單資訊＝＝＝＝＝//
                if (orderItems == null)
                {
                    Common.showToast(activity,"購物車裡沒有商品");
                    return;
                }
                Order order = new Order();
                if (take_meal_mode.equals("自取") || take_meal_mode.equals("外送")) {
                    //比較日期大小
                    String input = tvTime_ShoppingCart.getText().toString();
                    Date take_meal_time = null;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
                    try {
                        take_meal_time = formatter.parse(input);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(take_meal_time.before(new Date()))
                    {
                        Common.showToast(activity,"取餐時間小於現在時間");
                        return;
                    }
                    else
                    {
                        order.setOrder_take_meal_time(take_meal_time);
                    }
                }else
                {
                    Common.showToast(activity,"尚未選擇取餐方式");
                    return;
                }
                order.setOrder_id(db.collection("Order").document().getId());
                order.setOrder_date(new Date());
                order.setEmp_id("");
                order.setUser_id(user_id);
                order.setUser_name(user_name);
                order.setUser_phone(user_phone);
                order.setUser_address(user_address);
                order.setOrder_price(total);
                order.setOrder_take_meal(take_meal_mode);
                order.setOrder_status(1);
                //＝＝＝＝＝＝處理訂單資訊＝＝＝＝＝//


                //＝＝＝＝＝＝連結結帳系統＝＝＝＝＝//

                // 跳出user資訊視窗讓user確認，確認後會呼叫onActivityResult()
                tpdGooglePay.requestPayment(TransactionInfo.newBuilder()
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        // 消費總金額
                        .setTotalPrice(String.valueOf(order.getOrder_price()))
                        // 設定幣別
                        .setCurrencyCode("TWD")
                        .build(), LOAD_PAYMENT_DATA_REQUEST_CODE);

                        getPrimeFromTapPay(paymentData);


                Log.d(TAG, "SDK version is " + TPDSetup.getVersion());

                // 使用TPDSetup設定環境。每個設定值出處參看strings.xml
                TPDSetup.initInstance(activity,
                        15088,
                        "app_OdeBd5uyUTuFEHy0USYnck36UD1UrXfamS42X3VFBb8rLiNHkabO9aqmraM0",
                        TPDServerType.Sandbox);
                prepareGooglePay();

                db.collection("Order").document(order.getOrder_id()).set(order);
                //＝＝＝＝＝＝連結結帳系統＝＝＝＝＝//

                //＝＝＝＝＝＝處理訂單明細＝＝＝＝＝//
                for (OrderItem orderItem : orderItems)
                {
                    orderItem.setOrder_id(order.getOrder_id());
                    db.collection("OrderItem").document(orderItem.getSc_item_id()).set(orderItem);
                }
                preferences_shoppingCart = activity.getSharedPreferences("order_item", MODE_PRIVATE);
                preferences_shoppingCart.edit().clear().apply();//清除偏好設定購物車的資料
                //＝＝＝＝＝＝處理訂單明細＝＝＝＝＝//
                Bundle bundle = new Bundle();
                bundle.putString("order_id",order.getOrder_id());
                Navigation.findNavController(view).navigate(R.id.action_shoppingcartListFragment_to_orderListFragment,bundle);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊結帳按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    /**************************************************************************************/
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝GooglePay內容設定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    public void prepareGooglePay() {
        TPDMerchant tpdMerchant = new TPDMerchant();
        // 設定商店名稱
        tpdMerchant.setMerchantName(getString(R.string.TapPay_MerchantName));
        // 設定允許的信用卡種類
        tpdMerchant.setSupportedNetworks(CARD_TYPES);
        // 設定客戶填寫項目
        TPDConsumer tpdConsumer = new TPDConsumer();
        // 不需要電話號碼
        tpdConsumer.setPhoneNumberRequired(false);
        // 不需要運送地址
        tpdConsumer.setShippingAddressRequired(false);
        // 不需要Email
        tpdConsumer.setEmailRequired(false);

        tpdGooglePay = new TPDGooglePay(activity, tpdMerchant, tpdConsumer);
        // 檢查user裝置是否支援Google Pay
        tpdGooglePay.isGooglePayAvailable(new TPDGooglePayListener() {
            @Override
            public void onReadyToPayChecked(boolean isReadyToPay, String msg) {
                Log.d(TAG, "Pay with Google availability : " + isReadyToPay);
                if (isReadyToPay) {
                    btCheckOut_ShoppingCart.setEnabled(true);
                } else {
                    btCheckOut_ShoppingCart.setText(R.string.textCannotUseGPay);
                }
            }
        });
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
                    }
                    break;
                case Activity.RESULT_CANCELED:

                    tvCheckOutInfo_ShoppingCart.setText(R.string.textCanceled);
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    if (status != null) {
                        String text = "status code: " + status.getStatusCode() +
                                " , message: " + status.getStatusMessage();
                        Log.d(TAG, text);
                        tvCheckOutInfo_ShoppingCart.setText(text);
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
            tvCheckOutInfo_ShoppingCart.setText(cardDescription);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPrimeFromTapPay(PaymentData paymentData) {
        showProgressDialog();
        tpdGooglePay.getPrime(
                paymentData,
                new TPDTokenSuccessCallback() {
                    @Override
                    public void onSuccess(String prime, TPDCardInfo tpdCardInfo) {
                        hideProgressDialog();
                        String text = "Your prime is " + prime
                                + "\n\nUse below cURL to proceed the payment : \n"
                                + ApiUtil.generatePayByPrimeCURLForSandBox(prime,
                                getString(R.string.TapPay_PartnerKey),
                                getString(R.string.TapPay_MerchantID));
                        Log.d(TAG, text);
                        tvCheckOutInfo_ShoppingCart.setText(text);
                    }
                },
                new TPDTokenFailureCallback() {
                    @Override
                    public void onFailure(int status, String reportMsg) {
                        hideProgressDialog();
                        String text = "TapPay getPrime failed. status: " + status + ", message: " + reportMsg;
                        Log.d(TAG, text);
                        tvCheckOutInfo_ShoppingCart.setText(text);
                    }
                });
    }



    public ProgressDialog mProgressDialog;

    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝GooglePay內容設定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    /**************************************************************************************/

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝購物車列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingcartListFragment.ShoppingCartAdapter.MyViewHolder> {
        Context context;
        List<OrderItem> orderItems;

        public ShoppingCartAdapter(Context context, List<OrderItem> orderItems) {
            this.context = context;
            this.orderItems = orderItems;
        }

        @Override
        public int getItemCount() {
            return orderItems.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvProductName_ShoppingCart, tvQuantity_ShoppingCart, tvCapacity_ShoppingCart, tvPrice_ShoppingCart, tvTemperature_ShoppingCart, tvSweetness_ShoppingCart, tvProductTotal_ShoppingCart;
            private Button btEdit_ShoppingCart, btDelete_ShoppingCart;

            public MyViewHolder(View ShoppingCartView) {
                super(ShoppingCartView);
                tvProductName_ShoppingCart = ShoppingCartView.findViewById(R.id.tvProductName_ShoppingCart);
                tvQuantity_ShoppingCart = ShoppingCartView.findViewById(R.id.tvQuantity_ShoppingCart);
                tvCapacity_ShoppingCart = ShoppingCartView.findViewById(R.id.tvCapacity_ShoppingCart);
                tvPrice_ShoppingCart = ShoppingCartView.findViewById(R.id.tvPrice_ShoppingCart);
                tvTemperature_ShoppingCart = ShoppingCartView.findViewById(R.id.tvTemperature_ShoppingCart);
                tvSweetness_ShoppingCart = ShoppingCartView.findViewById(R.id.tvSweetness_ShoppingCart);
                tvProductTotal_ShoppingCart = ShoppingCartView.findViewById(R.id.tvProductTotal_ShoppingCart);
                btEdit_ShoppingCart = ShoppingCartView.findViewById(R.id.btEdit_ShoppingCart);
                btDelete_ShoppingCart = ShoppingCartView.findViewById(R.id.btDelete_ShoppingCart);
            }
        }

        @NonNull
        @Override
        public ShoppingcartListFragment.ShoppingCartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.shoppingcart_list_view, parent, false);
            return new ShoppingcartListFragment.ShoppingCartAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ShoppingcartListFragment.ShoppingCartAdapter.MyViewHolder holder, final int position) {
            final OrderItem orderItem = orderItems.get(position);
            int proPrice, proQuantity, proTotal; //計算該項內容的商品總價

            proPrice = (orderItem.getPro_capacity().equals("M")) ? orderItem.getPro_price_M() : orderItem.getPro_price_L();
            proQuantity = orderItem.getPro_quantity();
            proTotal = proPrice * proQuantity;

            holder.tvProductName_ShoppingCart.setText(orderItem.getPro_name()); //抓商品名稱
            holder.tvQuantity_ShoppingCart.setText("*" + String.valueOf(proQuantity)); //抓商品數量
            holder.tvCapacity_ShoppingCart.setText(orderItem.getPro_capacity()); //抓商品大小
            holder.tvPrice_ShoppingCart.setText("$NT" + String.valueOf(proPrice)); //抓商品價格
            holder.tvTemperature_ShoppingCart.setText(orderItem.getPro_temperature()); //抓商品溫度
            holder.tvSweetness_ShoppingCart.setText(orderItem.getPro_sweetness()); //抓商品甜度
            holder.tvProductTotal_ShoppingCart.setText(String.valueOf(proTotal)); //抓商品總價

            //＝＝＝＝＝＝按下編輯按鈕＝＝＝＝＝//
            holder.btEdit_ShoppingCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order_item",orderItem);
                    bundle.putString("fragment",TAG);
                    Navigation.findNavController(view).navigate(R.id.action_shoppingcartListFragment_to_productSpecificationFragment,bundle);
                }
            });
            //＝＝＝＝＝＝按下編輯按鈕＝＝＝＝＝//

            //＝＝＝＝＝＝按下刪除按鈕＝＝＝＝＝//
            holder.btDelete_ShoppingCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderItems.remove(position);
                    SharedPreferences.Editor order_item_delete = activity.getSharedPreferences("order_item", MODE_PRIVATE).edit();
                    order_item_delete.remove("order_item_json");
                    Gson gson = new Gson();
                    String order_item_json = gson.toJson(orderItems);
                    order_item_delete.putString("order_item_json", order_item_json);
                    order_item_delete.apply();
                    Common.showToast(activity,"已成功刪除商品");
                    showShoppingCartAll();
                }
            });
            //＝＝＝＝＝＝按下刪除按鈕＝＝＝＝＝//
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝購物車列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示購物車清單所有資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showShoppingCartAll() {
        if(orderItems != null) {
            rvShoppingCartList_ShoppingCart.setAdapter(new ShoppingCartAdapter(activity, orderItems));
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示購物車的資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            count = orderItems.size();
            cup = 0;total = 0;
            for (OrderItem orderItem : orderItems) {
                cup = cup + orderItem.getPro_quantity();
                if (orderItem.getPro_capacity().equals("M")) {
                    total += (orderItem.getPro_price_M() * orderItem.getPro_quantity());
                } else {
                    total += (orderItem.getPro_price_L() * orderItem.getPro_quantity());
                }
            }
            tvShoppingCartCount_ShoppingCart.setText(String.valueOf("共 " + count + " 筆"));
            tvCup_ShoppingCart.setText(String.valueOf(cup) + "杯");
            tvTotal_ShoppingCart.setText(String.valueOf(total));
        }
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示購物車的資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示購物車清單所有資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝自取與外送的狀態＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showTakeMealMode(String mode) {
        take_meal_mode = mode;
        switch (mode) {
            case "自取":
                btYourSelfPickUp_ShoppingCart.setBackgroundColor(Color.YELLOW);
                btOrderIn_ShoppingCart.setBackgroundColor(Color.GRAY);
                tvTakeMeal_ShoppingCart.setVisibility(View.VISIBLE);
                tvTime_ShoppingCart.setVisibility(View.VISIBLE);
                tvArrived_ShoppingCart.setVisibility(View.GONE);
                tvAddress_ShoppingCart.setVisibility(View.GONE);
                break;
            case "外送":
                btYourSelfPickUp_ShoppingCart.setBackgroundColor(Color.GRAY);
                btOrderIn_ShoppingCart.setBackgroundColor(Color.YELLOW);
                tvTakeMeal_ShoppingCart.setVisibility(View.VISIBLE);
                tvTime_ShoppingCart.setVisibility(View.VISIBLE);
                tvArrived_ShoppingCart.setVisibility(View.VISIBLE);
                tvAddress_ShoppingCart.setVisibility(View.VISIBLE);
                break;
            default:
                btYourSelfPickUp_ShoppingCart.setBackgroundColor(Color.GRAY);
                btOrderIn_ShoppingCart.setBackgroundColor(Color.GRAY);
                tvTakeMeal_ShoppingCart.setVisibility(View.GONE);
                tvTime_ShoppingCart.setVisibility(View.GONE);
                tvArrived_ShoppingCart.setVisibility(View.GONE);
                tvAddress_ShoppingCart.setVisibility(View.GONE);
                break;
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝自取與外送的狀態＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝設定自取時間＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showNow() { //取得現在的時間
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        updateDisplay();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        updateDisplay();
    }

    private void updateDisplay() {
        tvTime_ShoppingCart.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day))
                .append(" ").append(pad(hour)).append(":")
                .append(pad(minute)));
    }

    /* 若數字有十位數，直接顯示；
       若只有個位數則補0後再顯示，例如7會改成07後再顯示 */
    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + String.valueOf(number);
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝設定自取時間＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}