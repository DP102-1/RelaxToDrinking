package com.example.relaxtodrinking;

import android.app.Activity;
import android.os.Bundle;
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

import com.example.relaxtodrinking.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.regex.Pattern;


public class UserSignInFragment extends Fragment {
    private String TAG = "會員註冊";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private ImageView ivEmail_UserSignIn,ivPassword_UserSignIn,ivName_UserSignIn,ivPhone_UserSignIn,ivAddress_UserSignIn,ivExit_UserSignIn;
    private TextView tvErrorEmail_UserSignIn,tvErrorPassword_UserSignIn,tvErrorName_UserSignIn,tvErrorPhone_UserSignIn,tvErrorAddress_UserSignIn;
    private EditText etEmail_UserSignIn,etPassword_UserSignIn,etName_UserSignIn,etPhone_UserSignIn,etAddress_UserSignIn;
    private Button btSubmit_UserSignIn;

    private Boolean isErrorEmail = true,isErrorPassword = true,isErrorPhone = true,isErrorName = true,isErrorAddress = true;
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
        activity.setTitle("會員註冊");
        return inflater.inflate(R.layout.fragment_user_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivEmail_UserSignIn = view.findViewById(R.id.ivEmail_UserSignIn);
        ivPassword_UserSignIn = view.findViewById(R.id.ivPassword_UserSignIn);
        ivName_UserSignIn = view.findViewById(R.id.ivName_UserSignIn);
        ivPhone_UserSignIn = view.findViewById(R.id.ivPhone_UserSignIn);
        ivAddress_UserSignIn = view.findViewById(R.id.ivAddress_UserSignIn);
        etEmail_UserSignIn = view.findViewById(R.id.etEmail_UserSignIn);
        etPassword_UserSignIn = view.findViewById(R.id.etPassword_UserSignIn);
        etName_UserSignIn = view.findViewById(R.id.etName_UserSignIn);
        etPhone_UserSignIn = view.findViewById(R.id.etPhone_UserSignIn);
        etAddress_UserSignIn = view.findViewById(R.id.etAddress_UserSignIn);
        tvErrorEmail_UserSignIn = view.findViewById(R.id.tvErrorEmail_UserSignIn);
        tvErrorPassword_UserSignIn = view.findViewById(R.id.tvErrorPassword_UserSignIn);
        tvErrorName_UserSignIn = view.findViewById(R.id.tvErrorName_UserSignIn);
        tvErrorPhone_UserSignIn = view.findViewById(R.id.tvErrorPhone_UserSignIn);
        tvErrorAddress_UserSignIn = view.findViewById(R.id.tvErrorAddress_UserSignIn);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證信箱＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etEmail_UserSignIn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
                    if (!(Pattern.compile(pattern).matcher(etEmail_UserSignIn.getText().toString().trim()).matches())) {
                        tvErrorEmail_UserSignIn.setVisibility(View.VISIBLE);
                        tvErrorEmail_UserSignIn.setText("不正確的信箱格式");
                        ivEmail_UserSignIn.setVisibility(View.VISIBLE);
                        isErrorEmail = true;
                    } else {
                        tvErrorEmail_UserSignIn.setVisibility(View.GONE);
                        ivEmail_UserSignIn.setVisibility(View.GONE);
                        isErrorEmail = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證信箱＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etPassword_UserSignIn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String pattern = "([a-zA-Z]|\\d){6,16}";
                    if (!(Pattern.compile(pattern).matcher(etPassword_UserSignIn.getText().toString().trim()).matches())) {
                        tvErrorPassword_UserSignIn.setVisibility(View.VISIBLE);
                        tvErrorPassword_UserSignIn.setText("密碼必須為英文大小寫數字,6~16個字元");
                        ivPassword_UserSignIn.setVisibility(View.VISIBLE);
                        isErrorPassword = true;
                    } else {
                        tvErrorPassword_UserSignIn.setVisibility(View.GONE);
                        ivPassword_UserSignIn.setVisibility(View.GONE);
                        isErrorPassword = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證姓名＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etName_UserSignIn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (etName_UserSignIn.getText().toString().isEmpty()) {
                        tvErrorName_UserSignIn.setVisibility(View.VISIBLE);
                        tvErrorName_UserSignIn.setText("姓名不得為空");
                        ivName_UserSignIn.setVisibility(View.VISIBLE);
                        isErrorName = true;
                    } else {
                        tvErrorName_UserSignIn.setVisibility(View.GONE);
                        ivName_UserSignIn.setVisibility(View.GONE);
                        isErrorName = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證姓名＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證電話＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etPhone_UserSignIn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (etPhone_UserSignIn.getText().toString().isEmpty())
                {
                    isErrorPhone = false;
                    return;
                }
                if (!b) {
                    String pattern = "09[0-9]{8}";
                    if (!(Pattern.compile(pattern).matcher(etPhone_UserSignIn.getText().toString().trim()).matches())) {
                        tvErrorPhone_UserSignIn.setVisibility(View.VISIBLE);
                        tvErrorPhone_UserSignIn.setText("手機號碼格式不正確");
                        ivPhone_UserSignIn.setVisibility(View.VISIBLE);
                        isErrorPhone = true;
                    } else {
                        tvErrorPhone_UserSignIn.setVisibility(View.GONE);
                        ivPhone_UserSignIn.setVisibility(View.GONE);
                        isErrorPhone = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證電話＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證地址＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etAddress_UserSignIn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (etAddress_UserSignIn.getText().toString().isEmpty()) {
                        tvErrorAddress_UserSignIn.setVisibility(View.VISIBLE);
                        tvErrorAddress_UserSignIn.setText("地址不得為空");
                        ivAddress_UserSignIn.setVisibility(View.VISIBLE);
                        isErrorAddress = true;
                    } else {
                        tvErrorAddress_UserSignIn.setVisibility(View.GONE);
                        ivAddress_UserSignIn.setVisibility(View.GONE);
                        isErrorAddress = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證地址＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定新增＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_UserSignIn = view.findViewById(R.id.btSubmit_UserSignIn);
        btSubmit_UserSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllNotError())
                {
                    String email = etEmail_UserSignIn.getText().toString().trim();
                    String password = etPassword_UserSignIn.getText().toString().trim();
                    signInUser(email,password);
                    Navigation.findNavController(view)
                            .navigate(R.id.action_userSignInFragment_to_indexFragment);
                }
                else{
                    Common.showToast(activity,"有資料欄位輸入不正確");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定新增＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_UserSignIn = view.findViewById(R.id.ivExit_UserSignIn);
        ivExit_UserSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料上傳至firebase＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void signInUser(final String email, String password) {
        /* 利用user輸入的email與password建立新的帳號 */
        auth.setLanguageCode("zh-TW");
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 建立成功則轉至下頁；失敗則顯示錯誤訊息
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                User user = new User();
                                String name = etName_UserSignIn.getText().toString().trim();
                                String address = etAddress_UserSignIn.getText().toString().trim();
                                String phone = etPhone_UserSignIn.getText().toString().trim();
                                user.setUser_id(firebaseUser.getUid());
                                user.setUser_name(name);
                                user.setUser_address(address);
                                user.setUser_phone(phone);
                                user.setUser_email(email);
                                db.collection("User").document(user.getUser_id()).set(user);
                                Common.showToast(activity, "註冊成功,請進行登入");
                                auth.signOut();
                            }
                        } else {
                            Exception exception = task.getException();
                            String message = exception == null ? "註冊失敗" : "帳號已有人使用,請重新註冊";
                            Common.showToast(activity, message);
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料上傳至firebase＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷資料格式是否正確＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private boolean isAllNotError(){
        return !isErrorEmail && !isErrorPassword && !isErrorPhone && !isErrorName && !isErrorAddress;
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷資料格式是否正確＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
