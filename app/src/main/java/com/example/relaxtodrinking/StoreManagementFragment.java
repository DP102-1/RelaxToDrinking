package com.example.relaxtodrinking;
/***************************************************************/
//
/***************************************************************/

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.relaxtodrinking.data.Product;
import com.example.relaxtodrinking.data.Store;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class StoreManagementFragment extends Fragment {
    private static final int ACTION_AgreeUseAlbum = 200;
    private static final int ACTION_ChoiceFromAlbum = 102;
    private final static String Store_ID = "DVemDPjkmPDEVjuBJwCQ";
    private String TAG = "店家資訊管理";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    ImageView ivBack_StoreManagement, ivStoreLogo_StoreManagement;
    Button btChoiceFromAlbum_StoreManagement, btSubmit_StoreManagement;
    EditText etStoreName_StoreManagement, etStorePhone_StoreManagement, etStoreAddress_StoreManagement;

    private Store store;
    private String store_id;
    private Boolean pictureTaken = false;
    private Uri pick_picture_uri;
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
        activity.setTitle("店家資訊管理");
        return inflater.inflate(R.layout.fragment_store_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etStoreName_StoreManagement = view.findViewById(R.id.etStoreName_StoreManagement);
        etStorePhone_StoreManagement = view.findViewById(R.id.etStorePhone_StoreManagement);
        etStoreAddress_StoreManagement = view.findViewById(R.id.etStoreAddress_StoreManagement);
        ivStoreLogo_StoreManagement = view.findViewById(R.id.ivStoreLogo_StoreManagement);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入店家資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        db.collection("Store").document(Store_ID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                store = documentSnapshot.toObject(Store.class);
                etStoreName_StoreManagement.setText(store.getStore_name());
                etStoreAddress_StoreManagement.setText(store.getStore_address());
                etStorePhone_StoreManagement.setText(store.getStore_phone());
                if (store.getStore_picture() == null) { //抓商品圖片
                    ivStoreLogo_StoreManagement.setImageResource(R.drawable.no_image);
                } else {
                    showImage(ivStoreLogo_StoreManagement, store.getStore_picture());
                }
            }
        });


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入店家資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_StoreManagement = view.findViewById(R.id.btSubmit_StoreManagement);
        btSubmit_StoreManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                store = new Store();
                store.setStore_id(Store_ID);
                String store_name = etStoreName_StoreManagement.getText().toString();
                if (store_name.isEmpty()) {
                    Common.showToast(activity, "尚未填寫店家名稱");
                    return;
                } else {
                    store.setStore_name(store_name);
                }
                String store_phone = etStorePhone_StoreManagement.getText().toString();
                if (store_phone.isEmpty()) {
                    Common.showToast(activity, "尚未填寫店家電話");
                    return;
                } else {
                    store.setStore_phone(store_phone);
                }
                String store_address = etStoreAddress_StoreManagement.getText().toString();
                if (store_address.isEmpty()) {
                    Common.showToast(activity, "尚未填寫店家地址");
                } else {
                    Address address = geocode(store_address);
                    if (address == null) {
                        Common.showToast(activity, "店家地址無法判別,請重新輸入");
                        return;
                    }
                    store.setStore_address(store_address);
                    store.setStore_latitude(address.getLatitude());
                    store.setStore_longitude(address.getLongitude());
                }
                store.setStore_picture("image/store/logo");
                //＝＝＝＝＝如果有拍照上傳至storage＝＝＝＝＝//
                if (pictureTaken) {
                    final String imagePath = "image/store/logo";
                    storage.getReference().child(imagePath).putFile(pick_picture_uri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "上傳店家LOGO成功");
                                        insertStore(store);
                                    } else {
                                        Log.e(TAG, "上傳店家LOGO失敗");
                                        Common.showToast(activity, "上傳店家LOGO失敗");
                                    }
                                }
                            });
                }else {
                    insertStore(store);
                }
                Navigation.findNavController(view).popBackStack();
                //＝＝＝＝＝如果有拍照上傳至storage＝＝＝＝＝//
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊挑選相簿圖片＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btChoiceFromAlbum_StoreManagement = view.findViewById(R.id.btChoiceFromAlbum_StoreManagement);
        btChoiceFromAlbum_StoreManagement.setOnClickListener(new View.OnClickListener() {
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
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊挑選相簿圖片＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_StoreManagement = view.findViewById(R.id.ivBack_StoreManagement);
        ivBack_StoreManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
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
                btChoiceFromAlbum_StoreManagement.setEnabled(false);
            } else {
                btChoiceFromAlbum_StoreManagement.setEnabled(true);
            }
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證使用者是否同意使用相簿＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝圖片抓取成功顯示在頁面上＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_ChoiceFromAlbum) {
                pick_picture_uri = intent.getData();
                Bitmap bitmap = null;
                if (pick_picture_uri != null) {
                    try {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                            bitmap = BitmapFactory.decodeStream(
                                    activity.getContentResolver().openInputStream(pick_picture_uri));
                        } else {
                            ImageDecoder.Source source =
                                    ImageDecoder.createSource(activity.getContentResolver(), pick_picture_uri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
                if (bitmap != null) {
                    ivStoreLogo_StoreManagement.setImageBitmap(bitmap);
                    pictureTaken = true;
                } else {
                    ivStoreLogo_StoreManagement.setImageResource(R.drawable.no_image);
                }
            }
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝圖片抓取成功顯示在頁面上＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料上傳至firebase＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void insertStore(Store store) {
        db.collection("Store").document(Store_ID).set(store)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "店家訊息修改成功");
                            Common.showToast(activity, "店家訊息修改成功");
                        } else {
                            Log.d(TAG, "店家訊息修改失敗");
                            Common.showToast(activity, "店家訊息修改失敗");
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料上傳至firebase＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝地址轉經緯度＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Address geocode(String locationName) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            return null;
        } else {
            return addressList.get(0);
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝地址轉經緯度＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


}
