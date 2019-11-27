package com.example.relaxtodrinking;
/***************************************************************/
//日期區間搜尋下語法問題

/***************************************************************/

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.Query;
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
    private final static int STATUS_Order = 0;
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView rvOrderHistory_OrderSearch;
    private SearchView rvSearch_OrderSearch;
    private Spinner spSearchMeans_OrderSearch;
    private ImageView ivBack_OrderSearch;
    private Button btStartDate_OrderSearch, btEndDate_OrderSearch;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);
    private List<Order> orders;
    private List<Order> orders_scearch;
    private Date start_date, end_date;
    Calendar calendar = Calendar.getInstance();
    private int year, month, day, hour, minute;


    //  private String search_means = "用電話搜尋";
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
        activity.setTitle("歷史訂單查詢");
        return inflater.inflate(R.layout.fragment_order_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvOrderHistory_OrderSearch = view.findViewById(R.id.rvOrderHistory_OrderSearch);
        rvOrderHistory_OrderSearch.setLayoutManager(new LinearLayoutManager(activity));
        showOrderAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入顯示日期＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btStartDate_OrderSearch = view.findViewById(R.id.btStartDate_OrderSearch);
        btEndDate_OrderSearch = view.findViewById(R.id.btEndDate_OrderSearch);
        end_date = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -365);
        start_date = calendar.getTime();

        btStartDate_OrderSearch.setText(sdf.format(start_date));
        btEndDate_OrderSearch.setText(sdf.format(end_date));
        //＝＝＝＝＝設定開始日期＝＝＝＝＝//
        btStartDate_OrderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(
                        activity,
                        OrderSearchFragment.this,
                        year, month, day)
                        .show();
                Date new_start_date = null;
                try {
                    new_start_date = sdf.parse(year +"年"+pad(month)+"月"+day);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (new_start_date.before(end_date))//如果日期比end大

                {
                    start_date = new_start_date;
                }else
                {
                    Common.showToast(activity,"起始日期大於結束日期,無法查詢");
                }
                btStartDate_OrderSearch.setText(sdf.format(start_date));
            }
        });
        //＝＝＝＝＝設定開始日期＝＝＝＝＝//


        //＝＝＝＝＝設定結束日期＝＝＝＝＝//
        btEndDate_OrderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(
                        activity,
                        OrderSearchFragment.this,
                        year, month, day)
                        .show();
                Date new_end_date = null;
                try {
                    new_end_date = sdf.parse(year +"年"+pad(month)+"月"+day);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (new_end_date.before(start_date))//如果日期比start小

                {
                    end_date = new_end_date;
                }else
                {
                    Common.showToast(activity,"結束日期小於起始日期,無法查詢");
                }
                btEndDate_OrderSearch.setText(sdf.format(end_date));
            }
        });
        //＝＝＝＝＝設定結束日期＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入顯示日期＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


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
        db.collection("Order").orderBy("order_date", Query.Direction.DESCENDING).whereEqualTo("order_status", STATUS_Order).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orders = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    orders.add(snapshot.toObject(Order.class));
                rvOrderHistory_OrderSearch.setAdapter(new OrderSearchFragment.OrderAdapter(activity, orders));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示已完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示經過日期搜尋後的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showSearchAll() {

    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示經過日期搜尋後的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝設定查詢時間＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
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
