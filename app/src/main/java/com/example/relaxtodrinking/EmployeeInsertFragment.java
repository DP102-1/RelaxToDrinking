package com.example.relaxtodrinking;
/***************************************************************/
//
//
//

/***************************************************************/

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.relaxtodrinking.data.Employee;
import com.example.relaxtodrinking.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.regex.Matcher;
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

    private Employee employee;
    private User user;
    private String emp_id = "";
    private String user_id = "";
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
        activity.setTitle("員工新增");
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
        btSubmit_EmployeeInsert = view.findViewById(R.id.btSubmit_EmployeeInsert);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證信箱＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etEmail_EmployeeInsert.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
                if (!(Pattern.compile(pattern).matcher(editable.toString()).matches())) {
                    tvErrorEmail_EmployeeInsert.setVisibility(View.VISIBLE);
                    tvErrorEmail_EmployeeInsert.setText("不正確的信箱格式");
                    ivEmail_EmployeeInsert.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvErrorEmail_EmployeeInsert.setVisibility(View.GONE);
                    ivEmail_EmployeeInsert.setVisibility(View.GONE);
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證信箱＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證密碼＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證姓名＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證姓名＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證電話＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證電話＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證地址＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證地址＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定新增＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_EmployeeInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employee = new Employee();
                user = new User();
                if (emp_id.equals("") || emp_id == null){
                    employee.setEmp_id(db.collection("Employee").document().getId());
                }else
                {
                    employee.setEmp_id(emp_id);
                }
                employee.getEmp_email(etEmail_EmployeeInsert.getText().toString());
                employee.getEmp_email(etEmail_EmployeeInsert.getText().toString());


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


}
