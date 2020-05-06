package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class nextActivity extends Activity {
    int b;
    private Dialog progressDialog;
    String chapter_url = "";
    private TextView msg,my_string,title;
    SQLiteDatabase db;
    public String db_name = "gallery.sqlite";
    final DbHelper helper = new DbHelper(this, db_name, null, 1);
    LinearLayout background;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_main);
        db = helper.getWritableDatabase();
        background = (LinearLayout) findViewById(R.id.a123);
        Intent i = getIntent();
        final String book = i.getStringExtra("book");
        Cursor c = db.rawQuery("select * from content where book=?", new String[]{book}, null);
        c.moveToFirst();
        String string2 = c.getString(1);
        String string1 = c.getString(2);
        my_string = (TextView) findViewById(R.id.tx6);
        title = (TextView) findViewById(R.id.title1);
        title.setText(string2);
        my_string.setText("        " + string1);
        my_string.scrollTo(0, 0);
        my_string.setMovementMethod(ScrollingMovementMethod.getInstance());
        List<String> chapter = new ArrayList<String>();
        final List<String> list_chapter_url = new ArrayList<String>();
        final Cursor select_sql = db.rawQuery("select * from " + "_" + book , null);
        for (select_sql.moveToFirst(); !select_sql.isAfterLast(); select_sql.moveToNext()) {
            chapter.add(select_sql.getString(1));
            list_chapter_url.add(select_sql.getString(2));
        }
        color();
        final ArrayAdapter<String> adapter = (new ArrayAdapter<String>(nextActivity.this, android.R.layout.simple_spinner_item, chapter));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                    final Button txt_white = (Button) layout.findViewById(R.id.txt_white);
                    final Button txt_black = (Button) layout.findViewById(R.id.txt_black);
                    final Button txt_green = (Button) layout.findViewById(R.id.txt_green);
                    final Button green_pattern = (Button) layout.findViewById(R.id.green_pattern);
                    final Button night_pattern = (Button) layout.findViewById(R.id.night_pattern);
                    final Button background_white = (Button) layout.findViewById(R.id.background_white);
                    final Button background_black = (Button) layout.findViewById(R.id.background_black);
                    final Button background_green = (Button) layout.findViewById(R.id.background_green);
                    mulu.setAdapter(adapter);
                    mulu.post(new Runnable() {
                        @Override
                        public void run() {
                            Cursor select_id = db.rawQuery("select * from " + "_" + book + " where chapter=?", new String[]{title.getText().toString()});
                            select_id.moveToFirst();
                            int aaa = select_id.getInt(0)-1;
                            mulu.setSelection(aaa, false);
                        }
                    });
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
                            Cursor select_id = db.rawQuery("select * from " + "_" + book + " where chapter=?", new String[]{title.getText().toString()});
                            select_id.moveToFirst();
                            int id = select_id.getInt(0) - 1;
                            select_id.close();
                            Cursor select_up = db.rawQuery("select * from " + "_" + book + " where id=?", new String[]{String.valueOf(id)});
                            select_up.moveToFirst();
                            if (select_up.getCount() == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(nextActivity.this, "没有上一章了", Toast.LENGTH_LONG).show();
                                select_up.close();
                            } else {
                                final String url = select_up.getString(2);
                                select_up.close();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Document doc = Jsoup.connect(url).get();
                                            Elements btEl = doc.select("#content");
                                            final String data_title = doc.select("h1").text();
                                            btEl.select("br").next().append("\\n");
                                            String bt1 = btEl.text();
                                            final String data_text = bt1.replace("\\n", "\n       ");
                                                progressDialog.dismiss();
                                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                                mainHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        title.setText(data_title);
                                                        my_string.setText("        " + data_text);
                                                        my_string.scrollTo(0, 0);
                                                        b = 0;
                                                        db.execSQL("replace into content values(?,?,?)", new String[]{book, data_title, "        " + data_text});
                                                    }
                                                });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Log.e("nextActivity", "");
                                        }
                                    }
                                }).start();
                                aler.dismiss();
                            }
                        }
                    });
                    down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            msg.setText("搜索中,请稍等");
                            progressDialog.show();
                            Cursor select_id = db.rawQuery("select * from " + "_" + book + " where chapter=?", new String[]{title.getText().toString()});
                            select_id.moveToFirst();
                            int id = select_id.getInt(0)+1;
                            select_id.close();
                            Cursor select_down = db.rawQuery("select * from " + "_" + book + " where id=?", new String[]{String.valueOf(id)});
                            select_down.moveToFirst();
                            if (select_down.getCount() == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(nextActivity.this, "没有下一章了", Toast.LENGTH_LONG).show();
                                select_down.close();
                            }else {
                                final String url = select_down.getString(2);
                                select_down.close();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Document doc = Jsoup.connect(url).get();
                                            Elements btEl = doc.select("#content");
                                            final String data_title = doc.select("h1").text();
                                            btEl.select("br").next().append("\\n");
                                            String bt1 = btEl.text();
                                            final String data_text = bt1.replace("\\n", "\n       ");
                                            progressDialog.dismiss();
                                            Handler mainHandler = new Handler(Looper.getMainLooper());
                                            mainHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    title.setText(data_title);
                                                    my_string.setText("        " + data_text);
                                                    my_string.scrollTo(0, 0);
                                                    b = 0;
                                                    db.execSQL("replace into content values(?,?,?)", new String[]{book, data_title, "        " + data_text});

                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                aler.dismiss();
                            }
                        }
                    });
                    mulu.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Cursor select_id = db.rawQuery("select * from " + "_" + book + " where chapter=?", new String[]{title.getText().toString()});
                            select_id.moveToFirst();
                            int aaa = select_id.getInt(0)-1;
                            if (position == aaa) {
                                return;
                            }
                            chapter_url = list_chapter_url.get(position);
                            msg.setText("搜索中,请稍等");
                            progressDialog.show();
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
                                                    b=0;
                                                    db.execSQL("replace into content values(?,?,?)", new String[]{book, data_title, "        " + data_text});
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
                    //字体白色切换
                    txt_white.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            my_string.setTextColor(my_string.getResources().getColor(R.color.white));
                            title.setTextColor(title.getResources().getColor(R.color.white));
                            db.execSQL("update color set text='#FFFFFF'");
                        }
                    });
                    //字体黑色切换
                    txt_black.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            my_string.setTextColor(my_string.getResources().getColor(R.color.black));
                            title.setTextColor(title.getResources().getColor(R.color.black));
                            db.execSQL("update color set text='#000000'");
                        }
                    });
                    //字体绿色切换
                    txt_green.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            my_string.setTextColor(my_string.getResources().getColor(R.color.green));
                            title.setTextColor(title.getResources().getColor(R.color.green));
                            db.execSQL("update color set text='#008000'");
                        }
                    });
                    //背景白色切换
                    background_white.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            background.setBackgroundColor(Color.WHITE);
                            db.execSQL("update color set background='#FFFFFF'");
                        }
                    });
                    //背景黑色切换
                    background_black.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            background.setBackgroundColor(Color.BLACK);
                            db.execSQL("update color set background='#000000'");
                        }
                    });
                    //背景绿色切换
                    background_green.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            background.setBackgroundColor(background.getResources().getColor(R.color.green1));
                            db.execSQL("update color set background='#CCE8CF'");
                        }
                    });
                    //护眼模式
                    green_pattern.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            my_string.setTextColor(Color.BLACK);
                            title.setTextColor(Color.BLACK);
                            background.setBackgroundColor(background.getResources().getColor(R.color.green1));
                            db.execSQL("update color set text='#000000',background='#CCE8CF'");
                        }
                    });
                    //夜间模式
                    night_pattern.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            my_string.setTextColor(Color.WHITE);
                            title.setTextColor(Color.WHITE);
                            background.setBackgroundColor(Color.BLACK);
                            db.execSQL("update color set background='#000000',text='#FFFFFF'");
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

    private void color() {
        Cursor select_color = db.rawQuery("select * from color", null);
        select_color.moveToFirst();
        String text_color = select_color.getString(0);
        String background_color = select_color.getString(1);
        my_string.setTextColor(Color.parseColor(text_color));
        title.setTextColor(Color.parseColor(text_color));
        background.setBackgroundColor(Color.parseColor(background_color));
    }
};