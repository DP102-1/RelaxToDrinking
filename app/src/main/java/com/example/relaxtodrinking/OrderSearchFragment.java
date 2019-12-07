package com.example.relaxtodrinking;
/***************************************************************/
//日期區間搜尋下語法問題

/***************************************************************/

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderSearchFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private String TAG = "歷史訂單查詢";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView rvOrderHistory_OrderSearch;
    private ImageView ivBack_OrderSearch;
    private Button btDate_OrderSearch, btShowAll_OrderSearch;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);
    private List<Order> orders;
    private Date date;
    private Calendar calendar = Calendar.getInstance();
    private String action = "顯示全部";
    private int year, month, day;

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
        activity.setTitle(TAG);
        return inflater.inflate(R.layout.fragment_order_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btDate_OrderSearch = view.findViewById(R.id.btDate_OrderSearch);
        rvOrderHistory_OrderSearch = view.findViewById(R.id.rvOrderHistory_OrderSearch);
        rvOrderHistory_OrderSearch.setLayoutManager(new LinearLayoutManager(activity));
        showNow();
        action = "顯示全部";
        btDate_OrderSearch.setText("全部");
        showOrderAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點選顯示日期＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btDate_OrderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(
                        activity,
                        OrderSearchFragment.this,
                        year, month, day)
                        .show();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點選顯示日期＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點選顯示全部＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btShowAll_OrderSearch = view.findViewById(R.id.btShowNow_OrderSearch);
        btShowAll_OrderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = "顯示全部";
                btDate_OrderSearch.setText("全部");
                showOrderAll();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點選顯示全部＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_OrderSearch = view.findViewById(R.id.ivBack_OrderSearch);
        ivBack_OrderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class OrderAdapter extends RecyclerView.Adapter<OrderSearchFragment.OrderAdapter.MyViewHolder> {
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
            private TextView tvOrderDate_OrderHistory, tvOrderTakeMeal_OrderHistory, tvOrderTotal_OrderHistory;

            public MyViewHolder(View OrderView) {
                super(OrderView);
                tvOrderDate_OrderHistory = OrderView.findViewById(R.id.tvOrderDate_OrderHistory);
                tvOrderTakeMeal_OrderHistory = OrderView.findViewById(R.id.tvOrderTakeMeal_OrderHistory);
                tvOrderTotal_OrderHistory = OrderView.findViewById(R.id.tvOrderTotal_OrderHistory);
            }
        }

        @NonNull
        @Override
        public OrderSearchFragment.OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.order_history_view, parent, false);
            return new OrderSearchFragment.OrderAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderSearchFragment.OrderAdapter.MyViewHolder holder, int position) {
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
                    Navigation.findNavController(view).navigate(R.id.action_orderSearchFragment_to_orderDetailFragment, bundle);
                }
            });
            //＝＝＝＝＝查看訂單詳細資訊＝＝＝＝＝//
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示已完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showOrderAll() {
        db.collection("Order").whereEqualTo("order_status", 0).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orders = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    orders.add(snapshot.toObject(Order.class));
                }
                if (action.equals("顯示查詢結果")) {
                    for (int i = orders.size() - 1; i >= 0; i--) {
                        if (!sdf.format(orders.get(i).getOrder_date()).equals(sdf.format(date))) {
                            orders.remove(i);
                        }
                    }
                }
                rvOrderHistory_OrderSearch.setAdapter(new OrderSearchFragment.OrderAdapter(activity, orders));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示已完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝設定查詢時間＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showNow() { //取得現在的時間
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    private void updateDisplay() {
        try {
            date = sdf.parse(year + "年" + pad(month+1) + "月" + day + "日");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        btDate_OrderSearch.setText(sdf.format(date));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        updateDisplay();
        action = "顯示查詢結果";
        showOrderAll();
    }

    /* 若數字有十位數，直接顯示；
       若只有個位數則補0後再顯示，例如7會改成07後再顯示 */
    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + String.valueOf(number);
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝設定查詢時間＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
