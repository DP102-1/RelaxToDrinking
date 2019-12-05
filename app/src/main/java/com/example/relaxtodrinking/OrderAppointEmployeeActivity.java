package com.example.relaxtodrinking;
/***************************************************************/


/***************************************************************/

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.relaxtodrinking.data.Employee;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderAppointEmployeeActivity extends AppCompatActivity {
    private String TAG = "訂單指派外送員";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private FirebaseFirestore db;

    private Button btSubmit_OrderAppointEmployee,btExit_OrderAppointEmployee;
    private Spinner spEmployee_OrderAppointEmployee;

    private List<Employee> employees;
    private String emp_id = "0";
    private String emp_name = "未指派";
    private String order_id = "";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_appoint_employee);
        setTitle("");
        db = FirebaseFirestore.getInstance();
        loadOrderData();
        spEmployee_OrderAppointEmployee = findViewById(R.id.spEmployee_OrderAppointEmployee);
        showEmployeeAll();

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊選擇外送員列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        spEmployee_OrderAppointEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                emp_id = employees.get(i).getEmp_id();
                emp_name = employees.get(i).getEmp_name();
                Log.e(TAG,emp_id+" "+emp_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Common.showToast(OrderAppointEmployeeActivity.this, "沒有資料被選擇");
            }
        });

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊選擇外送員列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_OrderAppointEmployee = findViewById(R.id.btSubmit_OrderAppointEmployee);
        btSubmit_OrderAppointEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Order").document(order_id).update("order_status",2);
                db.collection("Order").document(order_id).update("emp_id",emp_id);
                Common.showToast(OrderAppointEmployeeActivity.this,"指派成功");
                finish();
            }
        });

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btExit_OrderAppointEmployee = findViewById(R.id.btExit_OrderAppointEmployee);
        btExit_OrderAppointEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示外送員列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showEmployeeAll() {
        //權限為2且狀態為1的員工
        db.collection("Employee").whereEqualTo("emp_permission",2).whereEqualTo("emp_status",1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    employees = new ArrayList<>(); //類別物件集合
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        employees.add(document.toObject(Employee.class));
                    }
                    final List<String> employeeNames = new ArrayList<>(); //類別名稱集合
                    for (int i = 0; i < employees.size(); i++) {
                        employeeNames.add(employees.get(i).getEmp_name());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(OrderAppointEmployeeActivity.this, android.R.layout.simple_spinner_item, employeeNames);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spEmployee_OrderAppointEmployee.setAdapter(arrayAdapter);
                    spEmployee_OrderAppointEmployee.setSelection(0, true);
                    emp_id = employees.get(0).getEmp_id();
                    emp_name = employeeNames.get(0);
                    Log.e(TAG,emp_id+" "+emp_name);
                }
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示外送員列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋訂單資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void loadOrderData() {
        Intent appointIntent = getIntent();
        order_id = appointIntent.getStringExtra("order_id");
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝找尋訂單資料＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
