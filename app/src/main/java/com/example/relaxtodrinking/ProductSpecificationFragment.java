package com.example.relaxtodrinking;
/***************************************************************/
//radiobutten的樣式更改
//加入購物車要圓角
//數量輸入框要小
//數量輸入格式問題
/***************************************************************/

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.relaxtodrinking.data.OrderItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ProductSpecificationFragment extends Fragment {
    private String TAG = "商品規格設定";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private SharedPreferences preferences_shoppingCart;

    private TextView tvProductName_ProductSpecification, tvPriceM_ProductSpecification, tvPriceL_ProductSpecification;
    private ImageView ivQuantityAdd_ProductSpecification, ivQuantityReduct_ProductSpecification, ivExit_ProductSpecification;
    private EditText etQuantity_ProductSpecification;
    private CheckBox cbProductL_ProductSpecification, cbProductM_ProductSpecification;
    private RadioButton rbTemperature1_ProductSpecification, rbTemperature2_ProductSpecification, rbTemperature3_ProductSpecification, rbTemperature4_ProductSpecification, rbTemperature5_ProductSpecification,
            rbSweetness1_ProductSpecification, rbSweetness2_ProductSpecification, rbSweetness3_ProductSpecification, rbSweetness4_ProductSpecification, rbSweetness5_ProductSpecification;
    private Button btAddShoppingCart_ProductSpecification;
    private RadioGroup rgSweetness_ProductSpecification, rgTemperature_ProductSpecification;

    private int proPriceM = 0, proPriceL = 0;
    private String ScItemId,
            proId = "",
            proName = "",
            temperature = "",
            capacity = "L",
            sweetness = "",
            quantity = "1";
    private OrderItem orderItem;

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("商品規格設定");
        return inflater.inflate(R.layout.fragment_product_specification, container, false);
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝偏好設定載入使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝偏好設定載入使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝抓取bundle的值並顯示在頁面＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString("fragment").equals("購物車")) //判別是從哪個頁面來
            {
                orderItem = (OrderItem) bundle.getSerializable("order_item");
            } else {
                proId = bundle.getString("pro_id");
                proName = bundle.getString("pro_name");
                proPriceL = bundle.getInt("pro_price_L");
                proPriceM = bundle.getInt("pro_price_M");
            }
        }

        tvProductName_ProductSpecification = view.findViewById(R.id.tvProductName_ProductSpecification);
        tvPriceL_ProductSpecification = view.findViewById(R.id.tvPriceL_ProductSpecification);
        tvPriceM_ProductSpecification = view.findViewById(R.id.tvPriceM_ProductSpecification);
        rgTemperature_ProductSpecification = view.findViewById(R.id.rgTemperature_ProductSpecification);
        cbProductL_ProductSpecification = view.findViewById(R.id.cbProductL_ProductSpecification);
        cbProductM_ProductSpecification = view.findViewById(R.id.cbProductM_ProductSpecification);
        rgSweetness_ProductSpecification = view.findViewById(R.id.rgSweetness_ProductSpecification);
        etQuantity_ProductSpecification = view.findViewById(R.id.etQuantity_ProductSpecification);
        btAddShoppingCart_ProductSpecification = view.findViewById(R.id.btAddShoppingCart_ProductSpecification);
        if (orderItem != null) {
            proId = orderItem.getPro_id();
            proName = orderItem.getPro_name();
            proPriceM = orderItem.getPro_price_M();
            proPriceL = orderItem.getPro_price_L();
            temperature = orderItem.getPro_temperature();
            capacity = orderItem.getPro_capacity();
            sweetness = orderItem.getPro_sweetness();
            quantity = String.valueOf(orderItem.getPro_quantity());
            ScItemId = orderItem.getSc_item_id();
            switch (temperature) {
                case "正常冰":
                    rbTemperature1_ProductSpecification = view.findViewById(R.id.rbTemperature1_ProductSpecification);
                    rbTemperature1_ProductSpecification.setChecked(true);
                    break;
                case "少冰":
                    rbTemperature2_ProductSpecification = view.findViewById(R.id.rbTemperature2_ProductSpecification);
                    rbTemperature2_ProductSpecification.setChecked(true);
                    break;
                case "去冰":
                    rbTemperature3_ProductSpecification = view.findViewById(R.id.rbTemperature3_ProductSpecification);
                    rbTemperature3_ProductSpecification.setChecked(true);
                    break;
                case "常溫":
                    rbTemperature4_ProductSpecification = view.findViewById(R.id.rbTemperature4_ProductSpecification);
                    rbTemperature4_ProductSpecification.setChecked(true);
                    break;
                case "熱飲":
                    rbTemperature5_ProductSpecification = view.findViewById(R.id.rbTemperature5_ProductSpecification);
                    rbTemperature5_ProductSpecification.setChecked(true);
                    break;
                default:
                    break;
            }

            switch (sweetness) {
                case "正常甜":
                    rbSweetness1_ProductSpecification = view.findViewById(R.id.rbSweetness1_ProductSpecification);
                    rbSweetness1_ProductSpecification.setChecked(true);
                    break;
                case "少糖":
                    rbSweetness2_ProductSpecification = view.findViewById(R.id.rbSweetness2_ProductSpecification);
                    rbSweetness2_ProductSpecification.setChecked(true);
                    break;
                case "半糖":
                    rbSweetness3_ProductSpecification = view.findViewById(R.id.rbSweetness3_ProductSpecification);
                    rbSweetness3_ProductSpecification.setChecked(true);
                    break;
                case "微糖":
                    rbSweetness4_ProductSpecification = view.findViewById(R.id.rbSweetness4_ProductSpecification);
                    rbSweetness4_ProductSpecification.setChecked(true);
                    break;
                case "無糖":
                    rbSweetness5_ProductSpecification = view.findViewById(R.id.rbSweetness5_ProductSpecification);
                    rbSweetness5_ProductSpecification.setChecked(true);
                    break;
                default:
                    break;
            }

            if (capacity.equals("M")) {
                cbProductM_ProductSpecification.setChecked(true);
                cbProductL_ProductSpecification.setChecked(false);
            } else {
                cbProductM_ProductSpecification.setChecked(false);
                cbProductL_ProductSpecification.setChecked(true);
            }
            btAddShoppingCart_ProductSpecification.setText("確定修改");
            etQuantity_ProductSpecification.setText(quantity);
        }
        tvProductName_ProductSpecification.setText(proName);
        tvPriceL_ProductSpecification.setText(String.valueOf(proPriceL));
        tvPriceM_ProductSpecification.setText(String.valueOf(proPriceM));
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝抓取bundle的值顯示在頁面＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇冷熱飲＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rgTemperature_ProductSpecification.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                temperature = radioButton.getText().toString();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇冷熱飲＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇容量＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        cbProductL_ProductSpecification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbProductM_ProductSpecification.setChecked(!isChecked);
                capacity = "L";
            }
        });
        cbProductM_ProductSpecification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbProductL_ProductSpecification.setChecked(!isChecked);
                capacity = "M";
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇容量＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇甜度＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rgSweetness_ProductSpecification.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                sweetness = radioButton.getText().toString();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇甜度＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇購買數量＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivQuantityAdd_ProductSpecification = view.findViewById(R.id.ivQuantityAdd_ProductSpecification);
        ivQuantityReduct_ProductSpecification = view.findViewById(R.id.ivQuantityReduct_ProductSpecification);
        //＝＝＝＝＝增加＝＝＝＝＝//
        ivQuantityAdd_ProductSpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf(quantity) >= 99) {
                    quantity = "99";
                } else {
                    int iQuantity = Integer.valueOf(quantity);
                    iQuantity++;
                    quantity = String.valueOf(iQuantity);
                }
                etQuantity_ProductSpecification.setText(quantity);
            }
        });
        //＝＝＝＝＝增加＝＝＝＝＝//

        //＝＝＝＝＝減少＝＝＝＝＝//
        ivQuantityReduct_ProductSpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf(quantity) <= 1) {
                    quantity = "1";
                } else {
                    int iQuantity = Integer.valueOf(quantity);
                    iQuantity--;
                    quantity = String.valueOf(iQuantity);
                }
                etQuantity_ProductSpecification.setText(quantity);
            }
        });
        //＝＝＝＝＝減少＝＝＝＝＝//
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇購買數量＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊加入購物車＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btAddShoppingCart_ProductSpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNullProductDate()) { //使用者都有填寫資料的話...
                    if (orderItem == null) {
                        orderItem = new OrderItem();
                    }
                    orderItem.setPro_id(proId);
                    orderItem.setPro_name(proName);
                    orderItem.setPro_capacity(capacity);
                    orderItem.setPro_price_M(proPriceM);
                    orderItem.setPro_price_L(proPriceL);
                    orderItem.setPro_sweetness(sweetness);
                    orderItem.setPro_temperature(temperature);
                    orderItem.setPro_quantity(Integer.valueOf(quantity));
                    Common.showToast(activity, (ScItemId == null) ? "商品已加入購物車" : "商品內容修改成功");
                    orderItem.setSc_item_id((ScItemId == null) ? db.collection("OrderItem").document().getId() : ScItemId);
                    saveShoppingCartData(orderItem);
                    Navigation.findNavController(view).popBackStack();
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊加入購物車＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_ProductSpecification = view.findViewById(R.id.ivExit_ProductSpecification);
        ivExit_ProductSpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證使用者是否都有填寫內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private boolean isNullProductDate() {
        if (capacity.length() <= 0) {
            Common.showToast(activity, "尚未選擇容量");
            return false;
        }
        if (sweetness.length() <= 0) {
            Common.showToast(activity, "尚未選擇甜度");
            return false;
        }
        if (temperature.length() <= 0) {
            Common.showToast(activity, "尚未選擇溫度");
            return false;
        }
        return true;
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證使用者是否都有填寫內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料儲存至購物車偏好設定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void saveShoppingCartData(OrderItem orderItem) {
        preferences_shoppingCart = activity.getSharedPreferences("order_item", MODE_PRIVATE);
        String order_item_json = preferences_shoppingCart.getString("order_item_json", null);
        List<OrderItem> orderItems = new ArrayList<>();
        if (order_item_json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<OrderItem>>() {
            }.getType();
            orderItems = gson.fromJson(order_item_json, type);
        }
        for (int i = orderItems.size() - 1; i >= 0; i--) {
            if (orderItems.get(i).getSc_item_id().equals(orderItem.getSc_item_id())) {
                orderItems.remove(i);
            }
        }
        orderItems.add(orderItem); //物件加入
        SharedPreferences.Editor order_item_editor = activity.getSharedPreferences("order_item", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        order_item_json = gson.toJson(orderItems);
        order_item_editor.putString("order_item_json", order_item_json);
        order_item_editor.apply();
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料儲存至購物車偏好設定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
