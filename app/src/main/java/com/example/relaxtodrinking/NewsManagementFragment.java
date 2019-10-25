package com.example.relaxtodrinking;

/***************************************************************/


/***************************************************************/

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.relaxtodrinking.data.News;
import com.example.relaxtodrinking.data.OrderItem;
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

public class NewsManagementFragment extends Fragment {
    private String TAG = "最新消息管理";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private RecyclerView rvNewsList_NewsManagement;
    private ImageView ivBack_NewsManagement, ivNewsPicture_NewsManagement;
    private TextView tvNewsDate_NewsManagement, tvNewsMessage_NewsManagement;
    private Button btInsert_NewsManagement,btEdit_NewsManagement,btSend_NewsManagement;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月d日 HH點mm分", Locale.CHINESE);
    private SimpleDateFormat sdf_list = new SimpleDateFormat("yyyy年MM月d日", Locale.CHINESE);
    private List<News> newses;

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

            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊新增最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊修改最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btEdit_NewsManagement= view.findViewById(R.id.btEdit_NewsManagement);
        btEdit_NewsManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊修改最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊發送通知＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSend_NewsManagement= view.findViewById(R.id.btSend_NewsManagement);
        btSend_NewsManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊發送通知＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_NewsManagement = view.findViewById(R.id.ivBack_NewsManagement);
        ivBack_NewsManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                        tvNewsDate_NewsManagement.setText(sdf.format(news.getNews_date()));
                        tvNewsMessage_NewsManagement.setText(news.getNews_message());
                        if (news.getNews_picture() == null) {
                            ivNewsPicture_NewsManagement.setImageResource(R.drawable.no_image);
                        } else {
                            showImage(ivNewsPicture_NewsManagement, news.getNews_picture());
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
                    tvNewsDate_NewsManagement.setText(sdf.format(news.getNews_date()));
                    tvNewsMessage_NewsManagement.setText(news.getNews_message());
                    if (news.getNews_picture() == null) {
                        ivNewsPicture_NewsManagement.setImageResource(R.drawable.no_image);
                    } else {
                        showImage(ivNewsPicture_NewsManagement, news.getNews_picture());
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
