package com.example.relaxtodrinking.user;


import android.app.Activity;
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

import com.example.relaxtodrinking.Common;
import com.example.relaxtodrinking.R;
import com.example.relaxtodrinking.data.User;
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
import java.util.regex.Pattern;


public class UserEditInfoFragment extends Fragment {
    private String TAG = "修改個人資料";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private ImageView ivBack_UserEditInfo,ivName_UserEditInfo,ivPhone_UserEditInfo,ivAddress_UserEditInfo;
    private TextView tvEmail_UserEditInfo,tvErrorName_UserEditInfo,tvErrorPhone_UserEditInfo,tvErrorAddress_UserEditInfo;
    private EditText etName_UserEditInfo,etPhone_UserEditInfo,etAddress_UserEditInfo;
    private Button btSubmit_UserEditInfo;

    private String user_id = "";
    private Boolean isErrorName = false,isErrorPhone = false,isErrorAddress = false;
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
        return inflater.inflate(R.layout.fragment_user_edit_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
        }else{
            user_id = "";
        }
        Log.e(TAG,user_id);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etName_UserEditInfo = view.findViewById(R.id.etName_UserEditInfo);
        etPhone_UserEditInfo = view.findViewById(R.id.etPhone_UserEditInfo);
        etAddress_UserEditInfo = view.findViewById(R.id.etAddress_UserEditInfo);
        tvEmail_UserEditInfo = view.findViewById(R.id.tvEmail_UserEditInfo);
        tvErrorName_UserEditInfo = view.findViewById(R.id.tvErrorName_UserEditInfo);
        tvErrorPhone_UserEditInfo = view.findViewById(R.id.tvErrorPhone_UserEditInfo);
        tvErrorAddress_UserEditInfo = view.findViewById(R.id.tvErrorAddress_UserEditInfo);
        ivName_UserEditInfo = view.findViewById(R.id.ivName_UserEditInfo);
        ivPhone_UserEditInfo = view.findViewById(R.id.ivPhone_UserEditInfo);
        ivAddress_UserEditInfo = view.findViewById(R.id.ivAddress_UserEditInfo);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入使用者資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        if(auth.getCurrentUser() != null) {
            tvEmail_UserEditInfo.setText(auth.getCurrentUser().getEmail());
        }
        db.collection("User").whereEqualTo("user_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> users = new ArrayList<>();
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        users.add(document.toObject(User.class));
                    }
                    User user = users.get(0);
                    etName_UserEditInfo.setText(user.getUser_name());
                    etPhone_UserEditInfo.setText(user.getUser_phone());
                    etAddress_UserEditInfo.setText(user.getUser_address());
                    //以下6行修正一開始跳UI驗證錯誤問題
                    tvErrorName_UserEditInfo.setVisibility(View.GONE);
                    ivName_UserEditInfo.setVisibility(View.GONE);
                    tvErrorPhone_UserEditInfo.setVisibility(View.GONE);
                    ivPhone_UserEditInfo.setVisibility(View.GONE);
                    tvErrorAddress_UserEditInfo.setVisibility(View.GONE);
                    ivAddress_UserEditInfo.setVisibility(View.GONE);
                }else
                {
                        user_id = "";
                        Log.e(TAG,"找不到使用者id");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入使用者資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證姓名＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etName_UserEditInfo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (etName_UserEditInfo.getText().toString().isEmpty()) {
                        tvErrorName_UserEditInfo.setVisibility(View.VISIBLE);
                        tvErrorName_UserEditInfo.setText("姓名不得為空");
                        ivName_UserEditInfo.setVisibility(View.VISIBLE);
                        isErrorName = true;
                    } else {
                        tvErrorName_UserEditInfo.setVisibility(View.GONE);
                        ivName_UserEditInfo.setVisibility(View.GONE);
                        isErrorName = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證姓名＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證手機號碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etPhone_UserEditInfo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (etPhone_UserEditInfo.getText().toString().isEmpty()) {
                    isErrorPhone = true;
                    tvErrorPhone_UserEditInfo.setText("手機號碼不得為空");
                }
                if (!b) {
                    String pattern = "09[0-9]{8}";
                    if (!(Pattern.compile(pattern).matcher(etPhone_UserEditInfo.getText().toString().trim()).matches())) {
                        tvErrorPhone_UserEditInfo.setVisibility(View.VISIBLE);
                        tvErrorPhone_UserEditInfo.setText("手機號碼格式不正確");
                        ivPhone_UserEditInfo.setVisibility(View.VISIBLE);
                        isErrorPhone = true;
                    } else {
                        tvErrorPhone_UserEditInfo.setVisibility(View.GONE);
                        ivPhone_UserEditInfo.setVisibility(View.GONE);
                        isErrorPhone = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證手機號碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證地址＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etAddress_UserEditInfo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (etAddress_UserEditInfo.getText().toString().isEmpty()) {
                        tvErrorAddress_UserEditInfo.setVisibility(View.VISIBLE);
                        tvErrorAddress_UserEditInfo.setText("地址不得為空");
                        ivAddress_UserEditInfo.setVisibility(View.VISIBLE);
                        isErrorAddress = true;
                    } else {
                        tvErrorAddress_UserEditInfo.setVisibility(View.GONE);
                        ivAddress_UserEditInfo.setVisibility(View.GONE);
                        isErrorAddress = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證地址＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定修改＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_UserEditInfo = view.findViewById(R.id.btSubmit_UserEditInfo);
        btSubmit_UserEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.e(TAG,user_id);
                if (isAllNotError())
                {
                    User user = new User();
                    user.setUser_id(user_id);
                    user.setUser_name(etName_UserEditInfo.getText().toString().trim());
                    user.setUser_phone(etPhone_UserEditInfo.getText().toString().trim());
                    user.setUser_address(etAddress_UserEditInfo.getText().toString().trim());
                    db.collection("User").document(user.getUser_id()).set(user);
                    Common.showToast(activity,"個人資料修改成功");
                    Navigation.findNavController(view).popBackStack();
                }else{
                    Common.showToast(activity,"有資料格式輸入不正確");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定修改＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_UserEditInfo = view.findViewById(R.id.ivBack_UserEditInfo);
        ivBack_UserEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷資料格式是否正確＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private boolean isAllNotError() {
        return !isErrorPhone && !isErrorName && !isErrorAddress;
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷資料格式是否正確＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
