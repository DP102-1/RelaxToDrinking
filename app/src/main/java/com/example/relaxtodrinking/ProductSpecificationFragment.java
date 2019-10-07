package com.example.relaxtodrinking;
/***************************************************************/
//radiobutten的樣式更改
//加入購物車要圓角
//數量輸入框要小
//數量輸入格式問題
/***************************************************************/

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductSpecificationFragment extends Fragment {
    private String TAG = "商品規格設定";

    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private TextView pro_name, pro_price_M, pro_price_L;
    private ImageView add, reduce, exit;
    private EditText pro_quantity;
    private CheckBox pro_L, pro_M;
    private Button addShoppingCart;
    private RadioGroup pro_sweetness, pro_temperature;

    int proPriceM = 0, proPriceL = 0;
    private String proId = "",
            proName = "",
            temperature = "",
            capacity = "L",
            sweetness = "",
            quantity = "1",
            sc_item_id; //判別是新增還是修改
    private ShoppingCart shoppingCart = new ShoppingCart();


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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pro_name = view.findViewById(R.id.tvProductName_ProductSpecification);
        pro_price_L = view.findViewById(R.id.tvPriceL_ProductSpecification);
        pro_price_M = view.findViewById(R.id.tvPriceM_ProductSpecification);
        pro_temperature = view.findViewById(R.id.rgTemperature_ProductSpecification);
        pro_L = view.findViewById(R.id.cbProductL_ProductSpecification);
        pro_M = view.findViewById(R.id.cbProductM_ProductSpecification);
        pro_sweetness = view.findViewById(R.id.rgSweetness_ProductSpecification);
        pro_quantity = view.findViewById(R.id.etQuantity_ProductSpecification);
        addShoppingCart = view.findViewById(R.id.btAddShoppingCart_ProductSpecification);
        Bundle sc_bundle = getArguments();
        if (sc_bundle != null) {
            sc_item_id = sc_bundle.getString("sc_item_id");
        }
        if (sc_item_id == null)//先判別sc_time_id 有id值代表修改 Null代表新增
        {
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝抓取bundle的值顯示在頁面＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            Bundle bundle = getArguments();
            if (bundle != null) {
                proId = bundle.getString("pro_id");
                proName = bundle.getString("pro_name");
                proPriceM = bundle.getInt("pro_price_M");
                proPriceL = bundle.getInt("pro_price_L");
            }
            pro_name.setText(proName);
            pro_price_L.setText(String.valueOf(proPriceL));
            pro_price_M.setText(String.valueOf(proPriceM));
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝抓取bundle的值顯示在頁面＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        } else {
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝抓取firebase的資料顯示在頁面＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            db.collection("ShoppingCart").document(sc_item_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        shoppingCart = document.toObject(ShoppingCart.class);
                        getShoppingCart();
                        pro_name.setText(proName);
                        pro_price_M.setText(String.valueOf(proPriceM));
                        pro_price_L.setText(String.valueOf(proPriceL));
                        //溫度 甜度顯示到radiogroup上不會寫
                        pro_quantity.setText(quantity);
                        if (capacity.equals("M")) {
                            pro_M.setChecked(true);
                            pro_L.setChecked(false);
                        } else {
                            pro_M.setChecked(false);
                            pro_L.setChecked(true);
                        }
                        addShoppingCart.setText("確定修改");
                    } else {
                        Toast.makeText(activity, "抓取firebase的資料失敗", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝抓取firebase的資料顯示在頁面＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        }

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇冷熱飲＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        pro_temperature.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                temperature = radioButton.getText().toString();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇冷熱飲＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇容量＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        pro_L.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                pro_M.setChecked(!isChecked);
                capacity = "L";
            }
        });
        pro_M.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                pro_L.setChecked(!isChecked);
                capacity = "M";
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇容量＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇甜度＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        pro_sweetness.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                sweetness = radioButton.getText().toString();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇甜度＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇購買數量＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        add = view.findViewById(R.id.ivQuantityAdd_ProductSpecification);
        reduce = view.findViewById(R.id.ivQuantityReduct_ProductSpecification);

        //＝＝＝＝＝自行輸入＝＝＝＝＝//
        pro_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isCorrectNumber(pro_quantity.getText().toString())) {
                    quantity = pro_quantity.getText().toString();
                } else {
                    pro_quantity.setText(quantity);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //＝＝＝＝＝自行輸入＝＝＝＝＝//

        //＝＝＝＝＝增加＝＝＝＝＝//
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf(quantity) >= 99)
                {
                    quantity = "99";
                }else
                {
                    int iQuantity = Integer.valueOf(quantity);
                    iQuantity++;
                    quantity = String.valueOf(iQuantity);
                }
                pro_quantity.setText(quantity);
            }
        });
        //＝＝＝＝＝增加＝＝＝＝＝//

        //＝＝＝＝＝減少＝＝＝＝＝//
        reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf(quantity) <= 1)
                {
                    quantity = "1";
                }else
                {
                    int iQuantity = Integer.valueOf(quantity);
                    iQuantity--;
                    quantity = String.valueOf(iQuantity);
                }
                pro_quantity.setText(quantity);
            }
        });
        //＝＝＝＝＝減少＝＝＝＝＝//
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇購買數量＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊加入購物車＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        addShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNullProductDate()) { //使用者都有填寫資料的話...
                    setShoppingCart();
                    db.collection("ShoppingCart").document(shoppingCart.getSc_item_id()).set(shoppingCart);
                    Navigation.findNavController(view).popBackStack();
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊加入購物車＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        exit = view.findViewById(R.id.ivExit_ProductSpecification);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷數字1~99＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private boolean isCorrectNumber(String quantity) {
        if (quantity.length() <= 0) { //沒有輸入字串
            Toast.makeText(activity, "數量不能為空", Toast.LENGTH_SHORT).show();
            return false;
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(quantity);
        if (!isNum.matches()) { //判別是否為數字
            Toast.makeText(activity, "數量只能為數字", Toast.LENGTH_SHORT).show();
            return false;
        }
        return Integer.valueOf(quantity) < 100 && Integer.valueOf(quantity) > 0; //判別是否為1~99
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷數字1~99＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證使用者是否都有填寫內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private boolean isNullProductDate() {
        if (capacity.length() <= 0) {
            Toast.makeText(activity, "尚未選擇容量", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sweetness.length() <= 0) {
            Toast.makeText(activity, "尚未選擇甜度", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (temperature.length() <= 0) {
            Toast.makeText(activity, "尚未選擇溫度", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證使用者是否都有填寫內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝從購物車物件抓修改資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    private void getShoppingCart() {
        proId = shoppingCart.getPro_id();
        proName = shoppingCart.getPro_name();
        proPriceM = shoppingCart.getPro_price_M();
        proPriceL = shoppingCart.getPro_price_L();
        temperature = shoppingCart.getPro_temperature();
        capacity = shoppingCart.getPro_capacity();
        sweetness = shoppingCart.getPro_sweetness();
        quantity = String.valueOf(shoppingCart.getPro_quantity());
        sc_item_id = shoppingCart.getSc_item_id();
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝從購物車物件抓修改資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料放到購物車物件＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void setShoppingCart() {
        shoppingCart.setPro_id(proId);
        shoppingCart.setPro_name(proName);
        shoppingCart.setPro_capacity(capacity);
        shoppingCart.setPro_price_M(proPriceM);
        shoppingCart.setPro_price_L(proPriceL);
        shoppingCart.setPro_sweetness(sweetness);
        shoppingCart.setPro_temperature(temperature);
        shoppingCart.setPro_quantity(Integer.valueOf(quantity));
        Toast.makeText(activity, (sc_item_id == null) ? "商品已加入購物車":"商品內容修改成功", Toast.LENGTH_SHORT).show();
        shoppingCart.setSc_item_id((sc_item_id == null) ? db.collection("ShoppingCart").document().getId() : sc_item_id);
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料放到購物車物件＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
