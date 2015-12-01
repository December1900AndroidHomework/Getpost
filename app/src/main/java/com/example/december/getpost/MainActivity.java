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
    MyHandler myHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));
        search = (Button) findViewById(R.id.search);
        edt = (EditText) findViewById(R.id.edt);
        myHandler = new MyHandler();
    }

    class MyHandler extends android.os.Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Map<String, Object>> list;
            try {
                String JSON = msg.getData().getString("JSON");
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
                }
                ListView lv = (ListView) findViewById(R.id.lv);
                simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.listview, new String[]
                        {"name", "id", "created", "signature"}, new int[]{R.id.name, R.id.id, R.id.loc_name, R.id.desc});
                lv.setAdapter(simpleAdapter);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}






