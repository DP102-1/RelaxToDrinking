package com.example.relaxtodrinking;
/***************************************************************/
//

/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.relaxtodrinking.data.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryFragment extends Fragment {
    private String TAG = "歷史訂單紀錄";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private RecyclerView rvOrderHistory_OrderHistory;
    private ImageView ivBack_OrderHistory;

    private List<Order> orders;
    private String user_id = "";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("歷史訂單紀錄");
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        Bundle bundle = getArguments(); //如果是從管理者搜尋頁面看資料 就有值
        if (bundle != null) {
            user_id = bundle.getString("user_id");
        }else {
            FirebaseUser user = auth.getCurrentUser(); //從登入者帳號查看歷史訂單
            if (user != null) {
                user_id = user.getUid();
                Log.e(TAG, user.getUid());
            } else {
                user_id = "";
            }
        }
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
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
        db.collection("Order").orderBy("order_date", Query.Direction.DESCENDING).whereEqualTo("order_status",0).whereEqualTo("user_id", user_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orders = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    orders.add(snapshot.toObject(Order.class));
                Log.e(TAG,"有" + orders.size()+"^"+user_id);
                rvOrderHistory_OrderHistory.setAdapter(new OrderHistoryFragment.OrderAdapter(activity, orders));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示使用者歷史訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
