package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class nextActivity extends Activity {
    int b, nunber1;
    private Dialog progressDialog;
    String chapter_url = "";
    private TextView msg;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        final MainActivity.Test ddd = new MainActivity.Test();
        final List<String> list_chapter_url = ddd.chapter_url();
        final List<String> chapter = ddd.chapter_txt();
        Log.d("-----------", String.valueOf(list_chapter_url));
        Intent i = getIntent();
        String string1 = i.getStringExtra("content");
        String string2 = i.getStringExtra("title");
        final String nunber = i.getStringExtra("nunber");
        final String paixu = i.getStringExtra("paixu");
        nunber1 = Integer.valueOf(nunber).intValue();
        final TextView my_string = (TextView) findViewById(R.id.tx6);
        final TextView title = (TextView) findViewById(R.id.title1);
        title.setText(string2);
        my_string.setText("        " + string1);
        my_string.scrollTo(0, 0);
        my_string.setMovementMethod(ScrollingMovementMethod.getInstance());
        b = 0;
        progressDialog = new Dialog(nextActivity.this, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);

        my_string.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int a = my_string.getScrollY();
                if (a - b > 10) {
                    b = a;
                } else if (b - a > 10) {
                    b = a;
                } else {
                    final LayoutInflater inflater = getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.set, null);
                    final EditText edit2 = (EditText) layout.findViewById(R.id.edit2);
                    final Spinner mulu = (Spinner) layout.findViewById(R.id.mulu);
                    ArrayAdapter<String> adapter = (new ArrayAdapter<String>(nextActivity.this, android.R.layout.simple_spinner_item, chapter));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mulu.setAdapter(adapter);
                    mulu.setSelection(nunber1);
                    final Dialog aler = new AlertDialog.Builder(nextActivity.this)
                            .setView(layout)
                            .show();
                    Button button = (Button) layout.findViewById(R.id.set_size);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String d = edit2.getText().toString();
                            float c = Float.parseFloat(d);
                            my_string.setTextSize(c);
                            title.setTextSize(c);
                            edit2.setText(edit2.getText().toString());
                        }
                    });
                    TextView up = (TextView) layout.findViewById(R.id.up);
                    final TextView down = (TextView) layout.findViewById(R.id.down);
                    up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msg.setText("搜索中,请稍等");
                            progressDialog.show();
                            try {
                                if (paixu.equals("降序")) {
                                    nunber1 = nunber1 + 1;
                                } else {
                                    nunber1 = nunber1 - 1;
                                }
                                if (nunber1 < 0) {
                                    progressDialog.dismiss();
                                    nunber1 = nunber1 + 1;
                                    Toast.makeText(nextActivity.this, "没有上一章了", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                chapter_url = list_chapter_url.get(nunber1);
                                if (!chapter_url.equals("")) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                System.out.print(chapter_url);
                                                Document doc = Jsoup.connect(chapter_url).get();
                                                Elements btEl = doc.select("#content");
                                                final String data_title = doc.select("h1").text();
                                                btEl.select("br").next().append("\\n");
                                                String bt1 = btEl.text();
                                                final String data_text = bt1.replace("\\n", "\n       ");
                                                if (!data_text.equals("")) {
                                                    progressDialog.dismiss();
                                                    Handler mainHandler = new Handler(Looper.getMainLooper());
                                                    mainHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            title.setText(data_title);
                                                            my_string.setText("        " + data_text);
                                                            my_string.scrollTo(0, 0);
                                                        }
                                                    });
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(nextActivity.this, "章节异常", Toast.LENGTH_LONG).show();
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                Log.e("nextActivity", "");
                                            }
                                        }
                                    }).start();
                                    aler.dismiss();
                                } else {
                                    Toast.makeText(nextActivity.this, "没有上一章了", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Log.e("click error", e.getMessage());
                            }
                        }

                    });
                    down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msg.setText("搜索中,请稍等");
                            progressDialog.show();
                            if (paixu.equals("降序")) {
                                nunber1 = nunber1 - 1;
                            } else {
                                nunber1 = nunber1 + 1;
                            }
                            if (nunber1 < 0) {
                                progressDialog.dismiss();
                                nunber1 = nunber1 + 1;
                                Toast.makeText(nextActivity.this, "没有下一章了", Toast.LENGTH_LONG).show();
                                return;
                            }
                            chapter_url = list_chapter_url.get(nunber1);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Document doc = Jsoup.connect(chapter_url).get();
                                        Elements btEl = doc.select("#content");
                                        final String data_title = doc.select("h1").text();
                                        btEl.select("br").next().append("\\n");
                                        String bt1 = btEl.text();
                                        final String data_text = bt1.replace("\\n", "\n       ");
                                        if (!data_text.equals("")) {
                                            progressDialog.dismiss();
                                            Handler mainHandler = new Handler(Looper.getMainLooper());
                                            mainHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    title.setText(data_title);
                                                    my_string.setText("        " + data_text);
                                                    my_string.scrollTo(0, 0);
                                                }
                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(nextActivity.this, "章节异常", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            aler.dismiss();
                        }
                    });
                    mulu.setSelection(0,false);
                    mulu.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            Log.d("---------", String.valueOf(position));
                            Log.d("---------", String.valueOf(id));
                            chapter_url = list_chapter_url.get(position);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Document doc = Jsoup.connect(chapter_url).get();
                                        Elements btEl = doc.select("#content");
                                        final String data_title = doc.select("h1").text();
                                        btEl.select("br").next().append("\\n");
                                        String bt1 = btEl.text();
                                        final String data_text = bt1.replace("\\n", "\n       ");
                                        if (!data_text.equals("")) {
                                            progressDialog.dismiss();
                                            Handler mainHandler = new Handler(Looper.getMainLooper());
                                            mainHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    title.setText(data_title);
                                                    my_string.setText("        " + data_text);
                                                    my_string.scrollTo(0, 0);
                                                }
                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(nextActivity.this, "章节异常", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    Window window = aler.getWindow();
                    window.setBackgroundDrawable(null); // 重设background
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.BOTTOM;
                    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    wlp.width = display.getWidth();
                    aler.getWindow().setBackgroundDrawable(null);
                    window.setAttributes(wlp);
                }
            }
        });
    }
};