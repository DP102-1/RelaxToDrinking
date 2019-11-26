package com.example.relaxtodrinking.user;
/***************************************************************/
// 員工姓名問題

/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import com.example.relaxtodrinking.R;
import com.example.relaxtodrinking.data.Order;
import com.example.relaxtodrinking.data.OrderItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class OrderDetailFragment extends Fragment {
    private String TAG = "訂單詳細";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView rvOrderDetailList_OrderDetail;
    private TextView tvOrderDate_OrderDetail, tvOrderTakeMeal_OrderDetail, tvOrderStatus_OrderDetail, tvOrderUserName_OrderDetail, tvOrderUserPhone_OrderDetail, tvOrderUserAddress_OrderDetail, tvOrderTakeMealTime_OrderDetail, tvOrderTotalPrice_OrderDetail, tvOrderEmployeeName_OrderDetail,tvProductQuantity_OrderDetail;
    private ImageView ivExit_OrderDetail;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月d日 HH點mm分", Locale.CHINESE);
    private List<OrderItem> orderItems;
    private String order_id = "";
    private Order order;

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋訂單資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void loadOrderData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            order_id = bundle.getString("order_id");
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋訂單資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("訂單詳細");
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入訂單資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        loadOrderData();
        tvOrderDate_OrderDetail = view.findViewById(R.id.tvOrderDate_OrderDetail);
        tvOrderTakeMeal_OrderDetail = view.findViewById(R.id.tvOrderTakeMeal_OrderDetail);
        tvOrderStatus_OrderDetail = view.findViewById(R.id.tvOrderStatus_OrderDetail);
        tvOrderUserName_OrderDetail = view.findViewById(R.id.tvOrderUserName_OrderDetail);
        tvOrderUserPhone_OrderDetail = view.findViewById(R.id.tvOrderUserPhone_OrderDetail);
        tvOrderUserAddress_OrderDetail = view.findViewById(R.id.tvOrderUserAddress_OrderDetail);
        tvOrderTakeMealTime_OrderDetail = view.findViewById(R.id.tvOrderTakeMealTime_OrderDetail);
        tvOrderTotalPrice_OrderDetail = view.findViewById(R.id.tvOrderTotalPrice_OrderDetail);
        tvOrderEmployeeName_OrderDetail = view.findViewById(R.id.tvOrderEmployeeName_OrderDetail);
        tvProductQuantity_OrderDetail = view.findViewById(R.id.tvProductQuantity_OrderDetail);

        order = new Order();
        db.collection("Order").document(order_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                order = documentSnapshot.toObject(Order.class);
                tvOrderDate_OrderDetail.setText(sdf.format(order.getOrder_date()));
                tvOrderTakeMeal_OrderDetail.setText(order.getOrder_take_meal());
                int order_status = order.getOrder_status();
                switch (order_status) {
                    case 0:
                        tvOrderStatus_OrderDetail.setText("已完成");
                        break;
                    case 1:
                        tvOrderStatus_OrderDetail.setText("未接單");
                        break;
                    case 2:
                        tvOrderStatus_OrderDetail.setText("送貨中");
                        break;
                    default:
                        tvOrderStatus_OrderDetail.setText("");
                        break;
                }
                tvOrderUserName_OrderDetail.setText(order.getUser_name());
                tvOrderUserPhone_OrderDetail.setText(order.getUser_phone());
                tvOrderUserAddress_OrderDetail.setText(order.getUser_address());
                tvOrderTakeMealTime_OrderDetail.setText(sdf.format(order.getOrder_take_meal_time()));
                tvOrderTotalPrice_OrderDetail.setText("$NT " + String.valueOf(order.getOrder_price()));
                tvOrderEmployeeName_OrderDetail.setText(order.getEmp_id());
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入訂單資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvOrderDetailList_OrderDetail = view.findViewById(R.id.rvOrderDetailList_OrderDetail);
        rvOrderDetailList_OrderDetail.setLayoutManager(new LinearLayoutManager(activity));
        showOrderDetailAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_OrderDetail = view.findViewById(R.id.ivExit_OrderDetail);
        ivExit_OrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單明細列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class OrderItemAdapter extends RecyclerView.Adapter<OrderDetailFragment.OrderItemAdapter.MyViewHolder> {
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
        public OrderDetailFragment.OrderItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.order_detail_view, parent, false);
            return new OrderDetailFragment.OrderItemAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderDetailFragment.OrderItemAdapter.MyViewHolder holder, int position) {
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
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    orderItems.add(snapshot.toObject(OrderItem.class));
                    tvProductQuantity_OrderDetail.setText("共 "+String.valueOf(orderItems.size())+" 項商品");
                }
                rvOrderDetailList_OrderDetail.setAdapter(new OrderDetailFragment.OrderItemAdapter(activity, orderItems));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示訂單明細列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
