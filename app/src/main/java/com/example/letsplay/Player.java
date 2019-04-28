package com.example.letsplay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity {
    SeekBar mSeekBar;
    Button btnPlay;
    Button btnNext;
    Button btnPrev;
    TextView songTitle;
    ArrayList<File> allSongs;
    Thread updateSeekBar;
    static MediaPlayer mMediaPlayer;
    int position;
    TextView curTime;
    TextView totTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mSeekBar = findViewById(R.id.mSeekBar);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnPlay = findViewById(R.id.btnPlay);
        songTitle = findViewById(R.id.songTitle);
        curTime = findViewById(R.id.curTime);
        totTime = findViewById(R.id.totalTime);




        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        Intent playerData = getIntent();
        Bundle bundle = playerData.getExtras();

        allSongs = (ArrayList) bundle.getParcelableArrayList("songs");

        String sname = allSongs.get(position).getName();
        String songName = playerData.getStringExtra("songName");
        songTitle.setText(songName);

        position = bundle.getInt("position",0);

        Uri songResourceUri = Uri.parse(allSongs.get(position).toString());

        mMediaPlayer = MediaPlayer.create(getApplicationContext(),songResourceUri);
//        mMediaPlayer.start();
//        mSeekBar.setMax(mMediaPlayer.getDuration());

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                String totalTime = createTimeLabel(mMediaPlayer.getDuration());
                totTime.setText(totalTime);
                mSeekBar.setMax(mMediaPlayer.getDuration());
                mMediaPlayer.start();

            }
        });



        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    mMediaPlayer.seekTo(progress);
                    mSeekBar.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null) {
                    try {
                        Log.i("Thread ", "Thread Called");
                        // create new message to send to handler
                        Message msg = new Message();
                        msg.what = mMediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();





    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("handler ", "handler called");
            int current_position = msg.what;
            mSeekBar.setProgress(current_position);
            String cTime = createTimeLabel(current_position);
            curTime.setText(cTime);
        }
    };


    public String createTimeLabel(int duration){
        String timeLabel = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        timeLabel += min +":";
        if(sec < 10 ) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;



    }
}
