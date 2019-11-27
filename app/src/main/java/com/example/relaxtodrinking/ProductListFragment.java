package com.example.relaxtodrinking;
/***************************************************************/
// 需要點擊類別按鈕改變文字顏色的功能
// 外觀美化
// 點擊返回按鈕沒做
/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.relaxtodrinking.data.Product;
import com.example.relaxtodrinking.data.ProductKind;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {
    private String TAG = "商品瀏覽";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView rvKindList_ProductList, rvProductList_ProductList;
    private ImageView ivGoShoppingCart_ProductList, ivBack_ProductList;

    private String productKindId = "0"; //"0"代表全部的類別
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
        activity.setTitle("商品瀏覽");
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvKindList_ProductList = view.findViewById(R.id.rvKindList_ProductList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvKindList_ProductList.setLayoutManager(linearLayoutManager);
        showKindAll();

        rvProductList_ProductList = view.findViewById(R.id.rvProductList_ProductList);
        rvProductList_ProductList.setLayoutManager(new LinearLayoutManager(activity));
        showProductAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊購物車＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivGoShoppingCart_ProductList = view.findViewById(R.id.ivGoShoppingCart_ProductList);
        ivGoShoppingCart_ProductList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_productListFragment_to_shoppingcartListFragment);
            }
        });

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊購物車＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊返回按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_ProductList = view.findViewById(R.id.ivBack_ProductList);
        ivBack_ProductList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊返回按鈕＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
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
            private TextView tvPriceL_ProductList, tvPriceM_ProductList, tvProductName_ProductList;
            private ImageView ivProduct_ProductList, ivGoProductSpecification_ProductList;

            public MyViewHolder(View ProductView) {
                super(ProductView);
                tvPriceL_ProductList = ProductView.findViewById(R.id.tvPriceL_ProductList);
                tvPriceM_ProductList = ProductView.findViewById(R.id.tvPriceM_ProductList);
                tvProductName_ProductList = ProductView.findViewById(R.id.tvProductName_ProductList);
                ivProduct_ProductList = ProductView.findViewById(R.id.ivProduct_ProductList);
                ivGoProductSpecification_ProductList = ProductView.findViewById(R.id.ivGoProductSpecification_ProductList);
            }
        }

        @NonNull
        @Override
        public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.product_list_view, parent, false);
            return new ProductAdapter.MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
            final Product product = products.get(position);
            holder.tvProductName_ProductList.setText(product.getPro_name()); //抓商品名稱
            holder.tvPriceL_ProductList.setText(String.valueOf(product.getPro_price_L())); //抓商品中杯價格
            holder.tvPriceM_ProductList.setText(String.valueOf(product.getPro_price_M())); //抓商品大杯價格
            if (product.getPro_picture() == null) { //抓商品圖片
                holder.ivProduct_ProductList.setImageResource(R.drawable.product_noimage);
            } else {
                showImage(holder.ivProduct_ProductList, product.getPro_picture());
            }
            holder.ivGoProductSpecification_ProductList.setOnClickListener(new View.OnClickListener() { //點擊商品規格細項設定
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment", TAG);
                    bundle.putString("pro_id", product.getPro_id());
                    bundle.putString("pro_name", product.getPro_name());
                    bundle.putInt("pro_price_M", product.getPro_price_M());
                    bundle.putInt("pro_price_L", product.getPro_price_L());
                    Navigation.findNavController(view).navigate(R.id.action_productListFragment_to_productSpecificationFragment, bundle);
                }
            });
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showProductAll() {
        db.collection("Product").whereEqualTo("pro_status",1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Product> products = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                products.add(document.toObject(Product.class));
                            }
                            if (!productKindId.equals("0")) { //如果為顯示全部就不執行這段
                                for (int i = products.size() - 1; i >= 0; i--) { //刪除products集合不是"該類別id"的商品
                                    Product product = products.get(i);
                                    if (!productKindId.equals(product.getPro_kind_id())) {
                                        products.remove(i);
                                    }
                                }
                            }
                            rvProductList_ProductList.setAdapter(new ProductAdapter(activity, products));
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品種類內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class KindAdapter extends RecyclerView.Adapter<KindAdapter.MyViewHolder> {
        Context context;
        List<ProductKind> kinds;

        public KindAdapter(Context context, List<ProductKind> kinds) {
            this.context = context;
            this.kinds = kinds;
        }

        @Override
        public int getItemCount() {
            return kinds.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvKindName_ProductList;
            public MyViewHolder(View KindView) {
                super(KindView);
                tvKindName_ProductList = KindView.findViewById(R.id.tvKindName_ProductList);
            }
        }
        @NonNull
        @Override
        public KindAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.productkind_list_view, parent, false);
            return new KindAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final KindAdapter.MyViewHolder holder, int position) {
            final ProductKind kind = kinds.get(position);
            holder.tvKindName_ProductList.setText(kind.getKind_name()); //抓商品種類名稱
            holder.itemView.setOnClickListener(new View.OnClickListener() { //點擊商品類別來分類
                @Override
                public void onClick(View view) {
                    productKindId = kind.getKind_id();
                    showProductAll();
                }
            });

        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝商品種類內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品種類＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showKindAll() {
        db.collection("ProductKind").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<ProductKind> kinds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                kinds.add(document.toObject(ProductKind.class));
                            }
                            rvKindList_ProductList.setAdapter(new KindAdapter(activity, kinds));
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
}
