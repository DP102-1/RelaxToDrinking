package com.example.relaxtodrinking;

/***************************************************************/

/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductManagementFragment extends Fragment {
    private String TAG = "商品管理";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView rvProductList_ProductManagement;
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("商品管理");
        return inflater.inflate(R.layout.fragment_product_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入商品列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvProductList_ProductManagement = view.findViewById(R.id.rvProductList_ProductManagement);
        rvProductList_ProductManagement.setLayoutManager(new LinearLayoutManager(activity));
        showProductAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入商品列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class ProductAdapter extends RecyclerView.Adapter<ProductManagementFragment.ProductAdapter.MyViewHolder> {
        Context context;
        List<Product> products;

        public ProductAdapter(Context context, List<Product> products) {
            this.context = context;
            this.products = products;
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvProductStatus_ProductManagement;
            private ImageView ivProduct_ProductManagement;
            private Spinner spProductKind_ProductManagement;
            private EditText etProductName_ProductManagement,etPriceL_ProductManagement,etPriceM_ProductManagement;
            private Button btUpdate_ProductManagement,btProductStatus_ProductManagement;
            public MyViewHolder(View ProductView) {
                super(ProductView);
                tvProductStatus_ProductManagement = ProductView.findViewById(R.id.tvProductStatus_ProductManagement);
                ivProduct_ProductManagement = ProductView.findViewById(R.id.ivProduct_ProductManagement);
                spProductKind_ProductManagement = ProductView.findViewById(R.id.spProductKind_ProductManagement);
                etProductName_ProductManagement = ProductView.findViewById(R.id.etProductName_ProductManagement);
                etPriceL_ProductManagement = ProductView.findViewById(R.id.etPriceL_ProductManagement);
                etPriceM_ProductManagement = ProductView.findViewById(R.id.etPriceM_ProductManagement);
                btUpdate_ProductManagement = ProductView.findViewById(R.id.btUpdate_ProductManagement);
                btProductStatus_ProductManagement = ProductView.findViewById(R.id.btProductStatus_ProductManagement);
            }
        }

        @NonNull
        @Override
        public ProductManagementFragment.ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.product_list_management_view, parent, false);
            return new ProductManagementFragment.ProductAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ProductManagementFragment.ProductAdapter.MyViewHolder holder, int position) {
            final Product product = products.get(position);
            if(product.getPro_status() == 1) { //抓商品狀態
                holder.tvProductStatus_ProductManagement.setText("上架中");
                holder.tvProductStatus_ProductManagement.setTextColor(Color.BLUE);
                holder.tvProductStatus_ProductManagement.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                holder.btProductStatus_ProductManagement.setText("下架商品");
            }else {
                holder.tvProductStatus_ProductManagement.setText("已下架");
                holder.tvProductStatus_ProductManagement.setTextColor(Color.GRAY);
                holder.tvProductStatus_ProductManagement.setTypeface(Typeface.DEFAULT);
                holder.btProductStatus_ProductManagement.setText("上架商品");
            }

            if (product.getPro_picture() == null) { //抓商品圖片
                holder.ivProduct_ProductManagement.setImageResource(R.drawable.product_noimage);
            } else {
                showImage(holder.ivProduct_ProductManagement, product.getPro_picture());
            }
            holder.etProductName_ProductManagement.setText(product.getPro_name());//抓商品名稱
            holder.etPriceL_ProductManagement.setText(String.valueOf(product.getPro_price_L()));//抓商品大杯價格
            holder.etPriceM_ProductManagement.setText(String.valueOf(product.getPro_price_M()));//抓商品中杯價格

            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品上下架＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            holder.btProductStatus_ProductManagement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(product.getPro_status() == 1) { //抓商品狀態
                        db.collection("Product").document(product.getPro_id()).update("pro_status",0);
                        Toast.makeText(activity, "商品已下架", Toast.LENGTH_SHORT).show();
                    }else {
                        db.collection("Product").document(product.getPro_id()).update("pro_status",1);
                        Toast.makeText(activity, "商品已上架", Toast.LENGTH_SHORT).show();
                    }
                    showProductAll();
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品上下架＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝儲存商品資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            holder.btUpdate_ProductManagement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String price_L = holder.etPriceL_ProductManagement.getText().toString(),
                           price_M = holder.etPriceM_ProductManagement.getText().toString();
                    if(isCorrectNumber(price_L) && isCorrectNumber (price_M)) {
                        db.collection("Product").document(product.getPro_id()).update("pro_price_M", price_M);
                        db.collection("Product").document(product.getPro_id()).update("pro_price_L", price_L);
                    }else
                    {
                        Toast.makeText(activity, "價格格式不正確", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.collection("Product").document(product.getPro_id()).update("pro_name",holder.etProductName_ProductManagement.getText().toString());

                    //更新類別







                    showProductAll();
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝儲存商品資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入資料庫中的商品類別＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void loadingProductKind() {
        db.collection("ProductKind").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<ProductKind> productKinds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                productKinds.add(document.toObject(ProductKind.class));
                            }

                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入資料庫中的商品類別＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showProductAll() {
        db.collection("Product").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Product> products = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                products.add(document.toObject(Product.class));
                            }
                            rvProductList_ProductManagement.setAdapter(new ProductManagementFragment.ProductAdapter(activity, products));
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


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


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷數字＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private boolean isCorrectNumber(String quantity) {
        if (quantity.length() <= 0) { //沒有輸入字串
            Toast.makeText(activity, "數量不能為空", Toast.LENGTH_SHORT).show();
            return false;
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(quantity);
        if (!isNum.matches()) { //判別是否為數字
            Toast.makeText(activity, "數量只能為數字", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷數字＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
