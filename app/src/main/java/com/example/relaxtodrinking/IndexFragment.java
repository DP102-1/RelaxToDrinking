package com.example.relaxtodrinking;
/***************************************************************/
//首頁最新消息輪播
//複合搜尋不能下orderby降序的問題
/***************************************************************/

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.relaxtodrinking.data.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class IndexFragment extends Fragment {
    private String TAG = "首頁";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private Button btUser_Index,btProduct_Index,btOrder_Index,btStore_Index;

    /*********/
    private ImageView ivNews_Index,imageView;
    private Boolean x = false;
    /*********/
    private String user_id = "";
    private String order_id = "無訂單";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    @Override
    public void onStart() {
        super.onStart();
        //＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝//
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
        }
        //＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝//
    }

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
        activity.setTitle("首頁");
        return inflater.inflate(R.layout.fragment_index, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊會員專區＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btUser_Index = view.findViewById(R.id.btUser_Index);
        btUser_Index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id == null || user_id.equals("")) {
                    Common.showToast(activity,"請先登入");
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_userLoginFragment);
                }else
                {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_userManagementFragment);
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊會員專區＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊商品瀏覽＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btProduct_Index = view.findViewById(R.id.btProduct_Index);
        btProduct_Index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!x) {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_productListFragment);
                }else{
                    /*******/
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_productManagementFragment);
                    x = false;
                    /*******/
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊商品瀏覽＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊訂單紀錄＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btOrder_Index = view.findViewById(R.id.btOrder_Index);
        btOrder_Index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                /******/
                if(x) {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_orderManagementFragment);
                    x = false;
                    return;
                }
                /******/


                if (user_id == null || user_id.equals(""))
                {
                    //請先登入
                    //跳轉到登入頁面
                }else
                {
                    db.collection("Order").whereGreaterThan("order_status",0).whereEqualTo("user_id",user_id).get()//先取沒完成的訂單
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Order> orders = new ArrayList<>();
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    orders.add(document.toObject(Order.class));
                                }
                                if  (orders.size() != 0) {
                                    Order order = orders.get(0);
                                    order_id = order.getOrder_id();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("order_id", order_id);
                                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_orderListFragment, bundle);
                                }
                                else
                                {
                                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_orderListFragment);
                                }
                            }else
                            {
                                Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_orderListFragment);
                            }
                        }
                    });
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊訂單紀錄＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊店家資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btStore_Index = view.findViewById(R.id.btStore_Index);
        btStore_Index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!x) {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_storeInformationFragment);
                }else
                {
                    /******/
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_storeManagementFragment);
                    x = false;
                    /*****/
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊店家資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        /*********/
        ivNews_Index = view.findViewById(R.id.ivNews_Index);
        ivNews_Index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!x) {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_newsListFragment);
                }else
                {
                    x = false;
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_newsManagementFragment);
                }
            }
        });

        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!x) {
                    x = true;
                    imageView.setImageResource(R.drawable.map_pin);
                }
                else
                {
                    x = false;
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
            }
        });
        /*********/
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }
}
