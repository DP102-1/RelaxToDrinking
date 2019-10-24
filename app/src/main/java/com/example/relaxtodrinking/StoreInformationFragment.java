package com.example.relaxtodrinking;

/***************************************************************/
//地圖執行緒卡到資料庫 心累
/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.relaxtodrinking.data.Order;
import com.example.relaxtodrinking.data.ProductKind;
import com.example.relaxtodrinking.data.Store;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreInformationFragment extends Fragment {
    private final static String Store_ID = "DVemDPjkmPDEVjuBJwCQ";
    private String TAG = "店家資訊";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private TextView tvStoreName_StoreInformation, tvStoreAddress_StoreInformation, tvStorePhone_StoreInformation;
    private MapView mvStoreAddress_StoreInformation;
    private GoogleMap mapStoreAddress_StoreInformation;
    private ImageView ivBack_StoreInformation, ivStoreLogo_StoreInformation;

    private Store store = new Store();
    private Double store_latitude;
    private Double store_longitude;
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
        activity.setTitle("店家資訊");
        return inflater.inflate(R.layout.fragment_store_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvStoreName_StoreInformation = view.findViewById(R.id.tvStoreName_StoreInformation);
        tvStoreAddress_StoreInformation = view.findViewById(R.id.tvStoreAddress_StoreInformation);
        tvStorePhone_StoreInformation = view.findViewById(R.id.tvStorePhone_StoreInformation);
        ivStoreLogo_StoreInformation = view.findViewById(R.id.ivStoreLogo_StoreInformation);

        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入店家資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        db.collection("Store").document(Store_ID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                store = documentSnapshot.toObject(Store.class);
                tvStoreName_StoreInformation.setText(store.getStore_name());
                tvStoreAddress_StoreInformation.setText(store.getStore_address());
                tvStorePhone_StoreInformation.setText(store.getStore_phone());
                store_latitude = store.getStore_latitude();
                store_longitude = store.getStore_longitude();
                if (store.getStore_picture() == null) { //抓商品圖片
                    ivStoreLogo_StoreInformation.setImageResource(R.drawable.no_image);
                } else {
                    showImage(ivStoreLogo_StoreInformation, store.getStore_picture());
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入店家資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝地圖顯示店家位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        mvStoreAddress_StoreInformation = view.findViewById(R.id.mvStoreAddress_StoreInformation);
        mvStoreAddress_StoreInformation.onCreate(savedInstanceState);
        mvStoreAddress_StoreInformation.onStart();
        mvStoreAddress_StoreInformation.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapStoreAddress_StoreInformation = googleMap;
                LatLng store_latlng = new LatLng(store_latitude, store_longitude);
                // 一開始將地圖移動至指定點
                if (store_latitude != null && store_longitude != null) {
                    moveMap(store_latlng);
                    addMarker(store_latlng);
                    // 自訂訊息視窗
                    mapStoreAddress_StoreInformation.setInfoWindowAdapter(new MyInfoWindowAdapter(activity));
                } else {
                    Common.showToast(activity, "222222222222");
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝地圖顯示店家位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_StoreInformation = view.findViewById(R.id.ivBack_StoreInformation);
        ivBack_StoreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝移動到地圖上的店家位置並圖釘＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void addMarker(LatLng latLng) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.map_pin);
        Address address = reverseGeocode(latLng.latitude, latLng.longitude);
        if (address == null) {
            Common.showToast(activity, "店家地址無法判別,請重新輸入");
            return;
        }
        // 取得道路名稱當做標題
        String title = address.getThoroughfare();
        // 取得地址當作說明文字
        String snippet = address.getAddressLine(0);
        mapStoreAddress_StoreInformation.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(icon));
        moveMap(latLng);
    }


    private void moveMap(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(10)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        mapStoreAddress_StoreInformation.animateCamera(cameraUpdate);
    }

    private Address reverseGeocode(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            return null;
        } else {
            return addressList.get(0);
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝移動到地圖上的店家位置並圖釘＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝店家定位點內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        Context context;

        MyInfoWindowAdapter(Context context) {
            this.context = context;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            View view = View.inflate(context, R.layout.dialog_map_info, null);

            String title = marker.getTitle();
            TextView tvTitle = view.findViewById(R.id.tvMapAddress_StoreInformation);
            tvTitle.setText(title);

            String snippet = marker.getSnippet();
            TextView tvSnippet = view.findViewById(R.id.tvMapInfo_StoreInformation);
            tvSnippet.setText(snippet);

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝店家定位點內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


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
