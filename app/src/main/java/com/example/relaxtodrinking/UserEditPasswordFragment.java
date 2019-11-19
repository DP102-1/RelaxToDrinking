package com.example.relaxtodrinking;
/***************************************************************/

/***************************************************************/

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.regex.Pattern;


public class UserEditPasswordFragment extends Fragment {
    private String TAG = "修改密碼";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private TextView tvEmail_UserEditPassword, tvErrorPassword_UserEditPassword, tvErrorPasswordAgain_UserEditPassword;
    private ImageView ivExit_UserEditPassword, ivNewPasswordAgain_UserEditPassword, ivNewPassword_UserEditPassword;
    private EditText etNewPassword_UserEditPassword, etNewPasswordAgain_UserEditPassword;
    private Button btSubmit_UserEditPassword;

    private Boolean isErrorPassword = true;

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onStart() {
        super.onStart();
        // 檢查user是否已經登入，是則FirebaseUser物件不為null
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String text = "帳號: " + user.getEmail();
            tvEmail_UserEditPassword.setText(text);
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity.setTitle("修改密碼");
        return inflater.inflate(R.layout.fragment_user_edit_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入使用者資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        tvEmail_UserEditPassword = view.findViewById(R.id.tvEmail_UserEditPassword);
        if(auth.getCurrentUser() != null) {
            tvEmail_UserEditPassword.setText(auth.getCurrentUser().getEmail());
        }
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入使用者資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證新密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etNewPassword_UserEditPassword = view.findViewById(R.id.etNewPassword_UserEditPassword);
        tvErrorPassword_UserEditPassword = view.findViewById(R.id.tvErrorPassword_UserEditPassword);
        ivNewPassword_UserEditPassword = view.findViewById(R.id.ivNewPassword_UserEditPassword);
        etNewPassword_UserEditPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String pattern = "([a-zA-Z]|\\d){6,16}";
                    if (!(Pattern.compile(pattern).matcher(etNewPassword_UserEditPassword.getText().toString().trim()).matches())) {
                        tvErrorPassword_UserEditPassword.setVisibility(View.VISIBLE);
                        tvErrorPassword_UserEditPassword.setText("密碼必須為英文大小寫數字,6~16個字元");
                        ivNewPassword_UserEditPassword.setVisibility(View.VISIBLE);
                        isErrorPassword = true;
                    } else {
                        tvErrorPassword_UserEditPassword.setVisibility(View.GONE);
                        ivNewPassword_UserEditPassword.setVisibility(View.GONE);
                        isErrorPassword = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證新密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證再次輸入新密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etNewPasswordAgain_UserEditPassword = view.findViewById(R.id.etNewPasswordAgain_UserEditPassword);
        tvErrorPasswordAgain_UserEditPassword = view.findViewById(R.id.tvErrorPasswordAgain_UserEditPassword);
        etNewPasswordAgain_UserEditPassword = view.findViewById(R.id.etNewPasswordAgain_UserEditPassword);

        etNewPasswordAgain_UserEditPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!etNewPassword_UserEditPassword.getText().toString().trim().equals(etNewPasswordAgain_UserEditPassword.getText().toString().trim())) {
                    tvErrorPasswordAgain_UserEditPassword.setVisibility(View.VISIBLE);
                    tvErrorPasswordAgain_UserEditPassword.setText("輸入的密碼不一致");
                    ivNewPasswordAgain_UserEditPassword.setVisibility(View.VISIBLE);
                    isErrorPassword = true;
                } else {
                    tvErrorPasswordAgain_UserEditPassword.setVisibility(View.GONE);
                    ivNewPasswordAgain_UserEditPassword.setVisibility(View.GONE);
                    isErrorPassword = false;
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證再次輸入新密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定修改＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_UserEditPassword = view.findViewById(R.id.btSubmit_UserEditPassword);
        btSubmit_UserEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isErrorPassword) {
                    auth.confirmPasswordReset("OK",etNewPassword_UserEditPassword.getText().toString().trim());
                    auth.signOut();
                    Common.showToast(activity, "密碼修改成功,請重新登入");
                }else
                {
                    Common.showToast(activity, "有資料欄位輸入不正確");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定修改＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_UserEditPassword = view.findViewById(R.id.ivExit_UserEditPassword);
        ivExit_UserEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }
}
