package com.example.relaxtodrinking;
/***************************************************************/


/***************************************************************/

import android.app.Activity;
import android.content.Context;
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

import com.example.relaxtodrinking.data.Order;
import com.example.relaxtodrinking.data.OrderItem;
import com.example.relaxtodrinking.data.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class OrderListFragment extends Fragment {
    private String TAG = "訂單瀏覽";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView rvOrderDetailList_OrderList;

    private String order_id = "";
    private Order order = new Order();
    private List<OrderItem> orderItems;
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
        activity.setTitle("訂單瀏覽");
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvOrderDetailList_OrderList = view.findViewById(R.id.rvOrderDetailList_OrderList);
        rvOrderDetailList_OrderList.setLayoutManager(new LinearLayoutManager(activity));
        showOrderDetailAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        db.collection("Order").document(order_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                order = documentSnapshot.toObject(Order.class);
            }
        });
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
        db.collection("OrderItem").whereEqualTo("order_id",order_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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