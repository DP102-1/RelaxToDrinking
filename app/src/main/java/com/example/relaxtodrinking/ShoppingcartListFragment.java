package com.example.relaxtodrinking;
/***************************************************************/
//購物車清單更新顯示有問題
//
/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ShoppingcartListFragment extends Fragment {
    private String TAG = "購物車";

    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView recyclerViewShoppingCart;
    private TextView sc_total, sc_cup, sc_count, user_address, user_pickup_time, arrived, take_meal;
    private ImageView exit;
    private Button yourself_pickup, order_in, check_out;


    private ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();
    private int count = 0; //購物車裡面的品項數
    private int cup = 0; //購物車的總杯數
    private int total = 0; //購物車的總金額
    private String take_meal_mode = "";//取餐方式
    private String address = "";//取餐地址
    private String time = "";//顧客到店取餐時間
    private User user = new User();
    ///////////假資料/////////////
    private String user_id = "ALJVuIeu4UWRH4TSLghiz2gu7M32";//判別使用者資料
    ///////////假資料/////////////


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
        activity.setTitle("購物車");
        return inflater.inflate(R.layout.fragment_shoppingcart_list, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        take_meal = view.findViewById(R.id.tvTakeMeal_ShoppingCart);
        arrived = view.findViewById(R.id.tvArrived_ShoppingCart);
        user_address = view.findViewById(R.id.tvAddress_ShoppingCart);
        user_pickup_time = view.findViewById(R.id.tvTime_ShoppingCart);
        yourself_pickup = view.findViewById(R.id.btYourSelfPickUp_ShoppingCart);
        order_in = view.findViewById(R.id.btOrderIn_ShoppingCart);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        db.collection("User").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    user = document.toObject(User.class);
                    address = user.getUser_address(); //取得使用者地址 但是點選了外送才顯示
                } else {
                    Toast.makeText(activity, "找不到使用者資料", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        recyclerViewShoppingCart = view.findViewById(R.id.rvShoppingCartList_ShoppingCart);
        recyclerViewShoppingCart.setLayoutManager(new LinearLayoutManager(activity));
        showShoppingCartAll();
        showTakeMealMode("");
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        sc_count = view.findViewById(R.id.tvShoppingCartCount_ShoppingCart);
        sc_total = view.findViewById(R.id.tvTotal_ShoppingCart);
        sc_cup = view.findViewById(R.id.tvCup_ShoppingCart);

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        exit = view.findViewById(R.id.ivExit_ShoppingCart);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊自取按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        yourself_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTakeMealMode("自取");
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊自取按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊外送按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        order_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address.equals("")) {
                    //請使用者完善地址資料
                } else {
                    showTakeMealMode("外送");
                    user_address.setText(address);
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊外送按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊結帳按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        check_out = view.findViewById(R.id.btCheckOut_ShoppingCart);
        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊結帳按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝購物車列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingcartListFragment.ShoppingCartAdapter.MyViewHolder> {
        Context context;
        List<ShoppingCart> shoppingCarts;

        public ShoppingCartAdapter(Context context, List<ShoppingCart> shoppingCarts) {
            this.context = context;
            this.shoppingCarts = shoppingCarts;
        }

        @Override
        public int getItemCount() {
            return shoppingCarts.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView pro_name, pro_quantity, pro_capacity, pro_price, pro_temperature, pro_sweetness, pro_total;
            private Button edit, delete;

            public MyViewHolder(View ShoppingCartView) {
                super(ShoppingCartView);
                pro_name = ShoppingCartView.findViewById(R.id.tvProductName_ShoppingCart);
                pro_quantity = ShoppingCartView.findViewById(R.id.tvQuantity_ShoppingCart);
                pro_capacity = ShoppingCartView.findViewById(R.id.tvCapacity_ShoppingCart);
                pro_price = ShoppingCartView.findViewById(R.id.tvPrice_ShoppingCart);
                pro_temperature = ShoppingCartView.findViewById(R.id.tvTemperature_ShoppingCart);
                pro_sweetness = ShoppingCartView.findViewById(R.id.tvSweetness_ShoppingCart);
                pro_total = ShoppingCartView.findViewById(R.id.tvProductTotal_ShoppingCart);
                edit = ShoppingCartView.findViewById(R.id.btEdit_ShoppingCart);
                delete = ShoppingCartView.findViewById(R.id.btDelete_ShoppingCart);
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
            final ShoppingCart shoppingCart = shoppingCarts.get(position);
            int proPrice, proQuantity, proTotal; //計算該項內容的商品總價

            proPrice = (shoppingCart.getPro_capacity().equals("M")) ? shoppingCart.getPro_price_M() : shoppingCart.getPro_price_L();
            proQuantity = shoppingCart.getPro_quantity();
            proTotal = proPrice * proQuantity;

            holder.pro_name.setText(shoppingCart.getPro_name()); //抓商品名稱
            holder.pro_quantity.setText("*" + String.valueOf(proQuantity)); //抓商品數量
            holder.pro_capacity.setText(shoppingCart.getPro_capacity()); //抓商品大小
            holder.pro_price.setText("$NT" + String.valueOf(proPrice)); //抓商品價格
            holder.pro_temperature.setText(shoppingCart.getPro_temperature()); //抓商品溫度
            holder.pro_sweetness.setText(shoppingCart.getPro_sweetness()); //抓商品甜度
            holder.pro_total.setText(String.valueOf(proTotal)); //抓商品總價
            //＝＝＝＝＝＝按下編輯按鈕＝＝＝＝＝//
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle sc_bundle = new Bundle();
                    sc_bundle.putString("sc_item_id", shoppingCart.getSc_item_id());
                    Navigation.findNavController(view).navigate(R.id.action_shoppingcartListFragment_to_productSpecificationFragment, sc_bundle);
                }
            });
            //＝＝＝＝＝＝按下編輯按鈕＝＝＝＝＝//

            //＝＝＝＝＝＝按下刪除按鈕＝＝＝＝＝//
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("ShoppingCart").document(shoppingCart.getSc_item_id()).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(activity, "已成功刪除商品", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, "刪除失敗", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    // ShoppingCartAdapter.notifyDataSetChanged();
                }
            });
            //＝＝＝＝＝＝按下刪除按鈕＝＝＝＝＝//
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝購物車列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示購物車清單所有資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showShoppingCartAll() {
        db.collection("ShoppingCart").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                shoppingCarts.add(document.toObject(ShoppingCart.class));
                            }
                            recyclerViewShoppingCart.setAdapter(new ShoppingCartAdapter(activity, shoppingCarts));
                            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示購物車的資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
                            count = shoppingCarts.size();
                            for (ShoppingCart shoppingCart : shoppingCarts) {
                                cup = cup + shoppingCart.getPro_quantity();
                                if (shoppingCart.getPro_capacity().equals("M")) {
                                    total += (shoppingCart.getPro_price_M() * shoppingCart.getPro_quantity());
                                } else {
                                    total += (shoppingCart.getPro_price_L() * shoppingCart.getPro_quantity());
                                }
                            }
                            sc_count.setText(String.valueOf("共 " + count + " 筆"));
                            sc_cup.setText(String.valueOf(cup) + "杯");
                            sc_total.setText(String.valueOf(total));
                            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示購物車的資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示購物車清單所有資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝自取與外送的狀態＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showTakeMealMode(String mode) {
        take_meal_mode = mode;
        switch (mode) {
            case "自取":
                yourself_pickup.setBackgroundColor(Color.YELLOW);
                order_in.setBackgroundColor(Color.GRAY);
                take_meal.setVisibility(View.VISIBLE);
                user_pickup_time.setVisibility(View.VISIBLE);
                arrived.setVisibility(View.GONE);
                user_address.setVisibility(View.GONE);
                break;
            case "外送":
                yourself_pickup.setBackgroundColor(Color.GRAY);
                order_in.setBackgroundColor(Color.YELLOW);
                take_meal.setVisibility(View.GONE);
                user_pickup_time.setVisibility(View.GONE);
                arrived.setVisibility(View.VISIBLE);
                user_address.setVisibility(View.VISIBLE);
                break;
            default:
                yourself_pickup.setBackgroundColor(Color.GRAY);
                order_in.setBackgroundColor(Color.GRAY);
                take_meal.setVisibility(View.GONE);
                user_pickup_time.setVisibility(View.GONE);
                arrived.setVisibility(View.GONE);
                user_address.setVisibility(View.GONE);
                break;
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝自取與外送的狀態＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

}
