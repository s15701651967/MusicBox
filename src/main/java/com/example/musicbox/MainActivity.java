package com.example.musicbox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radio.R;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    int []song_id={R.raw.song1,R.raw.song2,R.raw.song3};
    String[]song_name={"小城故事 — 邓丽君","Bad day - Danier Powter","Hozier-Take Me to Church"};
    int songs=0;
    TextView curTime,totalTime,theSong;
    Button play,pause,stop,next,previous;
    static int num=0;
    SeekBar seekBar;
    int p=0;
    Spinner spinner;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer=new MediaPlayer();

        play=findViewById(R.id.play);
        pause=findViewById(R.id.pause);
        stop=findViewById(R.id.stop);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        seekBar=findViewById(R.id.mSeekbar);


        curTime=findViewById(R.id.curTime);
        totalTime=findViewById(R.id.totalTime);
        theSong=findViewById(R.id.songName);
        test(num);
        initview();
        initSong();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                test(num);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(num==2)num=0;
                else num++;
                test(num);
                int total=mediaPlayer.getDuration()/1000;
                int curl=mediaPlayer.getCurrentPosition()/1000;
                curTime.setText(calculateTime(curl));
                totalTime.setText(calculateTime(total));
                mediaPlayer.start();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(num==0)num=2;
                else num--;
                test(num);
                int total=mediaPlayer.getDuration()/1000;
                int curl=mediaPlayer.getCurrentPosition()/1000;
                curTime.setText(calculateTime(curl));
                totalTime.setText(calculateTime(total));
                mediaPlayer.start();
            }

        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                //int total = mediaPlayer.getDuration() / 1000;
                int curl = mediaPlayer.getCurrentPosition();
                while(true){
                    //otal = mediaPlayer.getDuration() / 1000;
                    curl = mediaPlayer.getCurrentPosition() / 10;
                    curTime.setText(calculateTime(curl/100));
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();




    }

    public void initSong(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,song_name);
        ListView lv_1 = findViewById(R.id.listview);
        lv_1.setAdapter(adapter);
    }
    public void test(int i){
        theSong = findViewById(R.id.songName);
        theSong.setText(song_name[i]);
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(this, song_id[i]);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                option();
            }
        });
    }

    public void option(){
        Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_SHORT).show();

    }

    public void initview(){
        int total = mediaPlayer.getDuration() / 1000;
        int curl = mediaPlayer.getCurrentPosition() / 1000;
        curTime.setText(calculateTime(curl));
        totalTime.setText(calculateTime(total));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int total = mediaPlayer.getDuration() / 1000;//获取音乐总时长
                int curl = mediaPlayer.getCurrentPosition() / 1000;//获取当前播放的位置
                curTime.setText(calculateTime(curl));//开始时间
                totalTime.setText(calculateTime(total));//总时长
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(mediaPlayer.getDuration()*seekBar.getProgress()/100);//在当前位置播放
                curTime.setText(calculateTime(mediaPlayer.getCurrentPosition() / 1000));
            }
        });
    }

    public String calculateTime(int time){
        int minute;
        int second;
        if(time > 60){
            minute = time / 60;
            second = time % 60;
            //判断秒
            if(second >= 0 && second < 10){
                return "0"+minute+":"+"0"+second;
            }else {
                return "0"+minute+":"+second;
            }
        }else if(time < 60){
            second = time;
            if(second >= 0 && second < 10){
                return "00:"+"0"+second;
            }else {
                return "00:"+ second;
            }
        }else{
            return "01:00";
        }
    }




    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}