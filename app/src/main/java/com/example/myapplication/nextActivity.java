package com.example.myapplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class nextActivity  extends Activity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Intent i=getIntent();
        String string1=i.getStringExtra("string");
        final TextView my_string=(TextView)findViewById(R.id.tx6);
        my_string.setText("        "+string1);
        my_string.scrollTo(0,0);
        my_string.setMovementMethod(ScrollingMovementMethod.getInstance());
        Log.d("onCreate","d"+string1);
    }
}