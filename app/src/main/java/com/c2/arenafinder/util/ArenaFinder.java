package com.c2.arenafinder.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.c2.arenafinder.R;
import com.c2.arenafinder.data.local.LogApp;
import com.c2.arenafinder.data.local.LogTag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArenaFinder {

    public static final int VIBRATOR_SHORT = 150;
    public static final int VIBRATOR_MEDIUM = 500;
    public static final int VIBRATOR_LONG = 1000;
    public static final int MILLIS_OF_REFRESHING = 1500;
    public static final int TRANSPARENT_STATUS_BAR = 1_1;
    public static final int WHITE_STATUS_BAR = 6_0_9;

    public static void playVibrator(@NonNull Context context, int millis){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // API > 26
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            // API 26 kebawah
            vibrator.vibrate(millis);
        }
    }

    public static boolean isInternetConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    public static void restartApplication(@NonNull Context context, Class<?> activity){
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        LogApp.info(context, LogTag.APPLICATION, "Aplikasi direstart");
    }

    public static void closeApplication(@NonNull AppCompatActivity app){
        app.finishAffinity();
        System.exit(0);
        LogApp.info(app, LogTag.APPLICATION, "Aplikasi ditutup");
    }

    public static void closeApplication(@NonNull FragmentActivity app){
        app.finishAffinity();
        System.exit(0);
        LogApp.info(app, LogTag.APPLICATION, "Aplikasi ditutup");
    }

    public static void VibratorToast(Context context, String msg, int toastLong, int vibratorLong){
        playVibrator(context, vibratorLong);
        Toast.makeText(context, msg, toastLong).show();
        LogApp.info(context, LogTag.ON_VIBRATOR_TOAST, msg);
    }

    public static void VibratorToast(Context context, @StringRes int msg, int toastLong, int vibratorLong){
        VibratorToast(context, context.getString(msg), toastLong, vibratorLong);
    }

    public static String convertToDate(@NonNull String date){
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date inputDate = inputDateFormat.parse(date);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
            assert inputDate != null;
            return outputDateFormat.format(inputDate);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return inputDateFormat.toString();
    }

    public static void setRecyclerWidthByItem(
            Context context, RecyclerView recyclerView,
            int itemCount, @DimenRes int itemWidth
    ){
        int totalItemWidth = 0;
        for (int i = 0; i < itemCount; i++) {
            totalItemWidth += context.getResources().getDimensionPixelOffset(itemWidth);
        }

        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        layoutParams.width = totalItemWidth;
        recyclerView.setLayoutParams(layoutParams);
    }

    public static void showAlertDialog(
            Context context, String title, String message, boolean cancelable,
            DialogInterface.OnClickListener positiveAction, DialogInterface.OnClickListener negativeAction
    ){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(context.getString(R.string.dia_positive_ok), positiveAction)
                .setNegativeButton(context.getString(R.string.dia_negative_cancel), negativeAction)
                .create()
                .show();

    }

    @SuppressLint("Deprecated")
    public static void setStatusBarColor(Activity activity, int status, @ColorRes int color, boolean isLightStatusBar) {
        // Set window flags for transparent status bar
        activity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                    case TRANSPARENT_STATUS_BAR:
                        activity.getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        );
                        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
                        break;
                    case WHITE_STATUS_BAR:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, color));
                            if (isLightStatusBar){
                                activity.getWindow().getDecorView().setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_VISIBLE
                                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                );
                            }else {
                                activity.getWindow().getDecorView().setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_VISIBLE
                                );
                            }

                        } else {
                            activity.getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_VISIBLE);
                        }
                        break;
                }
            }
        });
    }


}
