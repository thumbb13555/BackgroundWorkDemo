package com.jetec.backgroundworkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btThread, btAsync;
        ToggleButton btHandler;

        btThread = findViewById(R.id.button_Thread);
        btHandler = findViewById(R.id.button_Handler);
        btAsync = findViewById(R.id.button_AsyncTask);
        tvResult = findViewById(R.id.textView_Result);

        btThread.setOnClickListener(v -> {
            threadRun();
        });
        btHandler.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                /**執行Handler*/
                handler.post(task);
            }else{
                /**取消Handler*/
                handler.removeCallbacks(task);
                tvResult.setText("結束使用Handler執行緒！");
            }
        });
        btAsync.setOnClickListener(v -> {
            asyncTaskRun();
        });

    }//onCreate

    /**使用Thread做背景執行處理*/
    private void threadRun() {
        new Thread(() -> {
            for (int i = 1; i < 6; i++) {
                int count = i;
                /**有要在UI上面顯示的內容的話，就必須使用runOnUiThread!*/
                runOnUiThread(() -> {
                    tvResult.setText(String.valueOf(count));
                });
                SystemClock.sleep(1000);
            }
            runOnUiThread(() -> {
                tvResult.setText("完成Thread計數！");
            });
        }).start();
    }

    /**使用AsyncTask做背景執行處理*/
    private void asyncTaskRun() {
        CountTask countTask = new CountTask();
        /**送出初始值並觸發*/
        countTask.execute("0");
        /**取得背景執行之類別回傳*/
        countTask.onTimerCount = new CountTask.OnTimerCount() {
            @Override
            public void onCountering(int progress) {
                tvResult.setText(String.valueOf(progress));
            }

            @Override
            public void onCounterFinish(String text) {
                tvResult.setText(text);
            }
        };
    }
    /**為Handler制定Runnable執行緒*/
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            /**在Task中將指令傳送到Handler內部的方法*/
            handler.sendEmptyMessage(1);
            /**每1秒會再重複執行此task*/
            handler.postDelayed(this,  1000);
        }
    };
    /**為Handler制定他要在畫面做的事*/
    @SuppressLint("HandlerLeak")//<-有加不加不影響
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date currentDate = new Date();
                    String displayTime1 = format1.format(currentDate);
                    tvResult.setText(displayTime1);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}