package com.example.relaxtodrinking;
/***************************************************************/
//相機拍照驗證問題
//類別設定功能
//新增者的員工id
/***************************************************************/

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class ProductInsertFragment extends Fragment {
    private String TAG = "商品新增";
    private static final int ACTION_TakePicture = 101;
    private static final int ACTION_ChoiceFromAlbum = 102;
    private static final int ACTION_CropPicture = 103;
    private static final int ACTION_AgreeUseAlbum = 200;
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private File file;//暫存圖
    private Uri contentUri;//暫存圖路徑

    private Spinner spProductKind_ProductInsert;
    private Button btTakePicture_ProductInsert, btEditKind_ProductInsert, btChoiceFromAlbum_ProductInsert, btProductStatus_ProductInsert, btProductInsert_ProductInsert;
    private ImageView ivProductPicture_ProductInsert, ivExit_ProductInsert;
    private EditText etProductName_ProductInsert, etProductPriceL_ProductInsert, etProductPriceM_ProductInsert;

    private List<ProductKind> kinds; //類別物件集合
    private int pro_status = 1;//商品狀態
    private String pro_kind_id = "0";//商品類別初始id
    private String pro_kind_name = "未分類";//商品類別初始名稱
    private boolean pictureTaken = false;//有沒有照片
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
        activity.setTitle("商品新增");
        return inflater.inflate(R.layout.fragment_product_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivProductPicture_ProductInsert = view.findViewById(R.id.ivProductPicture_ProductInsert);
        spProductKind_ProductInsert = view.findViewById(R.id.spProductKind_ProductInsert);
        showKindAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊拍照＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btTakePicture_ProductInsert = view.findViewById(R.id.btTakePicture_ProductInsert);
        btTakePicture_ProductInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (dir != null && !dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.e(TAG, "無法建立dir");
                        return;
                    }
                }
                file = new File(dir, "picture.jpg");
                contentUri = FileProvider.getUriForFile(
                        activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, ACTION_TakePicture);
                } else {
                    Common.showToast(activity, "找不到相機");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊拍照＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊加入相簿＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btChoiceFromAlbum_ProductInsert = view.findViewById(R.id.btChoiceFromAlbum_ProductInsert);
        btChoiceFromAlbum_ProductInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, ACTION_ChoiceFromAlbum);
                } else {
                    Common.showToast(activity, "找不到相簿");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊加入相簿＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊類別下拉式選單＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        spProductKind_ProductInsert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊類別設定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btEditKind_ProductInsert = view.findViewById(R.id.btEditKind_ProductInsert);
        btEditKind_ProductInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊類別設定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊上下架商品＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btProductStatus_ProductInsert = view.findViewById(R.id.btProductStatus_ProductInsert);
        btProductStatus_ProductInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pro_status == 1) {
                    pro_status = 0;
                    btProductStatus_ProductInsert.setText("已下架");
                } else {
                    pro_status = 1;
                    btProductStatus_ProductInsert.setText("上架中");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊上下架商品＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊新增＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        etProductName_ProductInsert = view.findViewById(R.id.etProductName_ProductInsert);
        etProductPriceL_ProductInsert = view.findViewById(R.id.etProductPriceL_ProductInsert);
        etProductPriceM_ProductInsert = view.findViewById(R.id.etProductPriceM_ProductInsert);
        btProductInsert_ProductInsert = view.findViewById(R.id.btProductInsert_ProductInsert);
        btProductInsert_ProductInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Product product = new Product();
                if (!etProductName_ProductInsert.getText().toString().trim().equals("")) {
                    product.setPro_name(etProductName_ProductInsert.getText().toString().trim());
                } else {
                    Common.showToast(activity, "商品名稱不得為空");
                    return;
                }
                if (isCorrectNumber(etProductPriceL_ProductInsert.getText().toString().trim(), "大杯")) {
                    product.setPro_price_L(Integer.valueOf(etProductPriceL_ProductInsert.getText().toString().trim()));
                } else {
                    return;
                }
                if (isCorrectNumber(etProductPriceM_ProductInsert.getText().toString().trim(), "中杯")) {
                    product.setPro_price_M(Integer.valueOf(etProductPriceM_ProductInsert.getText().toString().trim()));
                } else {
                    return;
                }
                product.setPro_id(db.collection("Product").document().getId());
                product.setPro_kind_id(pro_kind_id);
                product.setPro_kind_name(pro_kind_name);
                product.setPro_status(pro_status);


                //＝＝＝＝＝如果有拍照上傳至storage＝＝＝＝＝//
                if (pictureTaken) {
                    // document ID成為image path一部分，避免與其他圖檔名稱重複
                    final String imagePath = "image/product/" + product.getPro_id();
                    storage.getReference().child(imagePath).putFile(contentUri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "上傳商品圖片成功");
                                        product.setPro_picture(imagePath);
                                    } else {
                                        Log.e(TAG, "上傳商品圖片失敗");
                                        Common.showToast(activity, "上傳商品圖片失敗");
                                    }
                                }
                            });
                }
                //＝＝＝＝＝如果有拍照上傳至storage＝＝＝＝＝//

                //＝＝＝＝＝資料上傳至firebase＝＝＝＝＝//
                db.collection("Product").document(product.getPro_id()).set(product)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "商品新增成功");
                                    Common.showToast(activity, "商品新增成功");
                                    Navigation.findNavController(view).popBackStack();
                                } else {
                                    Log.d(TAG, "商品新增失敗");
                                    Common.showToast(activity, "商品新增失敗");
                                }
                            }
                        });
                //＝＝＝＝＝資料上傳至firebase＝＝＝＝＝//
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊新增＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_ProductInsert = view.findViewById(R.id.ivExit_ProductInsert);
        ivExit_ProductInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
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
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, kindNames);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spProductKind_ProductInsert.setAdapter(arrayAdapter);
                            spProductKind_ProductInsert.setSelection(0, true);
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示商品種類＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝圖片抓取成功顯示在頁面上＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_TakePicture:
                    crop(contentUri);
                    break;
                case ACTION_ChoiceFromAlbum:
                    crop(intent.getData());
                    break;
                case ACTION_CropPicture:
                    Uri uri = intent.getData();
                    Bitmap bitmap = null;
                    if (uri != null) {
                        try {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                                bitmap = BitmapFactory.decodeStream(
                                        activity.getContentResolver().openInputStream(uri));
                            } else {
                                ImageDecoder.Source source =
                                        ImageDecoder.createSource(activity.getContentResolver(), uri);
                                bitmap = ImageDecoder.decodeBitmap(source);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    if (bitmap != null) {
                        ivProductPicture_ProductInsert.setImageBitmap(bitmap);
                        pictureTaken = true;
                    } else {
                        ivProductPicture_ProductInsert.setImageResource(R.drawable.product_noimage);
                    }
                    break;
            }
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝圖片抓取成功顯示在頁面上＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝裁切圖片＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri uri = Uri.fromFile(file);
        // 開啟截圖功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 授權讓截圖程式可以讀取資料
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 設定圖片來源與類型
        intent.setDataAndType(sourceImageUri, "image/*");
        // 設定要截圖
        intent.putExtra("crop", "true");
        // 設定截圖框大小，0代表user任意調整大小
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        // 設定圖片輸出寬高，0代表維持原尺寸
        intent.putExtra("outputX", 0);
        intent.putExtra("outputY", 0);
        // 是否保持原圖比例
        intent.putExtra("scale", true);
        // 設定截圖後圖片位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 設定是否要回傳值
        intent.putExtra("return-data", true);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {// 開啟截圖activity
            startActivityForResult(intent, ACTION_CropPicture);
        } else {
            Common.showToast(activity,"找不到圖片裁切工具");
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝裁切圖片＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證使用者是否同意使用相簿＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onStart() {
        super.onStart();
        askExternalStoragePermission();
    }

    private void askExternalStoragePermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        int result = ContextCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, ACTION_AgreeUseAlbum);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @androidx.annotation.NonNull String[] permissions,
                                           @androidx.annotation.NonNull int[] grantResults) {
        if (requestCode == ACTION_AgreeUseAlbum) {
            // 如果user不同意將資料儲存至外部儲存體的公開檔案，就將儲存按鈕設為disable
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //         Toast.makeText(activity, R.string.textShouldGrant, Toast.LENGTH_SHORT).show();
                btChoiceFromAlbum_ProductInsert.setEnabled(false);
            } else {
                btChoiceFromAlbum_ProductInsert.setEnabled(true);
            }
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝圖片抓取成功顯示在頁面上＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷數字正確性＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private boolean isCorrectNumber(String price, String size) {
        if (price.length() <= 0) { //沒有輸入字串
            Common.showToast(activity, size + "價格不能為空");
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(price);
        if (!isNum.matches()) { //判別是否為數字
            Common.showToast(activity, size + "價格只能為數字");
            return false;
        }
        return true;
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷數字正確性＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
