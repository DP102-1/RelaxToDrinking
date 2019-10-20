package com.example.relaxtodrinking;
/***************************************************************/


/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.relaxtodrinking.data.Order;
import com.example.relaxtodrinking.data.OrderItem;
import com.example.relaxtodrinking.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class OrderHistoryFragment extends Fragment {
    private String TAG = "歷史訂單紀錄";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private SharedPreferences preferences_user;

    private RecyclerView rvOrderHistory_OrderHistory;
    private ImageView ivBack_OrderHistory;

    private List<Order> orders_history; //order_status不是0的話(已完成)不會進到這個集合
    private String user_id = "";

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void loadUserData() {
        preferences_user = activity.getSharedPreferences("user", MODE_PRIVATE);
        user_id = preferences_user.getString("user","ALJVuIeu4UWRH4TSLghiz2gu7M32" );//假資料
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋使用者資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("歷史訂單紀錄");
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        loadUserData();
        rvOrderHistory_OrderHistory = view.findViewById(R.id.rvOrderHistory_OrderHistory);
        rvOrderHistory_OrderHistory.setLayoutManager(new LinearLayoutManager(activity));
        showOrderAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_OrderHistory = view.findViewById(R.id.ivBack_OrderHistory);
        ivBack_OrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class OrderAdapter extends RecyclerView.Adapter<OrderHistoryFragment.OrderAdapter.MyViewHolder> {
        Context context;
        List<Order> orders;

        public OrderAdapter(Context context, List<Order> orders) {
            this.context = context;
            this.orders = orders;
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvOrderDate_OrderHistory,tvOrderTakeMeal_OrderHistory,tvOrderTotal_OrderHistory;
            public MyViewHolder(View OrderView) {
                super(OrderView);
                tvOrderDate_OrderHistory = OrderView.findViewById(R.id.tvOrderDate_OrderHistory);
                tvOrderTakeMeal_OrderHistory = OrderView.findViewById(R.id.tvOrderTakeMeal_OrderHistory);
                tvOrderTotal_OrderHistory = OrderView.findViewById(R.id.tvOrderTotal_OrderHistory);
            }
        }

        @NonNull
        @Override
        public OrderHistoryFragment.OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.order_history_view, parent, false);
            return new OrderHistoryFragment.OrderAdapter.MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull OrderHistoryFragment.OrderAdapter.MyViewHolder holder, int position) {
            final Order order = orders.get(position);
            holder.tvOrderDate_OrderHistory.setText(new SimpleDateFormat("yyyy年MM月d日 HH點mm分", Locale.CHINESE).format(order.getOrder_date()));
            holder.tvOrderTakeMeal_OrderHistory.setText(order.getOrder_take_meal());
            holder.tvOrderTotal_OrderHistory.setText("$NT " + String.valueOf(order.getOrder_price()));
            //＝＝＝＝＝查看訂單詳細資訊＝＝＝＝＝//
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("order_id", order.getOrder_id());
                    Navigation.findNavController(view).navigate(R.id.action_orderHistoryFragment_to_orderDetailFragment,bundle);
                }
            });
            //＝＝＝＝＝查看訂單詳細資訊＝＝＝＝＝//
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示使用者歷史訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showOrderAll() {
        db.collection("Order").orderBy("order_date", Query.Direction.DESCENDING).whereEqualTo("user_id", user_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Order> orders = new ArrayList<>();
                orders_history = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    orders.add(snapshot.toObject(Order.class));
                for (Order order : orders) {
                    if(order.getOrder_status() == 0)
                    {
                        orders_history.add(order);
                    }
                }
                rvOrderHistory_OrderHistory.setAdapter(new OrderHistoryFragment.OrderAdapter(activity, orders_history));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示使用者歷史訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
