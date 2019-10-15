package com.example.relaxtodrinking;

import android.app.Activity;
import android.widget.Toast;

public class Common {
    public static void showToast(Activity activity, String messageRes) {
        Toast.makeText(activity, messageRes, Toast.LENGTH_SHORT).show();
    }




}

