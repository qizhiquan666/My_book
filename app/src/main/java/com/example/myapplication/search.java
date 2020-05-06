package com.example.myapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import androidx.annotation.RequiresApi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class search extends AppCompatActivity {
    private TextView edittext, msg, tx7;
    private ArrayAdapter<String> adapter;
    private static List<String> list_chapter_url;
    private static List<String> chapter;
    private List<String> select_title;
    private List<String> select_url;
    private Spinner spinner, spinner1;
    private Button button1, button2, button5;
    private Dialog progressDialog;
    String chapter_url = "", book_url = "", data_book = "";
    SQLiteDatabase db;
    public String db_name = "gallery.sqlite";
    final DbHelper helper = new DbHelper(this, db_name, null, 1);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        progressDialog = new Dialog(search.this, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        edittext = (TextView) findViewById(R.id.edit1);
        Button button = (Button) findViewById(R.id.but1);
        button.setOnClickListener(new MyonclickListener());
        button1 = (Button) findViewById(R.id.but2);
        button1.setOnClickListener(new MyonclickListener1());
        button2 = (Button) findViewById(R.id.but3);
        button2.setOnClickListener(new MyonclickListener2());
        spinner = (Spinner) findViewById(R.id.spi1);
        spinner1 = (Spinner) findViewById(R.id.spi2);
        button5 = (Button) findViewById(R.id.bt5);
        tx7 = (TextView) findViewById(R.id.tx7);
        Stetho.initializeWithDefaults(this);//查看数据库
        db = helper.getWritableDatabase();
        button5.setOnClickListener(new MyonclickListener3());
    }

    private class MyonclickListener implements OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            String my_string = edittext.getText().toString();
            if (TextUtils.isEmpty(my_string)) {
                Toast.makeText(search.this, "没有数据输入", Toast.LENGTH_LONG).show();
            } else {
                spinner1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                msg.setText("搜索中,请稍等");
                progressDialog.show();
                final String url = "https://www.23txt.com/search.php?keyword=" + my_string;
                select_url = new ArrayList<String>();
                select_title = new ArrayList<String>();
                select_url.clear();
                select_title.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Document doc = Jsoup.connect(url).get();
                            Elements div_page = doc.select("div[class=search-result-page-main]>a");
                            if (div_page.size() > 0) {
                                int cishu = div_page.size() - 3;
                                for (int i = 1; i < cishu; i++) {
                                    Document doc1 = Jsoup.connect(url + "&page=" + i).get();
                                    Elements btEl = doc1.select("div[class=result-game-item-detail]");
                                    Elements resultLinks = btEl.select("h3>a");
                                    for (Element link : resultLinks) {
                                        String bt = link.attr("href");
                                        String bt1 = link.attr("title");
                                        select_url.add(bt);
                                        select_title.add(bt1);
                                    }
                                }
                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //已在主线程中，可以更新UI
                                        adapter = (new ArrayAdapter<String>(search.this, android.R.layout.simple_spinner_item, select_title));
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinner.setAdapter(adapter);
                                        System.out.print(select_title.size());
                                        spinner.setVisibility(View.VISIBLE);
                                        button1.setVisibility(View.VISIBLE);
                                    }
                                });
                                progressDialog.dismiss();
                            } else {
                                Elements btEl = doc.select("div[class=result-game-item-detail]");
                                if (btEl.size() == 0) {
                                    progressDialog.dismiss();
                                    Handler mainHandler = new Handler(Looper.getMainLooper());
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(search.this, "搜索不到该数据或作者", Toast.LENGTH_LONG).show();
                                            spinner.setVisibility(View.GONE);
                                            button1.setVisibility(View.GONE);
                                        }
                                    });
                                } else {
                                    Elements resultLinks = btEl.select("h3>a");
                                    for (Element link : resultLinks) {
                                        String bt = link.attr("href");
                                        String bt1 = link.attr("title");
                                        select_url.add(bt);
                                        select_title.add(bt1);
                                    }
                                    Handler mainHandler = new Handler(Looper.getMainLooper());
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //已在主线程中，可以更新UI
                                            adapter = (new ArrayAdapter<String>(search.this, android.R.layout.simple_spinner_item, select_title));
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinner.setAdapter(adapter);
                                            System.out.print(select_title.size());
                                            spinner.setVisibility(View.VISIBLE);
                                            button1.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    if (select_title.size() > 0) {
                                        progressDialog.dismiss();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(search.this, "搜索异常", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("onClick", "e" + e);
                        }
                    }
                }).start();
            }
        }
    }

    private class MyonclickListener1 implements OnClickListener {
        @Override
        public void onClick(View v) {
            msg.setText("搜索中,请稍等");
            progressDialog.show();
            int nunber = spinner.getSelectedItemPosition();
            book_url = select_url.get(nunber);
            list_chapter_url = new ArrayList<String>();
            chapter = new ArrayList<String>();
            chapter.clear();
            list_chapter_url.clear();
            data_book = select_title.get(nunber);
            final String data_book_url = select_url.get(nunber);
            Cursor select_sql = db.rawQuery("select * from book where book=?", new String[]{select_title.get(nunber)}, null);
            if (select_sql.getCount() == 0) {
                db.execSQL("insert into book values(?,?)", new String[]{data_book, data_book_url});
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(book_url).get();
                        Elements btEl = doc.select("dd");
                        db.execSQL("CREATE TABLE if not exists" + " " + "_" + data_book + "(id INTEGER PRIMARY KEY AUTOINCREMENT, chapter TEXT, chapter_url TEXT)");
                        for (Element link : btEl) {
                            Elements a = link.select("a");
                            String bt = a.attr("href");
                            String bt1 = link.text();
                            list_chapter_url.add("https://www.23txt.com" + bt);
                            Cursor select_sql = db.rawQuery("select * from " + "_" + data_book + " where chapter=?", new String[]{bt1}, null);
                            if (select_sql.getCount() == 0) {
                                db.execSQL("insert into "  + "_" + data_book + " (chapter,chapter_url)values(?,?)", new String[]{bt1, "https://www.23txt.com" + bt});
                            }
                            select_sql.close();
                            chapter.add(bt1);
                        }
                        String paixu = button5.getText().toString();
                        if (paixu.equals("降序")) {
                            Collections.reverse(chapter);
                            Collections.reverse(list_chapter_url);
                        }
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter = (new ArrayAdapter<String>(search.this, android.R.layout.simple_spinner_item, chapter));
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner1.setAdapter(adapter);
                                spinner1.setVisibility(View.VISIBLE);
                                button2.setVisibility(View.VISIBLE);
                                button5.setVisibility(View.VISIBLE);
                                tx7.setVisibility(View.VISIBLE);
                            }
                        });
                        if (chapter.size() > 0) {
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(search.this, "书籍异常", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("onClick", "e" + e);
                    }
                }
            }).start();
        }
    }

    private class MyonclickListener2 implements OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            msg.setText("搜索中,请稍等");
            progressDialog.show();
            final int nunber = spinner1.getSelectedItemPosition();
            chapter_url = list_chapter_url.get(nunber);
            final String paixu = button5.getText().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(chapter_url).get();
                        Elements btEl = doc.select("#content");
                        String title = doc.select("h1").text();
                        btEl.select("br").next().append("\\n");
                        String bt1 = btEl.text();
                        String text = bt1.replace("\\n", "\n       ");
                        db.execSQL("replace into content values(?,?,?)", new String[]{data_book, title, text});
                        if (!text.equals("")) {
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(search.this, "章节异常", Toast.LENGTH_LONG).show();
                        }
                        Intent intent = new Intent(search.this, nextActivity.class);
                        intent.putExtra("book", data_book);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("onClick", "e" + e);
                    }
                }
            }).start();
        }
    }

    private class MyonclickListener3 implements OnClickListener {
        @Override
        public void onClick(View v) {
            String paixu = button5.getText().toString();
            if (paixu.equals("降序")) {
                button5.setText("升序");
                Collections.reverse(chapter);
                Collections.reverse(list_chapter_url);
                adapter = (new ArrayAdapter<String>(search.this, android.R.layout.simple_spinner_item, chapter));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(adapter);
            } else if (paixu.equals("升序")) {
                button5.setText("降序");
                Collections.reverse(chapter);
                Collections.reverse(list_chapter_url);
                adapter = (new ArrayAdapter<String>(search.this, android.R.layout.simple_spinner_item, chapter));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(adapter);
            }
        }
    }

