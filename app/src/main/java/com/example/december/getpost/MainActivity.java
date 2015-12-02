package com.example.december.getpost;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class MainActivity extends AppCompatActivity {
    Button search;
    EditText edt;
    SimpleAdapter simpleAdapter;
    static android.os.Handler handler;
    private final String str = "https://api.douban.com/v2/user?q=";

    public static android.os.Handler getHandler() {
        return handler;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));
        search = (Button) findViewById(R.id.search);
        edt = (EditText) findViewById(R.id.edt);

        handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<Map<String, Object>> list;
                if (msg.what == 0 * 101) ;
                try {
                    Bundle b = msg.getData();
                    String JSON = b.getString("JSON");
                    JSONObject obj = new JSONObject(JSON);
                    JSONArray transitListArray = obj.getJSONArray("users");
                    list = new ArrayList<>();
                    for (int i = 0; i < transitListArray.length(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("name", transitListArray.getJSONObject(i).get("name"));
                        map.put("id", transitListArray.getJSONObject(i).get("id"));
                        map.put("created", transitListArray.getJSONObject(i).get("created"));
                        map.put("signature", transitListArray.getJSONObject(i).get("signature"));
                        list.add(map);
                        super.handleMessage(msg);
                    }
                    ListView lv = (ListView) findViewById(R.id.lv);
                    simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.listview, new String[]
                            {"name", "id", "created", "signature"}, new int[]{R.id.name, R.id.id, R.id.loc_name, R.id.desc});
                    lv.setAdapter(simpleAdapter);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Looper.loop();
                StringBuilder sb = new StringBuilder();
                try {
                    URL url = new URL(str);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.connect();
                    BufferedReader reader =  new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null){
                        sb.append(line);
                    }
                    Message msg = new Message();
                    msg.what = 0 * 101;
                    Bundle b = new Bundle();
                    b.putString("JSON",sb.toString());
                    msg.setData(b);
                    MainActivity.getHandler().sendMessage(msg);//向Handler发送消息，更新UI
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
               try {
                   stringBuilder.append(str).append(URLEncoder.encode(edt.getText().toString(),"utf-8"));
                   Message msg = new Message();
                   msg.obj = stringBuilder;
                   MainActivity.getHandler().sendMessage(msg);
               }catch (Exception e){
                   e.printStackTrace();
               }
            }
        });

    }
}








