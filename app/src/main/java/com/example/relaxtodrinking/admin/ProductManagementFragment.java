package com.example.relaxtodrinking.admin;

/***************************************************************/
//相機功能
//排序依類別功能...
//
/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.relaxtodrinking.Common;
import com.example.relaxtodrinking.R;
import com.example.relaxtodrinking.data.Product;
import com.example.relaxtodrinking.data.ProductKind;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private Button btInsert_ProductManagement;
    private ImageView ivBack_ProductManagement;
    private SearchView svSearchProduct_ProductManagement;

    private List<Product> products;
    private List<ProductKind> kinds;
    private String pro_kind_id;
    private String pro_kind_name;
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


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊新增商品＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btInsert_ProductManagement = view.findViewById(R.id.btInsert_ProductManagement);
                btInsert_ProductManagement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(view).navigate(R.id.action_productManagementFragment_to_productInsertFragment);
                    }
                });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊新增商品＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝搜尋商品＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        svSearchProduct_ProductManagement = view.findViewById(R.id.svSearchProduct_ProductManagement);
            svSearchProduct_ProductManagement.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    ProductAdapter adapter = (ProductAdapter) rvProductList_ProductManagement.getAdapter();
                    if (adapter != null) {
                        if (newText.isEmpty()) {
                            adapter.setProducts(products);
                        } else {
                            List<Product> searchProducts = new ArrayList<>();
                            for (Product product : products) {
                                if (product.getPro_name().toUpperCase().contains(newText.toUpperCase())) {
                                    searchProducts.add(product);
                                }
                            }
                            adapter.setProducts(searchProducts);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
            });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝搜尋商品＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊返回按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_ProductManagement = view.findViewById(R.id.ivBack_ProductManagement);
        ivBack_ProductManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊返回按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class ProductAdapter extends RecyclerView.Adapter<ProductManagementFragment.ProductAdapter.MyViewHolder> {
        Context context;
        List<Product> products;

        public void setProducts(List<Product> products){
            this.products = products;
        }

        public ProductAdapter(Context context, List<Product> products) {
            this.context = context;
            this.products = products;
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvProductStatus_ProductManagement,tvTimeEdit_ProductManagement;
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
                tvTimeEdit_ProductManagement = ProductView.findViewById(R.id.tvTimeEdit_ProductManagement);

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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            holder.tvTimeEdit_ProductManagement.setText(sdf.format(product.getPro_time()));
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊類別下拉式選單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            showKindAll(holder.spProductKind_ProductManagement,product.getPro_kind_id());
            holder.spProductKind_ProductManagement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    pro_kind_id = kinds.get(i).getKind_id();
                    pro_kind_name = kinds.get(i).getKind_name();
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Common.showToast(activity, "沒有資料被選擇");
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊類別下拉式選單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品上下架狀態＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
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
                    db.collection("Product").document(product.getPro_id()).update("pro_time", new Date());
                    showProductAll();
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品上下架狀態＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝儲存商品資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
            holder.btUpdate_ProductManagement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String price_L = holder.etPriceL_ProductManagement.getText().toString(),
                           price_M = holder.etPriceM_ProductManagement.getText().toString();
                    if(isCorrectNumber(price_L) && isCorrectNumber (price_M)) {
                        db.collection("Product").document(product.getPro_id()).update("pro_price_M", Integer.valueOf(price_M));
                        db.collection("Product").document(product.getPro_id()).update("pro_price_L", Integer.valueOf(price_L));
                    }else
                    {
                        Toast.makeText(activity, "價格格式不正確", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.collection("Product").document(product.getPro_id()).update("pro_name",holder.etProductName_ProductManagement.getText().toString());
                    db.collection("Product").document(product.getPro_id()).update("pro_kind_id", pro_kind_id);
                    db.collection("Product").document(product.getPro_id()).update("pro_kind_name", pro_kind_name);
                    db.collection("Product").document(product.getPro_id()).update("pro_time", new Date());
                    Common.showToast(activity,"修改成功");
                    showProductAll();
                }
            });
            //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝儲存商品資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showProductAll() {
        db.collection("Product").orderBy("pro_time", Query.Direction.DESCENDING).get()//降序
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        products = new ArrayList<>();
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                products.add(document.toObject(Product.class));
                            }
                            rvProductList_ProductManagement.setAdapter(new ProductManagementFragment.ProductAdapter(activity, products));
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品種類＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showKindAll(final Spinner spinner, final String productId) {
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
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, kindNames);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(arrayAdapter);
                            for (int i = 0; i < kinds.size(); i++) { //比對商品類別
                                if(kinds.get(i).getKind_id().equals(productId)) {
                                    spinner.setSelection(i, true);
                                    break;
                                }else
                                {
                                    spinner.setSelection(0, true);
                                }
                            }
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品種類＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


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
