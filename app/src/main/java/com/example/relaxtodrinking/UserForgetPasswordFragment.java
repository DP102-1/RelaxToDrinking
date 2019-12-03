package com.example.relaxtodrinking;
/***************************************************************/
//
/***************************************************************/

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Locale;
import java.util.regex.Pattern;


public class UserForgetPasswordFragment extends Fragment {
    private String TAG = "忘記密碼";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private EditText etEmail_UserForgetPassword;
    private TextView tvCountTime_UserForgetPassword, tvErrorEmail_UserForgetPassword;
    private Button btSendCode_UserForgetPassword;
    private ImageView ivErrorEmail_UserForgetPassword, ivExit_UserForgetPassword;

    private Boolean isErrorEmail = true;
    private int countTime = 10000;
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
        return inflater.inflate(R.layout.fragment_user_forget_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證信箱＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etEmail_UserForgetPassword = view.findViewById(R.id.etEmail_UserForgetPassword);
        tvErrorEmail_UserForgetPassword = view.findViewById(R.id.tvErrorEmail_UserForgetPassword);
        ivErrorEmail_UserForgetPassword = view.findViewById(R.id.ivErrorEmail_UserForgetPassword);
        etEmail_UserForgetPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
                    if (!(Pattern.compile(pattern).matcher(etEmail_UserForgetPassword.getText().toString().trim()).matches())) {
                        tvErrorEmail_UserForgetPassword.setVisibility(View.VISIBLE);
                        tvErrorEmail_UserForgetPassword.setText("不正確的信箱格式");
                        ivErrorEmail_UserForgetPassword.setVisibility(View.VISIBLE);
                        isErrorEmail = true;
                    } else {
                        tvErrorEmail_UserForgetPassword.setVisibility(View.GONE);
                        ivErrorEmail_UserForgetPassword.setVisibility(View.GONE);
                        isErrorEmail = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證信箱＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊發送驗證碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSendCode_UserForgetPassword = view.findViewById(R.id.btSendCode_UserForgetPassword);
        tvCountTime_UserForgetPassword = view.findViewById(R.id.tvCountTime_UserForgetPassword);
        btSendCode_UserForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isErrorEmail) {
                    Common.showToast(activity,"信箱格式不正確或尚未輸入");
                } else {
                    btSendCode_UserForgetPassword.setText("已發送");
                    btSendCode_UserForgetPassword.setEnabled(false);
                    tvCountTime_UserForgetPassword.setVisibility(View.VISIBLE);
                    tvCountTime_UserForgetPassword.setText(String.valueOf(countTime / 1000)+"秒後可再次發送");
                    CountDownTimer countDownTimer = new CountDownTimer(countTime, 1000) {
                        public void onTick(long millisUntilFinished) {
                            tvCountTime_UserForgetPassword.setText(String.format(Locale.getDefault(), "%d秒後可再次發送", millisUntilFinished / 1000L));
                        }

                        public void onFinish() {
                            btSendCode_UserForgetPassword.setText("發送驗證碼");
                            btSendCode_UserForgetPassword.setEnabled(true);
                            tvCountTime_UserForgetPassword.setVisibility(View.GONE);
                        }
                    }.start();

                    Log.e(TAG,etEmail_UserForgetPassword.getText().toString().trim());
                    auth.sendPasswordResetEmail(etEmail_UserForgetPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Common.showToast(activity,"驗證信已發送至"+etEmail_UserForgetPassword.getText().toString().trim());
                            }else
                            {
                                Common.showToast(activity,"該信箱尚未註冊");
                            }
                        }
                    });
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊發送驗證碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_UserForgetPassword = view.findViewById(R.id.ivExit_UserForgetPassword);
        ivExit_UserForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }
}
