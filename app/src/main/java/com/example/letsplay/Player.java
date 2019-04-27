package com.example.letsplay;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        mSeekBar = findViewById(R.id.seekBar);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnPlay = findViewById(R.id.btnPlay);
        songTitle = findViewById(R.id.songTitle);
        curTime = findViewById(R.id.curTime);
        totTime = findViewById(R.id.totalTime);



        updateSeekBar = new Thread(){
            @Override
            public void run() {

                int totalSongDuration = mMediaPlayer.getDuration();
                int currentSongPosition = 0;

                while(currentSongPosition < totalSongDuration){
                    try{
                        sleep(500);
                        currentSongPosition = mMediaPlayer.getCurrentPosition();
                        mSeekBar.setProgress(currentSongPosition);

                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        };

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



    }


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
