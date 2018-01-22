package com.hsap.customcontrol;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hsap.customcontrol.ui.HorizontalScrollView;

import java.util.ArrayList;

public class Demo1Activity extends AppCompatActivity {

    private static final String TAG = "Demo1Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        HorizontalScrollView hsv_demo1 = findViewById(R.id.hsv_demo1);
        LayoutInflater inflater = getLayoutInflater();
        int screentWidth= MyUtils.getScreenMetrics(this).widthPixels;
        int screentHeight= MyUtils.getScreenMetrics(this).heightPixels;
        for (int i = 0; i <3 ; i++) {
            Log.e(TAG, "onCreate: "+i );
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.content_layout, hsv_demo1, false);
            layout.getLayoutParams().width=screentWidth;
            TextView title = layout.findViewById(R.id.title);
            title.setText("page"+(i+1));
            layout.setBackgroundColor(Color.rgb(255 / (i + 1), 255 / (i + 1), 0));
            createList(layout);
            hsv_demo1.addView(layout);
        }
    }

    private void createList(ViewGroup layout) {
        ListView listView = (ListView) layout.findViewById(R.id.list);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i <50; i++) {
             list.add("name"+i);
        }
        Log.e(TAG, "createList: " );
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.content_list_item, R.id.name, list);
        listView.setAdapter(adapter);

    }
}
