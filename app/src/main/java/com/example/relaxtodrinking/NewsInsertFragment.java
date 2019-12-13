package com.example.relaxtodrinking;

/***************************************************************/
//新增無法及時顯示
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
import android.provider.MediaStore;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.relaxtodrinking.data.News;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class NewsInsertFragment extends Fragment {
    private String TAG = "消息新增";
    private static final int ACTION_AgreeUseAlbum = 200;
    private static final int ACTION_ChoiceFromAlbum = 102;
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private TextView tvTitle_NewsInsert,tvNewsDate_NewsInsert;
    private EditText etNewsMessage_NewsInsert;
    private ImageView ivNewsPicture_NewsInsert,ivExit_NewsInsert;
    private Button btChoiceFromAlbum_NewsInsert,btSubmit_NewsInsert;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月d日 HH點mm分", Locale.CHINESE);
    private Boolean pictureTaken = false;
    private Uri pick_picture_uri;
    private News news;
    private String action = "";
    private String news_id = "";
    private String news_date = "";
    private String news_message = "";
    private String news_picture;
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
        activity.setTitle("消息新增");
        return inflater.inflate(R.layout.fragment_news_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle_NewsInsert = view.findViewById(R.id.tvTitle_NewsInsert);
        btSubmit_NewsInsert = view.findViewById(R.id.btSubmit_NewsInsert);
        tvNewsDate_NewsInsert = view.findViewById(R.id.tvNewsDate_NewsInsert);
        etNewsMessage_NewsInsert = view.findViewById(R.id.etNewsMessage_NewsInsert);
        ivNewsPicture_NewsInsert = view.findViewById(R.id.ivNewsPicture_NewsInsert);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝抓取bundle的值並顯示在頁面＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString("action").equals("修改")) //判別是新增還是修改
            {
                news_id = bundle.getString("news_id");
                news_date = bundle.getString("news_date");
                news_message = bundle.getString("news_message");
                news_picture = bundle.getString("news_picture");
                tvTitle_NewsInsert.setText("消息修改");
                btSubmit_NewsInsert.setText("確定修改");
            }
            else
            {
                tvTitle_NewsInsert.setText("消息新增");
                btSubmit_NewsInsert.setText("確定新增");
                news_date = sdf.format(new Date());
            }
        }
        tvNewsDate_NewsInsert.setText(news_date);
        etNewsMessage_NewsInsert.setText(news_message);
        if (news_picture == null) { //抓商品圖片
            ivNewsPicture_NewsInsert.setImageResource(R.drawable.no_image);
        } else {
            showImage(ivNewsPicture_NewsInsert, news_picture);
        }
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝抓取bundle的值並顯示在頁面＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSubmit_NewsInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                news = new News();
                if (news_id.equals(""))
                {
                    news.setNews_id(db.collection("News").document().getId());
                }else
                {
                    news.setNews_id(news_id);
                }
                try {
                    news.setNews_date(sdf.parse(news_date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                news.setNews_message(etNewsMessage_NewsInsert.getText().toString());
                //＝＝＝＝＝如果有拍照上傳至storage＝＝＝＝＝//
                if (pictureTaken) {
                    final String imagePath = "image/news/"+news.getNews_id();
                    storage.getReference().child(imagePath).putFile(pick_picture_uri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "上傳最新消息圖片成功");
                                        news.setNews_picture(imagePath);
                                        insertNews(news,view);
                                    } else {
                                        Log.e(TAG, "上傳最新消息圖片失敗");
                                        Common.showToast(activity, "上傳最新消息圖片失敗");
                                    }
                                }
                            });
                }else {
                    news.setNews_picture(news_picture);
                    insertNews(news,view);
                }
                //＝＝＝＝＝如果有拍照上傳至storage＝＝＝＝＝//
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊確定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊挑選相簿圖片＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btChoiceFromAlbum_NewsInsert = view.findViewById(R.id.btChoiceFromAlbum_NewsInsert);
        btChoiceFromAlbum_NewsInsert.setOnClickListener(new View.OnClickListener() {
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


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivExit_NewsInsert = view.findViewById(R.id.ivExit_NewsInsert);
        ivExit_NewsInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊離開＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
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
                btSubmit_NewsInsert.setEnabled(false);
            } else {
                btSubmit_NewsInsert.setEnabled(true);
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
                    ivNewsPicture_NewsInsert.setImageBitmap(bitmap);
                    pictureTaken = true;
                } else {
                    ivNewsPicture_NewsInsert.setImageResource(R.drawable.no_image);
                }
            }
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝圖片抓取成功顯示在頁面上＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料上傳至firebase＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void insertNews(News news, final View view) {
        db.collection("News").document(news.getNews_id()).set(news)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (action.equals("修改"))
                            {
                                Log.d(TAG, "店家訊息修改成功");
                                Common.showToast(activity, "店家訊息修改成功");
                            }else
                            {
                                Log.d(TAG, "店家訊息新增成功");
                                Common.showToast(activity, "店家訊息新增成功");
                            }
                        } else {
                            Log.d(TAG, "最新消息操作失敗");
                            Common.showToast(activity, "最新消息操作失敗");
                        }
                        Navigation.findNavController(view).popBackStack();
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝資料上傳至firebase＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
