package com.example.relaxtodrinking.admin;

/***************************************************************/


/***************************************************************/

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.relaxtodrinking.Common;
import com.example.relaxtodrinking.R;
import com.example.relaxtodrinking.data.Product;
import com.example.relaxtodrinking.data.ProductKind;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class KindManagementActivity extends AppCompatActivity {
    private String TAG = "商品分類管理";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private EditText etNewKindName_KindManagement;
    private RadioGroup rgKindAction_KindManagement;
    private Spinner spOldKindName_KindManagement;
    private Button btExit_KindManagement,btSubmit_KindManagement;

    private List<ProductKind> kinds;
    private String pro_kind_id = "0";//商品類別初始id
    private String pro_kind_name = "未分類";//商品類別初始名稱
    private String action = "新增飲料分類";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kind_management);
        setTitle("商品分類管理");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        etNewKindName_KindManagement = findViewById(R.id.etNewKindName_KindManagement);
        spOldKindName_KindManagement = findViewById(R.id.spOldKindName_KindManagement);
        showKindAll();
        isEnabled(action);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇動作＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rgKindAction_KindManagement = findViewById(R.id.rgKindAction_KindManagement);
        rgKindAction_KindManagement.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                action = radioButton.getText().toString();
                isEnabled(action);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝選擇動作＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊類別下拉式選單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        spOldKindName_KindManagement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pro_kind_id = kinds.get(i).getKind_id();
                pro_kind_name = kinds.get(i).getKind_name();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Common.showToast(KindManagementActivity.this, "沒有資料被選擇");
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊類別下拉式選單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_KindManagement = findViewById(R.id.btSubmit_KindManagement);
        btSubmit_KindManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kind_name = etNewKindName_KindManagement.getText().toString().trim();
                if (kind_name.equals("") && !action.equals("刪除飲料分類")) {
                    Common.showToast(KindManagementActivity.this, "尚未輸入新分類名稱");
                    return;
                }
                if(pro_kind_id.equals("0") && !action.equals("新增飲料分類"))
                {
                    Common.showToast(KindManagementActivity.this, "尚未選擇分類");
                    return;
                }
                switch (action)
                {
                    //＝＝＝＝＝新增＝＝＝＝＝//
                    case "新增飲料分類":
                            ProductKind kind = new ProductKind();
                            kind.setKind_id(db.collection("ProductKind").document().getId());
                            kind.setKind_name(kind_name);
                            db.collection("ProductKind").document(kind.getKind_id()).set(kind);
                            Common.showToast(KindManagementActivity.this,"新增成功");

                        break;
                    //＝＝＝＝＝新增＝＝＝＝＝//


                    //＝＝＝＝＝修改＝＝＝＝＝//
                    case "修改飲料分類":
                        db.collection("ProductKind").document(pro_kind_id).update("kind_name",kind_name);
                        Common.showToast(KindManagementActivity.this,"修改成功");
                        break;
                    //＝＝＝＝＝修改＝＝＝＝＝//


                    //＝＝＝＝＝刪除＝＝＝＝＝//
                    case "刪除飲料分類":
                        db.collection("ProductKind").document(pro_kind_id).delete();
                        db.collection("Product").whereEqualTo("pro_kind_id",pro_kind_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<Product> products = new ArrayList<>();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                    products.add(snapshot.toObject(Product.class));
                                for (Product product : products)
                                {
                                    db.collection("Product").document(product.getPro_id()).update("pro_kind_id","0");
                                    db.collection("Product").document(product.getPro_id()).update("pro_kind_name","全部");
                                }
                                Common.showToast(KindManagementActivity.this,"刪除成功");
                            }
                        });
                        break;
                    //＝＝＝＝＝刪除＝＝＝＝＝//
                }
                finish();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btExit_KindManagement = findViewById(R.id.btExit_KindManagement);
        btExit_KindManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品種類＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showKindAll() {
        db.collection("ProductKind").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            kinds = new ArrayList<>(); //類別物件集合
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                kinds.add(document.toObject(ProductKind.class));
                            }
                            final List<String> kindNames = new ArrayList<>(); //類別名稱集合
                            for (int i = 0; i < kinds.size(); i++) {
                                kindNames.add(kinds.get(i).getKind_name());
                            }
                            kindNames.set(0, "未分類"); //強制把第一項(全部)顯示為未分類
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(KindManagementActivity.this, android.R.layout.simple_spinner_item, kindNames);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spOldKindName_KindManagement.setAdapter(arrayAdapter);
                            spOldKindName_KindManagement.setSelection(0, true);
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品種類＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    private void isEnabled(String action)
    {
        switch (action)
        {
            case "新增飲料分類":
                spOldKindName_KindManagement.setEnabled(false);
                etNewKindName_KindManagement.setEnabled(true);
                break;
            case "修改飲料分類":
                spOldKindName_KindManagement.setEnabled(true);
                etNewKindName_KindManagement.setEnabled(true);
                break;
            case "刪除飲料分類":
                spOldKindName_KindManagement.setEnabled(true);
                etNewKindName_KindManagement.setEnabled(false);
                etNewKindName_KindManagement.setText("");
                break;
        }
    }
}
