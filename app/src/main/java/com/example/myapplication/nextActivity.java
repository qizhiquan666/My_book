package com.example.myapplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.nsd.NsdManager;
import android.util.Log;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class nextActivity  extends Activity {
    int b;
    private Dialog progressDialog;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Intent i = getIntent();
        String string1 = i.getStringExtra("string");
        final TextView my_string = (TextView) findViewById(R.id.tx6);
        my_string.setText("        " + string1);
        my_string.scrollTo(0, 0);
        my_string.setMovementMethod(ScrollingMovementMethod.getInstance());
        b = 0;
        progressDialog = new Dialog(nextActivity.this, R.style.DialogTheme);
        progressDialog.setContentView(R.layout.set);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = progressDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        progressDialog.getWindow().setAttributes(lp);
        progressDialog.getWindow().setGravity(Gravity.BOTTOM);
        my_string.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = my_string.getScrollY();
                if (a - b > 10) {
                    b = a;
                } else if (b - a > 10) {
                    b = a;
                } else {
                    final LayoutInflater inflater = getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.set, null);
                    final EditText edit2 = (EditText) layout.findViewById(R.id.edit2);
                    final Dialog aler = new AlertDialog.Builder(nextActivity.this)
                            .setView(layout)
                            .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String a = edit2.getText().toString();
                                    float c = Float.parseFloat(a);
                                    my_string.setTextSize(c);
                                    edit2.setText(edit2.getText().toString());
                                }
                            }).show();
                }
            }
        });
    }
};