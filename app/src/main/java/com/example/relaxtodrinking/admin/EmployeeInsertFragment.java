package com.example.relaxtodrinking.admin;
/***************************************************************/
//Edittext的鍵盤輸入格式
/***************************************************************/

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.relaxtodrinking.Common;
import com.example.relaxtodrinking.R;
import com.example.relaxtodrinking.data.Employee;
import com.example.relaxtodrinking.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.regex.Pattern;

public class EmployeeInsertFragment extends Fragment {
    private String TAG = "員工新增";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private ImageView ivExit_EmployeeInsert;
    private ImageView ivEmail_EmployeeInsert, ivPassword_EmployeeInsert, ivName_EmployeeInsert, ivPhone_EmployeeInsert, ivAddress_EmployeeInsert;
    private EditText etEmail_EmployeeInsert, etPassword_EmployeeInsert, etName_EmployeeInsert, etPhone_EmployeeInsert, etAddress_EmployeeInsert;
    private TextView tvErrorEmail_EmployeeInsert, tvErrorPassword_EmployeeInsert, tvErrorName_EmployeeInsert, tvErrorPhone_EmployeeInsert, tvErrorAddress_EmployeeInsert;
    private Button btSubmit_EmployeeInsert;
    private RadioGroup rgSex_EmployeeInsert;

