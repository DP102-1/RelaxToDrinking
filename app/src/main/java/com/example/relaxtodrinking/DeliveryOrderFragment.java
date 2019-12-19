package com.example.relaxtodrinking;
/***************************************************************/
//導航中文化
//起始位置定位
/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeliveryOrderFragment extends Fragment {
    private String TAG = "外送員訂單管理";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private EditText etTimeOrder_DeliveryOrder;
    private Button btNotFinish_DeliveryOrder,btFinish_DeliveryOrder;
    private RecyclerView rvDeliveryOrder_DeliveryOrder;
    private ImageView ivBack_DeliveryOrder;

    private String order_id;
    private String emp_id;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月d日 HH點mm分", Locale.CHINESE);
    private SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy年MM月d日", Locale.CHINESE);
    private String today;
    private Boolean isOrderShowNotFinish = true;
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
        activity.setTitle(TAG);
        return inflater.inflate(R.layout.fragment_delivery_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝取得今天日期＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        today = sdf_date.format(new Date());
        etTimeOrder_DeliveryOrder = view.findViewById(R.id.etTimeOrder_DeliveryOrder);
        etTimeOrder_DeliveryOrder.setText(sdf.format(new Date()));
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝取得今天日期＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝取得外送員資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        Bundle bundle = getArguments();
        if (bundle != null) {
            emp_id = bundle.getString("emp_id");
        }
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝取得外送員資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入未完成訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btNotFinish_DeliveryOrder = view.findViewById(R.id.btNotFinish_DeliveryOrder);
        btFinish_DeliveryOrder = view.findViewById(R.id.btFinish_DeliveryOrder);
        //設定初始為未完成訂單列表
        btNotFinish_DeliveryOrder.setBackgroundResource(R.drawable.kind_button);

        rvDeliveryOrder_DeliveryOrder = view.findViewById(R.id.rvDeliveryOrder_DeliveryOrder);
        rvDeliveryOrder_DeliveryOrder.setLayoutManager(new LinearLayoutManager(activity));
        showOrderNotFinishAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入未完成訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//



        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊今日未完成訂單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btNotFinish_DeliveryOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //設定格式
                btNotFinish_DeliveryOrder.setBackgroundResource(R.drawable.kind_button);
                btFinish_DeliveryOrder.setBackgroundResource(R.drawable.index_button);
                isOrderShowNotFinish = true;
                //設定格式
                showOrderNotFinishAll();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊今日未完成訂單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//



        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊今日已完成訂單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btFinish_DeliveryOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //設定格式
                btNotFinish_DeliveryOrder.setBackgroundResource(R.drawable.index_button);
                btFinish_DeliveryOrder.setBackgroundResource(R.drawable.kind_button);
                isOrderShowNotFinish = false;
                //設定格式
                showOrderFinishAll();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊今日已完成訂單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_DeliveryOrder = view.findViewById(R.id.ivBack_DeliveryOrder);
        ivBack_DeliveryOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class OrderAdapter extends RecyclerView.Adapter<DeliveryOrderFragment.OrderAdapter.MyViewHolder> {
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
            private TextView tvOrderTime_DeliveryOrder,tvName_DeliveryOrder,tvPhone_DeliveryOrder,tvAddress_DeliveryOrder;
            private Button btOrderDetail_DeliveryOrder,btOrderAddress_DeliveryOrder,btQRCode_DeliveryOrder;

            public MyViewHolder(View OrderView) {
                super(OrderView);
                tvOrderTime_DeliveryOrder = OrderView.findViewById(R.id.tvOrderTime_DeliveryOrder);
                tvName_DeliveryOrder = OrderView.findViewById(R.id.tvName_DeliveryOrder);
                tvPhone_DeliveryOrder = OrderView.findViewById(R.id.tvPhone_DeliveryOrder);
                tvAddress_DeliveryOrder = OrderView.findViewById(R.id.tvAddress_DeliveryOrder);
                btOrderDetail_DeliveryOrder = OrderView.findViewById(R.id.btOrderDetail_DeliveryOrder);
                btOrderAddress_DeliveryOrder = OrderView.findViewById(R.id.btOrderAddress_DeliveryOrder);
                btQRCode_DeliveryOrder = OrderView.findViewById(R.id.btQRCode_DeliveryOrder);
            }
        }

        @Override
        public DeliveryOrderFragment.OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.delivery_order_view, parent, false);
            return new DeliveryOrderFragment.OrderAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final DeliveryOrderFragment.OrderAdapter.MyViewHolder holder, int position) {
            final Order order = orders.get(position);
            if (position % 2 != 0) //顏色交互
            {
                holder.itemView.setBackgroundColor(Color.parseColor("#F1F7D5"));
            }
            //＝＝＝＝＝如果訂單列表是已完成 全部按鈕不能點＝＝＝＝＝//
            if (isOrderShowNotFinish)
            {
                holder.btOrderAddress_DeliveryOrder.setEnabled(true);
                holder.btQRCode_DeliveryOrder.setEnabled(true);
            }else
            {
                holder.btOrderAddress_DeliveryOrder.setEnabled(false);
                holder.btQRCode_DeliveryOrder.setEnabled(false);
            }
            //＝＝＝＝＝如果訂單列表是已完成 全部按鈕不能點＝＝＝＝＝//
            holder.tvOrderTime_DeliveryOrder.setText(sdf.format(order.getOrder_take_meal_time()));
            holder.tvName_DeliveryOrder.setText(order.getUser_name());
            holder.tvPhone_DeliveryOrder.setText(order.getUser_phone());
            holder.tvAddress_DeliveryOrder.setText(order.getUser_address());


            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看訂單詳情＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            holder.btOrderDetail_DeliveryOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("order_id",order.getOrder_id());
                    Navigation.findNavController(view).navigate(R.id.action_deliveryOrderFragment_to_orderDetailFragment,bundle);
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看訂單詳情＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看訂單位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            holder.btOrderAddress_DeliveryOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String endPoint = order.getUser_address();
                    if (endPoint.isEmpty()) {
                        Common.showToast(activity, "訂單地址為空");
                        return;
                    }
                    db.collection("Employee").document(emp_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Employee employee = new Employee();
                            if (task.getResult() != null) {
                                employee = task.getResult().toObject(Employee.class);
                                if (employee.getEmp_position() == null)
                                {
                                    Common.showToast(activity,"找不到定位資料,請先至外送員管理點選『查看定位』");
                                }
                                else
                                {
                                    GeoPoint addressStartPoint = employee.getEmp_position();
                                    double fromLat = addressStartPoint.getLatitude();
                                    double fromLng = addressStartPoint.getLongitude();
                                    Address addressEndPoint = getAddress(endPoint);
                                    if (addressEndPoint == null) {
                                        Common.showToast(activity,"找不到該訂單地址");
                                        return;
                                    }
                                    double toLat = addressEndPoint.getLatitude();
                                    double toLng = addressEndPoint.getLongitude();
                                    direct(fromLat, fromLng, toLat, toLng);
                                }
                            }
                        }
                    });
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊查看訂單位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊開啟QRCODE掃描＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            holder.btQRCode_DeliveryOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    order_id = order.getOrder_id();
                    Log.e(TAG,order.getOrder_id());
                    /* 若在Activity內需要呼叫IntentIntegrator(Activity)建構式建立IntentIntegrator物件；
                     * 而在Fragment內需要呼叫IntentIntegrator.forSupportFragment(Fragment)建立物件，
                     * 掃瞄完畢時，Fragment.onActivityResult()才會被呼叫 */
                    // IntentIntegrator integrator = new IntentIntegrator(this);
                    IntentIntegrator integrator = IntentIntegrator.forSupportFragment(DeliveryOrderFragment.this);
                    // Set to true to enable saving the barcode image and sending its path in the result Intent.
                    integrator.setBarcodeImageEnabled(true); //保存圖像的路徑
                    // Set to false to disable beep on scan.
                    integrator.setBeepEnabled(false); //要不要BB叫
                    // Use the specified camera ID.
                    integrator.setCameraId(0); //前鏡頭還後鏡頭
                    // By default, the orientation is locked. Set to false to not lock.
                    integrator.setOrientationLocked(false);
                    // Set a prompt to display on the capture screen.
                    integrator.setPrompt("抓取成功,比對中"); //抓到之後要顯示啥
                    // Initiates a scan
                    integrator.initiateScan();
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊開啟QRCODE掃描＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝取得地址並開啟導航＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Address getAddress(String locationName) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        if (addressList == null || addressList.isEmpty()) {
            return null;
        } else {
            return addressList.get(0);
        }
    }


    private void direct(double fromLat, double fromLng, double toLat,
                        double toLng) {
        String uriStr = String.format(Locale.CHINESE,
                "https://www.google.com/maps/dir/?api=1" +
                        "&origin=%f,%f&destination=%f,%f&travelmode=driving&language=zh-TW",
                fromLat, fromLng, toLat, toLng);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uriStr));
        intent.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝取得地址並開啟導航＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝掃描QRCode回傳的結果＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null && intentResult.getContents() != null) {
            if (order_id.equals(intentResult.getContents()))
            {
                db.collection("Order").document(order_id).update("order_status",0);
                showOrderNotFinishAll();
                Common.showToast(activity,"完成訂單！");
            }else
            {
                Log.e(TAG,order_id+"不等於"+intentResult.getContents());
                Common.showToast(activity,"訂單不正確,請重新掃描");
            }
        } else {
            Common.showToast(activity,"已取消掃描");
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝掃描QRCode回傳的結果＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示未完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showOrderNotFinishAll() { //該外送員 今天 狀態為2的訂單
        Log.e(TAG,emp_id);
        db.collection("Order").whereEqualTo("emp_id",emp_id).whereEqualTo("order_status", 2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Order> orders = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    orders.add(snapshot.toObject(Order.class));
                for (int i = orders.size() - 1; i >= 0; i--) {
                    Order order = orders.get(i);
                    if (!sdf_date.format(order.getOrder_date()).equals(today)) //訂單日期不是今天就刪除
                    {
                        orders.remove(i);
                    }
                }
                rvDeliveryOrder_DeliveryOrder.setAdapter(new DeliveryOrderFragment.OrderAdapter(activity, orders));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示未完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示已完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showOrderFinishAll() { //該外送員 今天 狀態為0的訂單
        Log.e(TAG,emp_id);
        db.collection("Order").whereEqualTo("emp_id",emp_id).whereEqualTo("order_status", 0).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Order> orders = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                    orders.add(snapshot.toObject(Order.class));
                for (int i = orders.size() - 1; i >= 0; i--) {
                    Order order = orders.get(i);
                    if (!sdf_date.format(order.getOrder_date()).equals(today)) //訂單日期不是今天就刪除
                    {
                        orders.remove(i);
                    }
                }
                Log.e(TAG,"有"+orders.size());
                rvDeliveryOrder_DeliveryOrder.setAdapter(new DeliveryOrderFragment.OrderAdapter(activity, orders));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示已完成的訂單列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
