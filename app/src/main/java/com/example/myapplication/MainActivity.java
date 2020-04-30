package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    public String db_name = "gallery.sqlite";
    final DbHelper helper = new DbHelper(this, db_name, null, 1);

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);//查看数据库
        db = helper.getWritableDatabase();
        final LinearLayout ll = (LinearLayout) findViewById(R.id.content_view);
        final Cursor c = db.rawQuery("select * from book", null);
        long now_time = System.currentTimeMillis() / 1000;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            final String book = c.getString(0);
            final String book_url = c.getString(1);
            int time = c.getInt(2);
            Cursor b = db.rawQuery("select * from content where book=?", new String[]{book}, null);
            if (b.getCount() == 0) {
                db.execSQL("delete from book where book=?", new String[]{book});
                b.close();
            } else {
                if (now_time - time > 10800) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Document doc = Jsoup.connect(book_url).get();
                                Elements btEl = doc.select("dd");
                                for (Element link : btEl) {
                                    Elements a = link.select("a");
                                    String bt = a.attr("href");
                                    String bt1 = link.text();
                                    Cursor select_sql = db.rawQuery("select * from" + " " + "_" + book + " " + "where chapter=?", new String[]{bt1}, null);
                                    if (select_sql.getCount() == 0) {
                                        db.execSQL("insert into" + " " + "_" + book + " values(?,?)", new String[]{bt1, "https://www.23txt.com" + bt});
                                    }
                                    select_sql.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("onClick", "e" + e);
                            }
                        }
                    }).start();
                    db.execSQL("update book set time=? where book=?",new String[]{String.valueOf(now_time),book});
                }
            }
        }
            c.close();
            Cursor d = db.rawQuery("select * from book", null);
            if (d.getCount() == 0) {
                TextView tv = new TextView(this);
                tv.setText("书架无数据");
                tv.setTextSize(20);
                tv.setGravity(Gravity.CENTER);
                ll.addView(tv);
                Button bt = new Button(this);
                bt.setText("去搜索书籍");
                ll.addView(bt);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("time", String.valueOf(System.currentTimeMillis() / 1000));
                        Intent intent = new Intent(MainActivity.this, search.class);
                        startActivity(intent);
                        finish();
                    }
                });
                d.close();
            } else {
                for (d.moveToFirst(); !d.isAfterLast(); d.moveToNext()) {
                    String aaaaa = d.getString(0);
                    final Button bt = new Button(this);
                    bt.setText(aaaaa);
                    ll.addView(bt);
                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, nextActivity.class);
                            intent.putExtra("book", bt.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                    });
                    bt.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Dialog aler = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("删除书籍:" + bt.getText().toString())
                                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db.execSQL("delete from book where book=?", new String[]{bt.getText().toString()});
                                            Toast.makeText(MainActivity.this, "删除书籍成功", Toast.LENGTH_LONG).show();
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    }).show();
                            return false;
                        }
                    });
                }
            }
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            //使用菜单填充器获取menu下的菜单资源文件
            getMenuInflater().inflate(R.menu.search_menu, menu);
            //获取搜索的菜单组件
            MenuItem menuItem = menu.findItem(R.id.search);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(MainActivity.this, search.class);
                    startActivity(intent);
                    return false;
                }
            });
            return super.onCreateOptionsMenu(menu);
        }
    public void onBackPressed() {
        System.exit(0);
        super.onBackPressed();
    }
}
