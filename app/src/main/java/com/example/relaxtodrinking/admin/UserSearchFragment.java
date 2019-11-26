package com.example.relaxtodrinking.admin;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.relaxtodrinking.R;
import com.example.relaxtodrinking.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class UserSearchFragment extends Fragment {
    private String TAG = "會員查詢";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;


    private Button btSearchItem_UserSearch;
    private SearchView svSearchUser_UserSearch;
    private RecyclerView rvUserList_UserSearch;
    private ImageView ivBack_UserSearch;

    private int means = R.id.nameItem_UserSearch;
    private List<User> users;


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    @Override
    public void onResume() {
        super.onResume();
        svSearchUser_UserSearch.setQuery("",false);
    }

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
        return inflater.inflate(R.layout.fragment_user_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入會員列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        rvUserList_UserSearch = view.findViewById(R.id.rvUserList_UserSearch);
        rvUserList_UserSearch.setLayoutManager(new LinearLayoutManager(activity));
        showUsersAll();
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝載入會員列表＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊用XXX搜尋＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        btSearchItem_UserSearch = view.findViewById(R.id.btSearchItem_UserSearch);
        btSearchItem_UserSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.inflate(R.menu.user_search_means_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        means = item.getItemId();
                        btSearchItem_UserSearch.setText(item.getTitle());
                        svSearchUser_UserSearch.setQuery("",false);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊用XXX搜尋＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝搜尋使用者＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        svSearchUser_UserSearch = view.findViewById(R.id.svSearchUser_UserSearch);
        svSearchUser_UserSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                UserSearchFragment.UserAdapter adapter = (UserSearchFragment.UserAdapter) rvUserList_UserSearch.getAdapter();
                if (adapter != null) {
                    if (newText.isEmpty()) {
                        adapter.setUsers(users);
                    } else {
                        List<User> searchUsers = new ArrayList<>();
                        for (User user : users) {
                            String text;
                            switch (means) {
                                case R.id.nameItem_UserSearch:
                                    text = user.getUser_name();
                                    break;
                                case R.id.phoneItem_UserSearch:
                                    text = user.getUser_phone();
                                    break;
                                case R.id.addressItem_UserSearch:
                                    text = user.getUser_address();
                                    break;
                                default:
                                    Log.e(TAG,"means參數錯誤");
                                    return false;
                            }
                            if (text.toUpperCase().contains(newText.toUpperCase())) {
                                searchUsers.add(user);
                            }
                        }
                        adapter.setUsers(searchUsers);
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
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝搜尋使用者＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
        ivBack_UserSearch = view.findViewById(R.id.ivBack_UserSearch);
        ivBack_UserSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝點擊回上一頁＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    }

    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示會員內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private class UserAdapter extends RecyclerView.Adapter<UserSearchFragment.UserAdapter.MyViewHolder> {
        Context context;
        List<User> users;

        public void setUsers(List<User> users){
            this.users = users;
        }

        public UserAdapter(Context context, List<User> users) {
            this.context = context;
            this.users = users;
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvName_UserSearch,tvPhone_UserSearch,tvEmail_UserSearch,tvAddress_UserSearch,tvTime_UserSearch;
            public MyViewHolder(View NewsView) {
                super(NewsView);
                tvName_UserSearch = NewsView.findViewById(R.id.tvName_UserSearch);
                tvPhone_UserSearch = NewsView.findViewById(R.id.tvPhone_UserSearch);
                tvEmail_UserSearch = NewsView.findViewById(R.id.tvEmail_UserSearch);
                tvAddress_UserSearch = NewsView.findViewById(R.id.tvAddress_UserSearch);
            }
        }
        @NonNull
        @Override
        public UserSearchFragment.UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.user_list_view, parent, false);
            return new UserSearchFragment.UserAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final UserSearchFragment.UserAdapter.MyViewHolder holder, int position) {
            final User user = users.get(position);
            holder.tvName_UserSearch.setText(user.getUser_name());
            holder.tvPhone_UserSearch.setText(user.getUser_phone());
            holder.tvEmail_UserSearch.setText(user.getUser_email());
            holder.tvAddress_UserSearch.setText(user.getUser_address());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id",user.getUser_id());
                    Navigation.findNavController(view).navigate(R.id.action_userSearchFragment_to_orderHistoryFragment,bundle);
                }
            });
        }
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示會員內容＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//


    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示會員＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private void showUsersAll() {
        db.collection("User").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            users = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                users.add(document.toObject(User.class));
                            }
                            rvUserList_UserSearch.setAdapter(new UserSearchFragment.UserAdapter(activity, users));
                        }
                    }
                });
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝顯示會員＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

}
