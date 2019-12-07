package com.example.relaxtodrinking;
/***************************************************************/


/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderManagementFragment extends Fragment {
    private String TAG = "訂單管理";
    private final static int STATUS_Order = 1;
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView rvOrderList_OrderManagement;
    private TextView tvOrderDate_OrderManagement, tvOrderNotFinish_OrderManagement;
    private Button btOrderHistory_OrderManagement;
    private ImageView ivBack_OrderManagement;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月d日 HH點mm分", Locale.CHINESE);
    private SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy年MM月d日", Locale.CHINESE);
    private List<Order> orders;
    private String today;

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onResume() {
        super.onResume();
        showOrderAll();
    }

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
        activity.setTitle(TAG);
        return inflater.inflate(R.layout.fragment_order_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝取得今天日期＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        today = sdf_date.format(new Date());
        tvOrderDate_OrderManagement = view.findViewById(R.id.tvOrderDate_OrderManagement);
        tvOrderDate_OrderManagement.setText(sdf.format(new Date()));
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝取得今天日期＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvOrderList_OrderManagement = view.findViewById(R.id.rvOrderList_OrderManagement);
        rvOrderList_OrderManagement.setLayoutManager(new LinearLayoutManager(activity));
        showOrderAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示未完成訂單數＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        tvOrderNotFinish_OrderManagement = view.findViewById(R.id.tvOrderNotFinish_OrderManagement);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示未完成訂單數＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊歷史訂單查詢＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btOrderHistory_OrderManagement = view.findViewById(R.id.btOrderHistory_OrderManagement);
        btOrderHistory_OrderManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_orderManagementFragment_to_orderSearchFragment);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊歷史訂單查詢＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_OrderManagement = view.findViewById(R.id.ivBack_OrderManagement);
        ivBack_OrderManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class OrderAdapter extends RecyclerView.Adapter<OrderManagementFragment.OrderAdapter.MyViewHolder> {
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
            private TextView tvOrderNumber_OrderManagement, tvOrderDate_OrderManagement, tvUserName_OrderManagement, tvUserPhone_OrderManagement, tvUserAddress_OrderManagement, tvOrderStatus_OrderManagement;
            private Button btOrderAccept_OrderManagement;

            public MyViewHolder(View OrderView) {
                super(OrderView);
                tvOrderNumber_OrderManagement = OrderView.findViewById(R.id.tvOrderNumber_OrderManagement);
                tvOrderDate_OrderManagement = OrderView.findViewById(R.id.tvOrderDate_OrderManagement);
                tvUserName_OrderManagement = OrderView.findViewById(R.id.tvUserName_OrderManagement);
                tvUserPhone_OrderManagement = OrderView.findViewById(R.id.tvUserPhone_OrderManagement);
                tvUserAddress_OrderManagement = OrderView.findViewById(R.id.tvUserAddress_OrderManagement);
                tvOrderStatus_OrderManagement = OrderView.findViewById(R.id.tvOrderStatus_OrderManagement);
                btOrderAccept_OrderManagement = OrderView.findViewById(R.id.btOrderAccept_OrderManagement);
            }
        }

        @NonNull
        @Override
        public OrderManagementFragment.OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.order_management_view, parent, false);
            return new OrderManagementFragment.OrderAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final OrderManagementFragment.OrderAdapter.MyViewHolder holder, int position) {
            final Order order = orders.get(position);
            holder.tvOrderNumber_OrderManagement.setText("#" + String.valueOf(position + 1));
            holder.tvOrderDate_OrderManagement.setText(sdf.format(order.getOrder_date()));
            holder.tvUserName_OrderManagement.setText(order.getUser_name());
            holder.tvUserPhone_OrderManagement.setText(order.getUser_phone());
            holder.tvUserAddress_OrderManagement.setText(order.getUser_address());
            switch (order.getOrder_status()) {
                case 0:
                    holder.tvOrderStatus_OrderManagement.setText("已完成");
                    holder.tvOrderStatus_OrderManagement.setTextColor(Color.BLUE);
                case 1:
                    holder.tvOrderStatus_OrderManagement.setText("未出貨");
                    holder.tvOrderStatus_OrderManagement.setTextColor(Color.GRAY);
                case 2:
                    holder.tvOrderStatus_OrderManagement.setText("送貨中");
                    holder.tvOrderStatus_OrderManagement.setTextColor(Color.RED);
                default:
                    holder.tvOrderStatus_OrderManagement.setText("");
                    holder.tvOrderStatus_OrderManagement.setTextColor(Color.GRAY);
            }
            switch (order.getOrder_status()) {
                case 0:
                    holder.btOrderAccept_OrderManagement.setText("已經\n完成");
                    holder.btOrderAccept_OrderManagement.setEnabled(false);
                    holder.itemView.setBackgroundColor(Color.parseColor("#AAAAAA"));

                    break;
                case 1:
                    holder.btOrderAccept_OrderManagement.setText("接收\n訂單");
                    break;
                case 2:
                    holder.btOrderAccept_OrderManagement.setText("查看\n位置");
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFC9D7"));
                    break;
                default:
                    break;
            }
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝接受訂單和查看外送人員位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            holder.btOrderAccept_OrderManagement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (order.getOrder_status()) {
                        case 0:
                            Common.showToast(activity, "該訂單已完成");
                            break;
                        //＝＝＝＝＝接收訂單＝＝＝＝＝//
                        case 1:
                            Intent appointIntent = new Intent(activity, OrderAppointEmployeeActivity.class);
                            appointIntent.putExtra("order_id", order.getOrder_id());
                            startActivity(appointIntent);
                            holder.tvOrderStatus_OrderManagement.setText("送貨中");
                            holder.tvOrderStatus_OrderManagement.setTextColor(Color.RED);
                            holder.btOrderAccept_OrderManagement.setText("查看\n位置");
                            break;
                        //＝＝＝＝＝接收訂單＝＝＝＝＝//
                        //＝＝＝＝＝查看外送員位置＝＝＝＝＝//
                        case 2:

                            break;
                        //＝＝＝＝＝查看外送員位置＝＝＝＝＝//
                        default:
                            break;
                    }
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝接受訂單和查看外送人員位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看訂單詳細資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("order_id", order.getOrder_id());
                    Navigation.findNavController(view).navigate(R.id.action_orderManagementFragment_to_orderDetailFragment, bundle);
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看訂單詳細資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示未完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showOrderAll() { //今天 未完成的訂單
        db.collection("Order").whereGreaterThan("order_status", 0).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orders = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    orders.add(snapshot.toObject(Order.class));
                for (int i = orders.size() - 1; i >= 0; i--) {
                    Order order = orders.get(i);
                    if (!sdf_date.format(order.getOrder_date()).equals(today)) //訂單日期不是今天就刪除
                    {
                        orders.remove(i);
                    }
                }
                rvOrderList_OrderManagement.setAdapter(new OrderManagementFragment.OrderAdapter(activity, orders));
                if (orders.size() == 0) {
                    tvOrderNotFinish_OrderManagement.setText("0");
                } else {
                    tvOrderNotFinish_OrderManagement.setText(orders.size() + "筆");
                }

            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示未完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
