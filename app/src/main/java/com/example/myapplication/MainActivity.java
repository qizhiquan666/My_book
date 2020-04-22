package com.example.myapplication;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    private TextView edittext,msg,tx7;
    private ArrayAdapter<String> adapter;
    private List<String> list_chapter_url,chapter,select_title,select_url;
    private Spinner spinner,spinner1;
    private Button button1,button2,button5;
    private Dialog progressDialog;
    String chapter_url="",book_url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new Dialog(MainActivity.this,R.style.progress_dialog);
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
        button5=(Button)findViewById(R.id.bt5);
        tx7=(TextView)findViewById(R.id.tx7);
        button5.setOnClickListener(new MyonclickListener3());
    }
    private class MyonclickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String my_string = edittext.getText().toString();
            if (TextUtils.isEmpty(my_string)) {
                Toast.makeText(MainActivity.this, "没有数据输入", Toast.LENGTH_LONG).show();
            } else {
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
                            //                Log.d("onClick","d"+doc);
                            Elements btEl = doc.select("div[class=result-game-item-detail]");
                            ;
                            if (btEl.size()==0){
                                progressDialog.dismiss();
                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "搜索不到该数据或作者", Toast.LENGTH_LONG).show();
                                        spinner.setVisibility(View.GONE);
                                        button1.setVisibility(View.GONE);
                                    }
                                });
                            }else {
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
                                    adapter = (new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, select_title));
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinner.setAdapter(adapter);
                                    System.out.print(select_title.size());
                                    spinner.setVisibility(View.VISIBLE);
                                    button1.setVisibility(View.VISIBLE);
                                }
                            });
                            if (select_title.size() > 0) {
                                progressDialog.dismiss();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "搜索异常", Toast.LENGTH_LONG).show();
                            }
                        } }catch (Exception e) {
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
            int nunber=spinner.getSelectedItemPosition();
            book_url=select_url.get(nunber);
            list_chapter_url=new ArrayList<String>();
            chapter = new ArrayList<String>();
            chapter.clear();
            list_chapter_url.clear();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Document doc = Jsoup.connect(book_url).get();
//                    Log.d("onClick","d"+doc);
                        Elements btEl = doc.select("dd");;
                        for (Element link  : btEl) {
                            Elements a=link.select("a");
                            String bt = a.attr("href");
                            String bt1 = link.text();
                            list_chapter_url.add("https://www.23txt.com"+bt);
                            chapter.add(bt1);
                        }
                        String paixu=button5.getText().toString();
                        if (paixu.equals("降序")){
                            Collections.reverse(chapter);
                            Collections.reverse(list_chapter_url);
                        }
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter = (new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, chapter));
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner1.setAdapter(adapter);
                                spinner1.setVisibility(View.VISIBLE);
                                button2.setVisibility(View.VISIBLE);
                                button5.setVisibility(View.VISIBLE);
                                tx7.setVisibility(View.VISIBLE);
                            }
                        });
                        if (chapter.size()>0){
                            progressDialog.dismiss();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "书籍异常", Toast.LENGTH_LONG).show();
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                        Log.e("onClick","e"+e);
                    }
                };
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
            final int nunber=spinner1.getSelectedItemPosition();
            chapter_url=list_chapter_url.get(nunber);
            final String paixu=button5.getText().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Document doc = Jsoup.connect(chapter_url).get();
                        Elements btEl = doc.select("#content");;
                        String title = doc.select("h1").text();;
                        btEl.select("br").next().append("\\n");
                        String bt1 = btEl.text();
                        String text = bt1.replace("\\n", "\n       ");
                        if (!text.equals("")){
                            progressDialog.dismiss();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "章节异常", Toast.LENGTH_LONG).show();
                        }
                        Intent intent=new Intent(MainActivity.this,nextActivity.class);
                        intent.putExtra("content", text);
                        intent.putExtra("title", title);
                        intent.putExtra("paixu", paixu);
                        intent.putExtra("nunber", nunber+"");
//                        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
                        intent.putStringArrayListExtra("list_chapter_url", (ArrayList<String>) list_chapter_url);
                        startActivity(intent);
                    }catch(Exception e) {
                        e.printStackTrace();
                        Log.e("onClick","e"+e);
                    }
                }
            }).start();
        }
    }
    private class MyonclickListener3 implements OnClickListener {
        @Override
        public void onClick(View v) {
            String paixu = button5.getText().toString();
            if (paixu.equals("降序")){
                button5.setText("升序");
                Collections.reverse(chapter);
                Collections.reverse(list_chapter_url);
                adapter = (new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, chapter));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(adapter);
            }else if(paixu.equals("升序")){
                button5.setText("降序");
                Collections.reverse(chapter);
                Collections.reverse(list_chapter_url);
                adapter = (new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, chapter));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(adapter);
            }
        }
    }
}