    private String sex = "男";
    private Boolean isErrorEmail = true, isErrorPassword = true, isErrorName = true, isErrorPhone = true, isErrorAddress = true;
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return inflater.inflate(R.layout.fragment_employee_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivEmail_EmployeeInsert = view.findViewById(R.id.ivEmail_EmployeeInsert);
        ivPassword_EmployeeInsert = view.findViewById(R.id.ivPassword_EmployeeInsert);
        ivName_EmployeeInsert = view.findViewById(R.id.ivName_EmployeeInsert);
        ivPhone_EmployeeInsert = view.findViewById(R.id.ivPhone_EmployeeInsert);
        ivAddress_EmployeeInsert = view.findViewById(R.id.ivAddress_EmployeeInsert);
        etEmail_EmployeeInsert = view.findViewById(R.id.etEmail_EmployeeInsert);
        etPassword_EmployeeInsert = view.findViewById(R.id.etPassword_EmployeeInsert);
        etName_EmployeeInsert = view.findViewById(R.id.etName_EmployeeInsert);
        etPhone_EmployeeInsert = view.findViewById(R.id.etPhone_EmployeeInsert);
        etAddress_EmployeeInsert = view.findViewById(R.id.etAddress_EmployeeInsert);
        tvErrorEmail_EmployeeInsert = view.findViewById(R.id.tvErrorEmail_EmployeeInsert);
        tvErrorPassword_EmployeeInsert = view.findViewById(R.id.tvErrorPassword_EmployeeInsert);
        tvErrorName_EmployeeInsert = view.findViewById(R.id.tvErrorName_EmployeeInsert);
        tvErrorPhone_EmployeeInsert = view.findViewById(R.id.tvErrorPhone_EmployeeInsert);
        tvErrorAddress_EmployeeInsert = view.findViewById(R.id.tvErrorAddress_EmployeeInsert);
        rgSex_EmployeeInsert = view.findViewById(R.id.rgSex_EmployeeInsert);
        btSubmit_EmployeeInsert = view.findViewById(R.id.btSubmit_EmployeeInsert);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證信箱＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etEmail_EmployeeInsert = view.findViewById(R.id.etEmail_EmployeeInsert);
        etEmail_EmployeeInsert.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
                    if (!(Pattern.compile(pattern).matcher(etEmail_EmployeeInsert.getText().toString().trim()).matches())) {
                        tvErrorEmail_EmployeeInsert.setVisibility(View.VISIBLE);
                        tvErrorEmail_EmployeeInsert.setText("不正確的信箱格式");
                        ivEmail_EmployeeInsert.setVisibility(View.VISIBLE);
                        isErrorEmail = true;
                    } else {
                        tvErrorEmail_EmployeeInsert.setVisibility(View.GONE);
                        ivEmail_EmployeeInsert.setVisibility(View.GONE);
                        isErrorEmail = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證信箱＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etPassword_EmployeeInsert.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String pattern = "([a-zA-Z]|\\d){6,16}";
                    if (!(Pattern.compile(pattern).matcher(etPassword_EmployeeInsert.getText().toString().trim()).matches())) {
                        tvErrorPassword_EmployeeInsert.setVisibility(View.VISIBLE);
                        tvErrorPassword_EmployeeInsert.setText("密碼必須為英文大小寫數字,6~16個字元");
                        ivPassword_EmployeeInsert.setVisibility(View.VISIBLE);
                        isErrorPassword = true;
                    } else {
                        tvErrorPassword_EmployeeInsert.setVisibility(View.GONE);
                        ivPassword_EmployeeInsert.setVisibility(View.GONE);
                        isErrorPassword = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證姓名＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etName_EmployeeInsert.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (etName_EmployeeInsert.getText().toString().isEmpty()) {
                        tvErrorName_EmployeeInsert.setVisibility(View.VISIBLE);
                        tvErrorName_EmployeeInsert.setText("姓名不得為空");
                        ivName_EmployeeInsert.setVisibility(View.VISIBLE);
                        isErrorName = true;
                    } else {
                        tvErrorName_EmployeeInsert.setVisibility(View.GONE);
                        ivName_EmployeeInsert.setVisibility(View.GONE);
                        isErrorName = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證姓名＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證性別＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rgSex_EmployeeInsert.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                sex = radioButton.getText().toString();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證性別＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證手機號碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etPhone_EmployeeInsert.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (etPhone_EmployeeInsert.getText().toString().isEmpty()) {
                    isErrorPhone = true;
                    tvErrorPhone_EmployeeInsert.setText("手機號碼不得為空");
                }
                if (!b) {
                    String pattern = "09[0-9]{8}";
                    if (!(Pattern.compile(pattern).matcher(etPhone_EmployeeInsert.getText().toString().trim()).matches())) {
                        tvErrorPhone_EmployeeInsert.setVisibility(View.VISIBLE);
                        tvErrorPhone_EmployeeInsert.setText("手機號碼格式不正確");
                        ivPhone_EmployeeInsert.setVisibility(View.VISIBLE);
                        isErrorPhone = true;
                    } else {
                        tvErrorPhone_EmployeeInsert.setVisibility(View.GONE);
                        ivPhone_EmployeeInsert.setVisibility(View.GONE);
                        isErrorPhone = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證手機號碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證地址＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etAddress_EmployeeInsert.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (etAddress_EmployeeInsert.getText().toString().isEmpty()) {
                        tvErrorAddress_EmployeeInsert.setVisibility(View.VISIBLE);
                        tvErrorAddress_EmployeeInsert.setText("地址不得為空");
                        ivAddress_EmployeeInsert.setVisibility(View.VISIBLE);
                        isErrorAddress = true;
                    } else {
                        tvErrorAddress_EmployeeInsert.setVisibility(View.GONE);
                        ivAddress_EmployeeInsert.setVisibility(View.GONE);
                        isErrorAddress = false;
                    }
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證地址＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定新增＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_EmployeeInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //Auth新增帳號密碼 新增Employee 新增User
                if(isAllNotError())
                {
                    String email = etEmail_EmployeeInsert.getText().toString().trim();
                    String password = etPassword_EmployeeInsert.getText().toString().trim();
                    signInEmployeeInsert(email,password);
                    Common.showToast(activity, "員工新增成功");
                    Navigation.findNavController(view).popBackStack();
                }else{
                    Common.showToast(activity,"有資料欄位輸入不正確");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定新增＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_EmployeeInsert = view.findViewById(R.id.ivExit_EmployeeInsert);
        ivExit_EmployeeInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料上傳至firebase＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void signInEmployeeInsert(String email, String password) {
        /* 利用user輸入的email與password建立新的帳號 */
        auth.setLanguageCode("zh-Hant");
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 建立成功則轉至下頁；失敗則顯示錯誤訊息
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                Employee employee = new Employee();
                                User user = new User();
                                String emp_id = db.collection("Employee").document().getId();
                                String email = etEmail_EmployeeInsert.getText().toString().trim();
                                String name = etName_EmployeeInsert.getText().toString().trim();
                                String phone = etPhone_EmployeeInsert.getText().toString().trim();
                                String address = etAddress_EmployeeInsert.getText().toString().trim();

                                employee.setEmp_id(emp_id);
                                employee.setEmp_email(email);
                                employee.setEmp_name(name);
                                employee.setEmp_phone(phone);
                                employee.setEmp_address(address);
                                employee.setEmp_sex(sex);
                                employee.setEmp_status(1);
                                employee.setEmp_permission(2);
                                employee.setUser_id(firebaseUser.getUid());

                                user.setUser_id(firebaseUser.getUid());
                                user.setUser_name(name);
                                user.setUser_address(address);
                                user.setUser_phone(phone);
                                user.setUser_email(email);
                                insertEmployee(employee);
                                insertUser(user);
                            }
                        } else {
                            Exception exception = task.getException();
                            String message = exception == null ? "登入失敗" : exception.getLocalizedMessage();
                            Common.showToast(activity, message);
                        }
                    }
                });
    }
    private void insertEmployee(Employee employee) {
        db.collection("Employee").document(employee.getEmp_id()).set(employee)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "員工基本資料新增成功");

                        } else {
                            Log.d(TAG, "員工基本資料新增失敗");
                        }
                    }
                });
    }

    private void insertUser(User user) {
        db.collection("User").document(user.getUser_id()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "會員基本資料新增成功");

                        } else {
                            Log.d(TAG, "會員基本資料新增失敗");
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料上傳至firebase＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷資料格式是否正確＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private boolean isAllNotError() {
        return !isErrorEmail && !isErrorPassword && !isErrorPhone && !isErrorName && !isErrorAddress;
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷資料格式是否正確＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
