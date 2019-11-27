package com.example.relaxtodrinking;

/***************************************************************/


/***************************************************************/

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.relaxtodrinking.data.News;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NewsManagementFragment extends Fragment {
    private String TAG = "最新消息管理";
    private final static int NOTIFICATION_ID = 0;
    private final static String NOTIFICATION_CHANNEL_ID = "最新消息通知";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private NotificationManager notificationManager;


    private RecyclerView rvNewsList_NewsManagement;
    private ImageView ivBack_NewsManagement, ivNewsPicture_NewsManagement;
    private TextView tvNewsDate_NewsManagement, tvNewsMessage_NewsManagement;
    private Button btInsert_NewsManagement,btEdit_NewsManagement,btSend_NewsManagement;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月d日 HH點mm分", Locale.CHINESE);
    private SimpleDateFormat sdf_list = new SimpleDateFormat("yyyy年MM月d日", Locale.CHINESE);
    private List<News> newses;
    private String news_id;
    private String news_date;
    private String news_message;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity.setTitle("最新消息管理");
        return inflater.inflate(R.layout.fragment_news_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivNewsPicture_NewsManagement = view.findViewById(R.id.ivNewsPicture_NewsManagement);
        tvNewsDate_NewsManagement = view.findViewById(R.id.tvNewsDate_NewsManagement);
        tvNewsMessage_NewsManagement = view.findViewById(R.id.tvNewsMessage_NewsManagement);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvNewsList_NewsManagement = view.findViewById(R.id.rvNewsList_NewsManagement);
        rvNewsList_NewsManagement.setLayoutManager(new LinearLayoutManager(activity));
        showNewsAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入所有列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊新增最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btInsert_NewsManagement= view.findViewById(R.id.btInsert_NewsManagement);
        btInsert_NewsManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("action","新增");
                Navigation.findNavController(view).navigate(R.id.action_newsManagementFragment_to_newsInsertFragment,bundle);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊新增最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊修改最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btEdit_NewsManagement= view.findViewById(R.id.btEdit_NewsManagement);
        btEdit_NewsManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("action","修改");
                bundle.putString("news_id",news_id);
                bundle.putString("news_date",news_date);
                bundle.putString("news_message",news_message);
                bundle.putString("news_picture",news_picture);
                Navigation.findNavController(view).navigate(R.id.action_newsManagementFragment_to_newsInsertFragment,bundle);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊修改最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊發送通知＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSend_NewsManagement= view.findViewById(R.id.btSend_NewsManagement);
        btSend_NewsManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // 重要性越高，提示(打擾)user方式就越明確，設為IMPORTANCE_HIGH會懸浮通知並發出聲音
                    NotificationChannel notificationChannel = new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            "MyNotificationChannel",
                            NotificationManager.IMPORTANCE_HIGH);
                    // 如果裝置有支援，開啟指示燈
                    notificationChannel.enableLights(true);
                    // 設定指示燈顏色
                    notificationChannel.setLightColor(Color.RED);
                    // 開啟震動
                    notificationChannel.enableVibration(true);
                    // 設定震動頻率
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                Intent intent = new Intent(activity, NewsListFragment.class);
                Bitmap icon = BitmapFactory.decodeResource(view.getResources(),R.drawable.add_drink);
                Bitmap largeIcon = ((BitmapDrawable)ivNewsPicture_NewsManagement.getDrawable()).getBitmap();
                NotificationCompat.BigPictureStyle bitStyle = new NotificationCompat.BigPictureStyle();
                bitStyle.bigPicture(largeIcon);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(activity, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setContentTitle("免爽飲茶飲")
                        .setContentText(news_message)
                        .setAutoCancel(true)
                        .setColor(Color.BLUE)
                        .setStyle(bitStyle)
                        .setLargeIcon(icon)
                        .setContentIntent(pendingIntent) // 若無開啟頁面可不寫
                        .build();
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊發送通知＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_NewsManagement = view.findViewById(R.id.ivBack_NewsManagement);
        ivBack_NewsManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單明細列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class NewsItemAdapter extends RecyclerView.Adapter<NewsManagementFragment.NewsItemAdapter.MyViewHolder> {
        Context context;
        List<News> newses;

        public NewsItemAdapter(Context context, List<News> newses) {
            this.context = context;
            this.newses = newses;
        }

        @Override
        public int getItemCount() {
            return newses.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvDateList_NewsManagement;

            public MyViewHolder(View NewsItemView) {
                super(NewsItemView);
                tvDateList_NewsManagement = NewsItemView.findViewById(R.id.tvDateList_NewsManagement);
            }
        }

        @NonNull
        @Override
        public NewsManagementFragment.NewsItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.news_management_view, parent, false);
            return new NewsManagementFragment.NewsItemAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsManagementFragment.NewsItemAdapter.MyViewHolder holder, int position) {
            final News news = newses.get(position);
            holder.tvDateList_NewsManagement.setText(sdf_list.format(news.getNews_date()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        news_id = news.getNews_id();
                        news_date = sdf.format(news.getNews_date());
                        news_message = news.getNews_message();
                        news_picture = news.getNews_picture();
                        tvNewsDate_NewsManagement.setText(news_date);
                        tvNewsMessage_NewsManagement.setText(news_message);
                        if (news_picture == null) {
                            ivNewsPicture_NewsManagement.setImageResource(R.drawable.no_image);
                        } else {
                            showImage(ivNewsPicture_NewsManagement, news_picture);
                        }
                }
            });
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝訂單明細列表內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示訂單明細列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showNewsAll() {
        db.collection("News").orderBy("news_date", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                newses = new ArrayList<>();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    newses.add(snapshot.toObject(News.class));
                }
                //＝＝＝＝＝取得第一筆資料並顯示＝＝＝＝＝//
                if (newses.size() != 0) {
                    News news = newses.get(0);
                    news_id = news.getNews_id();
                    news_date = sdf.format(news.getNews_date());
                    news_message = news.getNews_message();
                    news_picture = news.getNews_picture();
                    tvNewsDate_NewsManagement.setText(news_date);
                    tvNewsMessage_NewsManagement.setText(news_message);
                    if (news_picture == null) {
                        ivNewsPicture_NewsManagement.setImageResource(R.drawable.no_image);
                    } else {
                        showImage(ivNewsPicture_NewsManagement, news_picture);
                    }
                }
                //＝＝＝＝＝取得第一筆資料並顯示＝＝＝＝＝//
                rvNewsList_NewsManagement.setAdapter(new NewsManagementFragment.NewsItemAdapter(activity, newses));
            }
        });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示訂單明細列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


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
