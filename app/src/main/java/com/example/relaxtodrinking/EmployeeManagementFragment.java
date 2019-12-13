package com.example.relaxtodrinking;
/***************************************************************/
//地址多行顯示
//員工管理資料不更新 最新消息資料不更新
//auth註冊後直接自動登入的問題...
/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class EmployeeManagementFragment extends Fragment {
    private String TAG = "員工管理";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView rvEmployeeList_EmployeeManagement;
    private EditText etName_EmployeeManagement, etEmail_EmployeeManagement, etPassword_EmployeeManagement, etSex_EmployeeManagement, etPhone_EmployeeManagement, etAddress_EmployeeManagement, etStatus_EmployeeManagement, etPermission_EmployeeManagement;
    private Button btInsert_EmployeeManagement, btEdit_EmployeeManagement, btStatus_EmployeeManagement;
    private ImageView ivBack_EmployeeManagement;

    private List<Employee> employees;
    private String employee_id;
    private String user_id;
    private String employee_name;
    private String employee_email;
    private String employee_password;
    private String employee_sex;
    private String employee_phone;
    private String employee_address;
    private int employee_status;
    private int employee_permission;

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        showEmployeeAll();
        Log.e(TAG,"start");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(TAG);
        return inflater.inflate(R.layout.fragment_employee_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName_EmployeeManagement = view.findViewById(R.id.etName_EmployeeManagement);
        etEmail_EmployeeManagement = view.findViewById(R.id.etEmail_EmployeeManagement);
        etPassword_EmployeeManagement = view.findViewById(R.id.etPassword_EmployeeManagement);
        etSex_EmployeeManagement = view.findViewById(R.id.etSex_EmployeeManagement);
        etPhone_EmployeeManagement = view.findViewById(R.id.etPhone_EmployeeManagement);
        etAddress_EmployeeManagement = view.findViewById(R.id.etAddress_EmployeeManagement);
        etStatus_EmployeeManagement = view.findViewById(R.id.etStatus_EmployeeManagement);
        etPermission_EmployeeManagement = view.findViewById(R.id.etPermission_EmployeeManagement);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvEmployeeList_EmployeeManagement = view.findViewById(R.id.rvEmployeeList_EmployeeManagement);
        rvEmployeeList_EmployeeManagement.setLayoutManager(new LinearLayoutManager(activity));
        showEmployeeAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊新增員工＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btInsert_EmployeeManagement = view.findViewById(R.id.btInsert_EmployeeManagement);
        btInsert_EmployeeManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("action", "新增");
                Navigation.findNavController(view).navigate(R.id.action_employeeManagementFragment_to_employeeInsertFragment, bundle);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊新增員工＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊修改員工＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btEdit_EmployeeManagement = view.findViewById(R.id.btEdit_EmployeeManagement);
        btEdit_EmployeeManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("action", "修改");
                bundle.putString("emp_id", employee_id);
                bundle.putString("user_id", user_id);
                bundle.putString("emp_email", employee_email);
                bundle.putString("emp_name", employee_name);
                bundle.putString("emp_sex", employee_sex);
                bundle.putString("emp_phone", employee_phone);
                bundle.putString("emp_address", employee_address);
                Navigation.findNavController(view).navigate(R.id.action_employeeManagementFragment_to_employeeInsertFragment, bundle);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊修改員工＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊在離職＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btStatus_EmployeeManagement = view.findViewById(R.id.btStatus_EmployeeManagement);
        btStatus_EmployeeManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(employee_status == 1) { //抓商品狀態
                    db.collection("Employee").document(employee_id).update("emp_status",0);
                    Common.showToast(activity,"該員工設定已離職");
                }else {
                    db.collection("Employee").document(employee_id).update("emp_status",1);
                    Common.showToast(activity,"該員工設定在職中");
                }
                showEmployeeAll();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊在離職＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_EmployeeManagement = view.findViewById(R.id.ivBack_EmployeeManagement);
        ivBack_EmployeeManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝員工列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class EmployeeAdapter extends RecyclerView.Adapter<EmployeeManagementFragment.EmployeeAdapter.MyViewHolder> {
        Context context;
        List<Employee> employees;

        public EmployeeAdapter(Context context, List<Employee> employees) {
            this.context = context;
            this.employees = employees;
        }

        @Override
        public int getItemCount() {
            return employees.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvEmployeeName_EmployeeManagement;

            public MyViewHolder(View EmployeeView) {
                super(EmployeeView);
                tvEmployeeName_EmployeeManagement = EmployeeView.findViewById(R.id.tvEmployeeName_EmployeeManagement);
            }
        }

        @NonNull
        @Override
        public EmployeeManagementFragment.EmployeeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.employee_list_view, parent, false);
            return new EmployeeManagementFragment.EmployeeAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull EmployeeManagementFragment.EmployeeAdapter.MyViewHolder holder, int position) {
            final Employee employee = employees.get(position);
            holder.tvEmployeeName_EmployeeManagement.setText(employee.getEmp_name());
            if(employee.getEmp_permission() < 2)
            {
                holder.tvEmployeeName_EmployeeManagement.setTextColor(Color.BLUE);
            }
            if (employee.getEmp_status() == 0)
            {
                holder.tvEmployeeName_EmployeeManagement.setTextColor(Color.GRAY);
                holder.tvEmployeeName_EmployeeManagement.setTypeface(Typeface.DEFAULT,Typeface.ITALIC);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setEmpDataAndShow(employee);
                }
            });
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝員工列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示員工列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showEmployeeAll() {
        db.collection("Employee").orderBy("emp_permission", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                employees = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    employees.add(snapshot.toObject(Employee.class));
                }
                //＝＝＝＝＝取得第一筆資料並顯示＝＝＝＝＝//
                if (employees.size() != 0) {
                    Employee employee = employees.get(0);
                    setEmpDataAndShow(employee);
                }
                //＝＝＝＝＝取得第一筆資料並顯示＝＝＝＝＝//
                rvEmployeeList_EmployeeManagement.setAdapter(new EmployeeManagementFragment.EmployeeAdapter(activity, employees));
            }
        });
    }

    private void setEmpDataAndShow(Employee employee){
        employee_id = employee.getEmp_id();
        user_id = employee.getUser_id();
        employee_name = employee.getEmp_name();
        employee_email = employee.getEmp_email();
        employee_password = "------------";
        employee_sex = employee.getEmp_sex();
        employee_phone = employee.getEmp_phone();
        employee_address = employee.getEmp_address();
        employee_status = employee.getEmp_status();
        employee_permission = employee.getEmp_permission();

        etName_EmployeeManagement.setText(employee_name);
        etEmail_EmployeeManagement.setText(employee_email);
        etPassword_EmployeeManagement.setText(employee_password);
        etSex_EmployeeManagement.setText(employee_sex);
        etPhone_EmployeeManagement.setText(employee_phone);
        etAddress_EmployeeManagement.setText(employee_address);
        if (employee_status == 1) {
            etStatus_EmployeeManagement.setText("在職中");
        } else if (employee_status == 0){
            etStatus_EmployeeManagement.setText("已離職");
        }
        if (employee_permission == 1) {
            etPermission_EmployeeManagement.setText("店長/管理員");
        }
        else if (employee_permission == 2)
        {
            etPermission_EmployeeManagement.setText("店員/外送員");
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示員工列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
