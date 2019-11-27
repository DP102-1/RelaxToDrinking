package com.example.relaxtodrinking;
/***************************************************************/
//舊密碼驗證
/***************************************************************/

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.relaxtodrinking.data.Employee;
import com.example.relaxtodrinking.data.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class UserLoginFragment extends Fragment {
    private String TAG = "會員登入";
    private final static String Store_ID = "DVemDPjkmPDEVjuBJwCQ";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private SharedPreferences preferences_user;

    private ImageView ivLogo_UserLogin, ivExit_UserLogin;
    private TextView tvSignIn_UserLogin, tvForgetPassword_UserLogin;
    private EditText etAccount_UserLogin, etPassword_UserLogin;
    private Button btSubmit_UserLogin;
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
        activity.setTitle("會員登入");
        return inflater.inflate(R.layout.fragment_user_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etAccount_UserLogin = view.findViewById(R.id.etAccount_UserLogin);
        etPassword_UserLogin = view.findViewById(R.id.etPassword_UserLogin);

        ivLogo_UserLogin = view.findViewById(R.id.ivLogo_UserLogin);
        db.collection("Store").document(Store_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    Store store = document.toObject(Store.class);
                    if (store != null) {
                        showImage(ivLogo_UserLogin, store.getStore_picture());
                    }
                } else {
                    Log.e(TAG,"找不到店家資料");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊註冊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        tvSignIn_UserLogin = view.findViewById(R.id.tvSignIn_UserLogin);
        tvSignIn_UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_userLoginFragment_to_userSignInFragment);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊註冊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊忘記密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        tvForgetPassword_UserLogin = view.findViewById(R.id.tvForgetPassword_UserLogin);
        tvForgetPassword_UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_userLoginFragment_to_userForgetPasswordFragment);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊忘記密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊登入＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_UserLogin = view.findViewById(R.id.btSubmit_UserLogin);
        btSubmit_UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String email = etAccount_UserLogin.getText().toString().trim();
                String password = etPassword_UserLogin.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Common.showToast(activity, "帳號或密碼為空");
                }else
                {
                    auth.setLanguageCode("zh-TW");
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // 登入成功轉至下頁；失敗則顯示錯誤訊息
                                    if (task.isSuccessful()) {
                                        //＝＝＝＝＝判別使用者的身份＝＝＝＝＝//
                                        db.collection("Employee").whereEqualTo("user_id",auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                List<Employee> employees = new ArrayList<>();
                                                if (task.isSuccessful() && task.getResult() != null) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        employees.add(document.toObject(Employee.class));
                                                    }
                                                    if (employees.size() == 0) { //沒有員工資料等於普通使用者
                                                        Navigation.findNavController(view).popBackStack();
                                                    }
                                                    else
                                                    {
                                                        Employee employee = employees.get(0);
                                                        if (employee.getEmp_permission() == 0) //權限0是最高管理員,權限1是管理員,權限2是店員
                                                        {
                                                            Navigation.findNavController(view).navigate(R.id.action_userLoginFragment_to_managementListFragment);
                                                        }else if (employee.getEmp_permission() == 1)
                                                        {
                                                            Navigation.findNavController(view).navigate(R.id.action_userLoginFragment_to_managementListFragment);
                                                        }else
                                                        {
                                                            /****************/
                                                            Navigation.findNavController(view).navigate(R.id.action_userLoginFragment_to_userManagementFragment);
                                                            /****************/
                                                        }
                                                    }
                                                } else {
                                                    Log.e(TAG, "無法判別登入者身份");
                                                    Common.showToast(activity, "無法判別登入者身份");
                                                }
                                            }
                                        });
                                        //＝＝＝＝＝判別使用者的身份＝＝＝＝＝//`
                                    } else {
                                        Exception exception = task.getException();
                                        String message = exception == null ? "登入失敗" : "帳號或密碼錯誤！";
                                        //exception.getLocalizedMessage()
                                        Common.showToast(activity, message);
                                    }
                                }
                            });
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊登入＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_UserLogin = view.findViewById(R.id.ivExit_UserLogin);
        ivExit_UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示圖片＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            byte[] bytes = task.getResult();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
                        } else {
                            Log.e(TAG, "圖片載入失敗");
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示圖片＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
