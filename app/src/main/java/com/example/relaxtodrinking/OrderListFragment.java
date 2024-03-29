package com.example.relaxtodrinking;
/***************************************************************/


/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import com.example.relaxtodrinking.data.Employee;
import com.example.relaxtodrinking.data.Order;
import com.example.relaxtodrinking.data.OrderItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class OrderListFragment extends Fragment {
    private String TAG = "訂單瀏覽";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private RecyclerView rvOrderDetailList_OrderList;
    private ImageView ivBack_OrderList;
    private TextView tvOrderDate_OrderList, tvOrderTakeMealTime_OrderList, tvOrderStatus_OrderList, tvOrderTotalPrice_OrderDetail, tvOrderTakeMeal_OrderList;
    private Button btOrderQRCode_OrderList, btEmployeePosition_OrderList, btOrderHistory_OrderList, btOrderDetail_OrderList;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月d日 HH點mm分", Locale.CHINESE);
    private String order_id = "無訂單";
    private String user_id = "";
    private Order order = new Order();
    private List<OrderItem> orderItems;

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
        activity.setTitle("訂單瀏覽");
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
            Log.e(TAG, user.getUid());
        } else {
            user_id = "";
        }
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvOrderDetailList_OrderList = view.findViewById(R.id.rvOrderDetailList_OrderList);
        rvOrderDetailList_OrderList.setLayoutManager(new LinearLayoutManager(activity));
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入訂單資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        tvOrderDate_OrderList = view.findViewById(R.id.tvOrderDate_OrderList);
        tvOrderTakeMealTime_OrderList = view.findViewById(R.id.tvOrderTakeMealTime_OrderList);
        tvOrderStatus_OrderList = view.findViewById(R.id.tvOrderStatus_OrderList);
        tvOrderTotalPrice_OrderDetail = view.findViewById(R.id.tvOrderTotalPrice_OrderDetail);
        tvOrderTakeMeal_OrderList = view.findViewById(R.id.tvOrderTakeMeal_OrderList);

        btOrderQRCode_OrderList = view.findViewById(R.id.btOrderQRCode_OrderList);
        btEmployeePosition_OrderList = view.findViewById(R.id.btEmployeePosition_OrderList);
        btOrderDetail_OrderList = view.findViewById(R.id.btOrderDetail_OrderList);

        db.collection("Order").whereGreaterThan("order_status", 0).whereEqualTo("user_id", user_id).get()//先取沒完成的訂單.whereGreaterThan("order_status", 0)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Order> orders = new ArrayList<>();
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                orders.add(document.toObject(Order.class));
                            }
                            if (orders.size() != 0) {
                                order = orders.get(0);
                                order_id = order.getOrder_id();
                            } else {
                                order_id = "無訂單";
                            }
                            //＝＝＝＝＝判別該使用者有沒有進行中訂單＝＝＝＝＝＝//
                            Log.e(TAG,order_id);
                            if (order_id.equals("無訂單")) //判別有沒有order_id
                            {
                                btOrderQRCode_OrderList.setEnabled(false);
                                btEmployeePosition_OrderList.setEnabled(false);
                                btOrderDetail_OrderList.setEnabled(false);
                                tvOrderStatus_OrderList.setText("無進行中訂單");
                            } else {
                                db.collection("Order").document(order_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult() != null) {
                                            DocumentSnapshot document = task.getResult();
                                            order = document.toObject(Order.class);
                                        }
                                        tvOrderDate_OrderList.setText(sdf.format(order.getOrder_date()));
                                        tvOrderTakeMealTime_OrderList.setText(sdf.format(order.getOrder_take_meal_time()));
                                        int order_status = order.getOrder_status();
                                        switch (order_status) {
                                            case 0:
                                                tvOrderStatus_OrderList.setText("已完成");
                                                tvOrderStatus_OrderList.setTextColor(Color.BLUE);
                                                break;
                                            case 1:
                                                tvOrderStatus_OrderList.setText("未出貨");
                                                tvOrderStatus_OrderList.setTextColor(Color.GRAY);
                                                break;
                                            case 2:
                                                tvOrderStatus_OrderList.setText("送貨中");
                                                tvOrderStatus_OrderList.setTextColor(Color.RED);
                                                break;
                                            default:
                                                tvOrderStatus_OrderList.setText("");
                                                tvOrderStatus_OrderList.setTextColor(Color.GRAY);
                                                break;
                                        }
                                        tvOrderTotalPrice_OrderDetail.setText("$NT " + String.valueOf(order.getOrder_price()));
                                        tvOrderTakeMeal_OrderList.setText(order.getOrder_take_meal());
                                    }
                                });
                                showOrderDetailAll();
                            }
                            //＝＝＝＝＝判別該使用者有沒有進行中訂單＝＝＝＝＝＝//
                        }
                    }
                });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入訂單資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊訂單QRCode顯示＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btOrderQRCode_OrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent QRcodeIntent = new Intent(activity, OrderQRCodeActivity.class);
                QRcodeIntent.putExtra("order_id", order.getOrder_id());
                startActivity(QRcodeIntent);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊訂單QRCode顯示＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看外送員位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btEmployeePosition_OrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (order.getOrder_status() == 2)
                {
                    db.collection("Employee").document(order.getEmp_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Employee employee = new Employee();
                            if (task.getResult() != null) {
                                employee = task.getResult().toObject(Employee.class);
                                if (employee.getEmp_position() == null) {
                                    Common.showToast(activity,"該外送員尚未開啟定位");
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("action","會員");
                                    bundle.putString("emp_id",order.getEmp_id());
                                    Navigation.findNavController(view).navigate(R.id.action_orderListFragment_to_deliveryPositionFragment,bundle);
                                }
                            }
                        }
                    });
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看外送員位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看歷史訂單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btOrderHistory_OrderList = view.findViewById(R.id.btOrderHistory_OrderList);
        btOrderHistory_OrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_orderListFragment_to_orderHistoryFragment);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看歷史訂單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊訂單詳情＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btOrderDetail_OrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("order_id", order_id);
                Navigation.findNavController(view).navigate(R.id.action_orderListFragment_to_orderDetailFragment, bundle);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊訂單詳情＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_OrderList = view.findViewById(R.id.ivBack_OrderList);
        ivBack_OrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_orderListFragment_to_indexFragment);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單明細列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class OrderItemAdapter extends RecyclerView.Adapter<OrderListFragment.OrderItemAdapter.MyViewHolder> {
        Context context;
        List<OrderItem> orderItems;

        public OrderItemAdapter(Context context, List<OrderItem> orderItems) {
            this.context = context;
            this.orderItems = orderItems;
        }

        @Override
        public int getItemCount() {
            return orderItems.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvProductName_OrderDetail, tvQuantity_OrderDetail, tvCapacity_OrderDetail, tvPrice_OrderDetail, tvTemperature_OrderDetail, tvSweetness_OrderDetail, tvProductTotal_OrderDetail;

            public MyViewHolder(View OrderItemView) {
                super(OrderItemView);
                tvProductName_OrderDetail = OrderItemView.findViewById(R.id.tvProductName_OrderDetail);
                tvQuantity_OrderDetail = OrderItemView.findViewById(R.id.tvQuantity_OrderDetail);
                tvCapacity_OrderDetail = OrderItemView.findViewById(R.id.tvCapacity_OrderDetail);
                tvPrice_OrderDetail = OrderItemView.findViewById(R.id.tvPrice_OrderDetail);
                tvTemperature_OrderDetail = OrderItemView.findViewById(R.id.tvTemperature_OrderDetail);
                tvSweetness_OrderDetail = OrderItemView.findViewById(R.id.tvSweetness_OrderDetail);
                tvProductTotal_OrderDetail = OrderItemView.findViewById(R.id.tvProductTotal_OrderDetail);
            }
        }

        @NonNull
        @Override
        public OrderListFragment.OrderItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.order_detail_view, parent, false);
            return new OrderListFragment.OrderItemAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderListFragment.OrderItemAdapter.MyViewHolder holder, int position) {
            final OrderItem orderItem = orderItems.get(position);

            int proPrice = (orderItem.getPro_capacity().equals("M")) ? orderItem.getPro_price_M() : orderItem.getPro_price_L();
            int proQuantity = orderItem.getPro_quantity();
            int proTotal = proPrice * proQuantity;

            holder.tvProductName_OrderDetail.setText(orderItem.getPro_name()); //抓商品名稱
            holder.tvQuantity_OrderDetail.setText("*" + String.valueOf(orderItem.getPro_quantity())); //抓商品數量
            holder.tvCapacity_OrderDetail.setText(orderItem.getPro_capacity()); //抓商品大小
            holder.tvPrice_OrderDetail.setText("$NT" + String.valueOf(proPrice)); //抓商品價格
            holder.tvTemperature_OrderDetail.setText(orderItem.getPro_temperature()); //抓商品溫度
            holder.tvSweetness_OrderDetail.setText(orderItem.getPro_sweetness()); //抓商品甜度
            holder.tvProductTotal_OrderDetail.setText(String.valueOf(proTotal)); //抓商品總價
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單明細列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示訂單明細列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showOrderDetailAll() {
        db.collection("OrderItem").whereEqualTo("order_id", order_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orderItems = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    orderItems.add(snapshot.toObject(OrderItem.class));
                rvOrderDetailList_OrderList.setAdapter(new OrderListFragment.OrderItemAdapter(activity, orderItems));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示訂單明細列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
