package com.example.relaxtodrinking;
/***************************************************************/
//
/***************************************************************/

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.relaxtodrinking.data.Employee;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.List;


public class DeliveryPositionFragment extends Fragment {
    private String TAG = "外送員位置";
    private static final int PER_ACCESS_LOCATION = 0;
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private GoogleMap mapDeliveryPosition_DeliveryPosition;

    private MapView mvDeliveryPosition_DeliveryPosition;
    private ImageView ivExit_DeliveryPosition;

    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private String emp_id;
    private String action;

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(TAG);
        return inflater.inflate(R.layout.fragment_delivery_position, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入外送員位置資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        mvDeliveryPosition_DeliveryPosition = view.findViewById(R.id.mvDeliveryPosition_DeliveryPosition);
        // 在Fragment生命週期方法內呼叫對應的MapView方法
        mvDeliveryPosition_DeliveryPosition.onCreate(savedInstanceState);
        mvDeliveryPosition_DeliveryPosition.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapDeliveryPosition_DeliveryPosition = googleMap;
                if (action.equals("外送員")) {
                    showMyLocation();
                } else if (action.equals("會員")) {
                    db.collection("Employee").document(emp_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Employee employee = new Employee();
                            if (task.getResult() != null) {
                                employee = task.getResult().toObject(Employee.class);
                                GeoPoint geoPoint = employee.getEmp_position();
                                LatLng emp_latlng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                // 一開始將地圖移動至指定點
                                moveMap(emp_latlng);
                                addMarker(emp_latlng);
                                // 自訂訊息視窗
                                mapDeliveryPosition_DeliveryPosition.setInfoWindowAdapter(new DeliveryPositionFragment.MyInfoWindowAdapter(activity));
                            }
                        }
                    });
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入外送員位置資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_DeliveryPosition = view.findViewById(R.id.ivExit_DeliveryPosition);
        ivExit_DeliveryPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證外送員同意定位＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            action = bundle.getString("action");
            emp_id = bundle.getString("emp_id");
        } else {
            Log.e(TAG, "失敗");

        }
        mvDeliveryPosition_DeliveryPosition.onStart();
        askAccessLocationPermission();
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        lastLocation = location;
                        updateLastLocationInfo(lastLocation);
                    }
                });
            }
        }
    }

    private void askAccessLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int result = ContextCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, PER_ACCESS_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        showMyLocation();
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝驗證外送員同意定位＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示外送員位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    private void showMyLocation() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mapDeliveryPosition_DeliveryPosition.setMyLocationEnabled(true);
        } else {
            Log.e(TAG, "showMyLocation失敗");

        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示外送員位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料庫儲存外送員位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void updateLastLocationInfo(Location lastLocation) {
        if (lastLocation == null) {
            Common.showToast(activity, "遺失最後位置");
            return;
        }
        if (action.equals("外送員")) { //把外送員的目前位置存進資料庫
            GeoPoint emp_point = new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
            db.collection("Employee").document(emp_id).update("emp_position", emp_point);
        }
//        else if (action.equals("會員")) //查看該訂單所屬外送員的位置
//        {
//            db.collection("Employee").document(emp_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.getResult() != null) {
//                        DocumentSnapshot document = task.getResult();
//                        Employee employee = document.toObject(Employee.class);
//                        if (employee.getEmp_position() != null)
//                        {
//                            GeoPoint emp_point = employee.getEmp_position();
//
//                        }else
//                        {
//                            Common.showToast(activity,"外送人員尚未開始進行定位,請稍後再嘗試");
//                        }
//                    }
//                }
//            });
//        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料庫儲存外送員位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝移動到地圖上的外送員位置並圖釘＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void addMarker(LatLng latLng) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.map_pin);
        Log.e(TAG,latLng.latitude+" "+latLng.longitude);
        Address address = reverseGeocode(latLng.latitude, latLng.longitude);
        if (address == null) {
            Common.showToast(activity, "外送員無法判別,請重新輸入");
            return;
        }
        // 取得道路名稱當做標題
        String title = address.getThoroughfare();
        // 取得地址當作說明文字
        String snippet = address.getAddressLine(0);
        mapDeliveryPosition_DeliveryPosition.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(icon));
        moveMap(latLng);
    }


    private void moveMap(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        mapDeliveryPosition_DeliveryPosition.animateCamera(cameraUpdate);
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
            tvTitle.setText("外送員位置");

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
}
