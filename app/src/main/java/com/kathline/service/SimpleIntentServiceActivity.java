package com.kathline.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SimpleIntentServiceActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_service);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        TextView textView=findViewById(R.id.stop);
        textView.setText(Html.fromHtml(
                    "<span style=\"color:yellow;font-size:50px;line-height:100px;padding:30px;\">" +
                            "重显著超出-444了相同年龄女孩的第重显著超出了相同年龄女孩的第重显著超出了相同年龄女孩的第重显著超出了相同" +
                            "年龄女孩的第重显著超出了相同年龄女孩的第111111111111111111111111111</span>"
              ));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.start == id) {
            Intent intent = new Intent(this, SimpleIntentService.class);
            startService(intent);
        } else if (R.id.stop == id) {
            Intent intent = new Intent(this, SimpleIntentService.class);
            stopService(intent);
        }
    }
}