//    public static class requests {
//        private String title1, text,book_url;
//        HashMap<String, String> hashMap = new HashMap<>();

//        String select_chapter(book_url) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Document doc = Jsoup.connect(book_url).get();
//                        Elements btEl = doc.select("dd");
//                        db.execSQL("CREATE TABLE if not exists" + " " + "_" + data_book + "(id INTEGER PRIMARY KEY AUTOINCREMENT, chapter TEXT, chapter_url TEXT)");
//                        for (Element link : btEl) {
//                            Elements a = link.select("a");
//                            String bt = a.attr("href");
//                            String bt1 = link.text();
//                            list_chapter_url.add("https://www.23txt.com" + bt);
//                            Cursor select_sql = db.rawQuery("select * from" + " " + "_" + data_book + " " + "where chapter=?", new String[]{bt1}, null);
//                            if (select_sql.getCount() == 0) {
//                                db.execSQL("insert into" + " " + "_" + data_book + " (chapter,chapter_url)values(?,?)", new String[]{bt1, "https://www.23txt.com" + bt});
//                            }
//                            select_sql.close();
//                            chapter.add(bt1);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e("onClick", "e" + e);
//
//                    }
//                }
//            }).start();
//        }

//        List<String> select_txt() {
//            return chapter;
//        }
//    }
}