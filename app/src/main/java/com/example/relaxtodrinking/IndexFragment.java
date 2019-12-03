package com.example.relaxtodrinking;
/***************************************************************/
//首頁最新消息輪播紅點
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.relaxtodrinking.data.Employee;
import com.example.relaxtodrinking.data.News;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IndexFragment extends Fragment {
    private String TAG = "首頁";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private Button btUser_Index,btProduct_Index,btOrder_Index,btStore_Index;
    private RecyclerView rvNewsList_Index;

    private String user_id = "";
    private String order_id = "無訂單";
    private List<News> newses;
    private ScheduledExecutorService scheduledExecutorService;
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
        return inflater.inflate(R.layout.fragment_index, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user_id = user.getUid();
            //＝＝＝＝＝判別使用者的身份＝＝＝＝＝//
            db.collection("Employee").whereEqualTo("user_id",auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<Employee> employees = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            employees.add(document.toObject(Employee.class));
                        }
                        if (employees.size() != 0) { //沒有員工資料等於普通使用者
                            Employee employee = employees.get(0);
                            if (employee.getEmp_permission() == 0) //權限0是最高管理員,權限1是管理員,權限2是店員
                            {
                                Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_managementListFragment);
                            }else if (employee.getEmp_permission() == 1)
                            {
                                Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_managementListFragment);
                            }else if (employee.getEmp_permission() == 2)
                            {
                                Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_deliveryManagementFragment);
                            }
                        }
                    } else {
                        Log.e(TAG, "無法判別登入者身份");
                    }
                }
            });
            //＝＝＝＝＝判別使用者的身份＝＝＝＝＝//
        }else{
            user_id = "";
        }
        Log.e(TAG,"user_id"+user_id);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝判斷使用者有無登入 有的話取得ID＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvNewsList_Index = view.findViewById(R.id.rvNewsList_Index);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvNewsList_Index.setLayoutManager(linearLayoutManager);
        rvNewsList_Index.setHasFixedSize(true);
        showNewsAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝輪播設定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvNewsList_Index);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                rvNewsList_Index.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + 1);
            }
        }, 2000, 2000, TimeUnit.MILLISECONDS);
        rvNewsList_Index.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int i = linearLayoutManager.findFirstVisibleItemPosition() % newses.size();
                    //得到指示器紅點的位置
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝輪播設定＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊會員專區＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btUser_Index = view.findViewById(R.id.btUser_Index);
        btUser_Index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (user_id == null || user_id.equals("")) {
                    Common.showToast(activity,"請先登入");
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_userLoginFragment);
                }else
                {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_userManagementFragment);
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊會員專區＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊商品瀏覽＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btProduct_Index = view.findViewById(R.id.btProduct_Index);
        btProduct_Index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_productListFragment);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊商品瀏覽＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊訂單紀錄＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btOrder_Index = view.findViewById(R.id.btOrder_Index);
        btOrder_Index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (user_id == null || user_id.equals(""))
                {
                    Common.showToast(activity,"請先登入");
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_userLoginFragment);
                }else
                {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_orderListFragment);
                }
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊訂單紀錄＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊店家資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btStore_Index = view.findViewById(R.id.btStore_Index);
        btStore_Index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_storeInformationFragment);
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊店家資訊＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝最新消息內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class NewsAdapter extends RecyclerView.Adapter<IndexFragment.NewsAdapter.MyViewHolder> {
        Context context;
        List<News> newses;

        public NewsAdapter(Context context, List<News> newses) {
            this.context = context;
            this.newses = newses;
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivNewsPicture_NewsList;
            private TextView tvNewsDate_NewsList,tvMessage_NewsList;
            public MyViewHolder(View NewsView) {
                super(NewsView);
                ivNewsPicture_NewsList = NewsView.findViewById(R.id.ivNewsPicture_NewsList);
                tvNewsDate_NewsList = NewsView.findViewById(R.id.tvNewsDate_NewsList);
                tvMessage_NewsList = NewsView.findViewById(R.id.tvMessage_NewsList);
            }
        }
        @NonNull
        @Override
        public IndexFragment.NewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.news_list_view, parent, false);
            return new IndexFragment.NewsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final IndexFragment.NewsAdapter.MyViewHolder holder, int position) {
            final News news = newses.get(position % newses.size());
            holder.tvMessage_NewsList.setVisibility(View.GONE);
            holder.tvNewsDate_NewsList.setVisibility(View.GONE);
            if (news.getNews_picture() == null) { //抓消息圖片
                holder.ivNewsPicture_NewsList.setImageResource(R.drawable.no_image);
            } else {
                   showImage(holder.ivNewsPicture_NewsList, news.getNews_picture());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.action_indexFragment_to_newsListFragment);
                }
            });
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝最新消息內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showNewsAll() {
        db.collection("News").orderBy("news_date", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            newses = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                newses.add(document.toObject(News.class));
                            }
                            rvNewsList_Index.setAdapter(new IndexFragment.NewsAdapter(activity, newses));
                            rvNewsList_Index.scrollToPosition(newses.size()*10);
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示最新消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


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


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝離開頁面停止輪播＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    @Override
    public void onStop() {
        super.onStop();
        scheduledExecutorService.shutdown();
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝離開頁面停止輪播＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
}
