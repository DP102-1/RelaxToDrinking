package com.example.relaxtodrinking;
/***************************************************************/


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
import android.widget.TextView;

import com.example.relaxtodrinking.data.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月d日 HH點mm分", Locale.CHINESE);
    private List<Order> orders;
    private int order_status;
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
        activity.setTitle("訂單管理");
        return inflater.inflate(R.layout.fragment_order_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvOrderList_OrderManagement = view.findViewById(R.id.rvOrderList_OrderManagement);
        rvOrderList_OrderManagement.setLayoutManager(new LinearLayoutManager(activity));
        showOrderAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

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
            holder.tvOrderNumber_OrderManagement.setText("#"+String.valueOf(position));
            holder.tvOrderDate_OrderManagement.setText(sdf.format(order.getOrder_date()));
            holder.tvUserName_OrderManagement.setText(order.getUser_name());
            holder.tvUserPhone_OrderManagement.setText(order.getUser_phone());
            holder.tvUserAddress_OrderManagement.setText(order.getUser_address());
            order_status = order.getOrder_status();
            switch (order_status) {
                case 0:
                    holder.tvOrderStatus_OrderManagement.setText("已完成");
                    holder.tvOrderStatus_OrderManagement.setTextColor(Color.BLUE);
                case 1:
                    holder.tvOrderStatus_OrderManagement.setText("未接單");
                    holder.tvOrderStatus_OrderManagement.setTextColor(Color.GRAY);
                case 2:
                    holder.tvOrderStatus_OrderManagement.setText("送貨中");
                    holder.tvOrderStatus_OrderManagement.setTextColor(Color.RED);
                default:
                    holder.tvOrderStatus_OrderManagement.setText("");
                    holder.tvOrderStatus_OrderManagement.setTextColor(Color.GRAY);
            }
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝接受訂單和查看外送人員位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            holder.btOrderAccept_OrderManagement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (order_status) {
                        case 0:
                            Common.showToast(activity,"該訂單已完成");
                            break;
                        //＝＝＝＝＝接收訂單＝＝＝＝＝//
                        case 1:




                            holder.tvOrderStatus_OrderManagement.setText("送貨中");
                            holder.tvOrderStatus_OrderManagement.setTextColor(Color.RED);
                            order_status = 2;
                            holder.btOrderAccept_OrderManagement.setText("查看外送員位置");
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
                    bundle.putString("order_id",order.getOrder_id());
                    Navigation.findNavController(view).navigate(R.id.action_orderManagementFragment_to_orderDetailFragment,bundle);
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看訂單詳細資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示未完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showOrderAll() {
        db.collection("Order").orderBy("order_date", Query.Direction.DESCENDING).whereEqualTo("order_status", STATUS_Order).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orders = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    orders.add(snapshot.toObject(Order.class));
                rvOrderList_OrderManagement.setAdapter(new OrderManagementFragment.OrderAdapter(activity, orders));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示未完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
