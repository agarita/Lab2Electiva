package com.example.reproductormusica;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Integer numCancion=0;
    ListView listView;
    TextView nombre;
    Button pausePlayBtn;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    boolean pausado = false;
    ArrayList<MediaPlayer> playlist;
    ArrayList<String> nomPlaylist;

    private void hacerLista(){
        playlist = new ArrayList<MediaPlayer>();
        nomPlaylist = new ArrayList<String>();
        mediaPlayer = MediaPlayer.create(this, R.raw.and_so_it_begins);
        playlist.add(mediaPlayer);
        nomPlaylist.add("And so it Begins");
        playlist.add(MediaPlayer.create(this, R.raw.nes_pictionary_01_title));
        nomPlaylist.add("NES Pictionary 01 Title");
        playlist.add(MediaPlayer.create(this, R.raw.to_the_moon));
        nomPlaylist.add("To The Moon");
        playlist.add(MediaPlayer.create(this, R.raw.the_jungle));
        nomPlaylist.add("The Jungle");
        playlist.add(MediaPlayer.create(this, R.raw.the_final_run));
        nomPlaylist.add("The Final Run");
        playlist.add(MediaPlayer.create(this, R.raw.the_beach));
        nomPlaylist.add("The Beach");
        playlist.add(MediaPlayer.create(this, R.raw.splash));
        nomPlaylist.add("Splash");
        playlist.add(MediaPlayer.create(this, R.raw.new_world));
        nomPlaylist.add("New World");
        playlist.add(MediaPlayer.create(this, R.raw.hazard));
        nomPlaylist.add("Hazard");
        playlist.add(MediaPlayer.create(this, R.raw.blue_caves));
        nomPlaylist.add("Blue Caves");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomPlaylist);
        listView.setAdapter(adapter);
    }

    public void playStopClick(View view){
        if(pausado){
            mediaPlayer.start();
            pausePlayBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
            Log.i("INFO", String.format("Canción #%d: %s", numCancion+1, nomPlaylist.get(numCancion)));
        }
        else {
            mediaPlayer.pause();
            pausePlayBtn.setBackgroundResource(android.R.drawable.ic_media_play);
        }
        pausado = !pausado;
    }

    public void lastSongClick(View view){
        if(numCancion>0) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer = playlist.get(--numCancion);
            mediaPlayer.start();
            nombre.setText(nomPlaylist.get(numCancion));
        }
        else {
            mediaPlayer.stop();
            mediaPlayer.seekTo(0);
            mediaPlayer = playlist.get(9);
            mediaPlayer.start();
            numCancion = 9;
            nombre.setText(nomPlaylist.get(9));
        }
        Log.i("INFO", String.format("Canción #%d: %s", numCancion+1, nomPlaylist.get(numCancion)));
    }

    public void nextSongClick(View view){
        if(numCancion<9) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer = playlist.get(++numCancion);
            mediaPlayer.start();
            nombre.setText(nomPlaylist.get(numCancion));
        }
        else {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer = playlist.get(0);
            mediaPlayer.start();
            numCancion = 0;
            nombre.setText(nomPlaylist.get(0));
        }
        Log.i("INFO", String.format("Canción #%d: %s", numCancion+1, nomPlaylist.get(numCancion)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pausePlayBtn = findViewById(R.id.lastSongBtn);
        nombre = findViewById(R.id.cancionTxt);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                mediaPlayer = playlist.get(position);
                nombre.setText(nomPlaylist.get(position));
                mediaPlayer.start();
                numCancion = position;
                Log.i("INFO", String.format("Canción #%d: %s", numCancion+1, nomPlaylist.get(numCancion)));
            }
        });

        hacerLista();
        nombre.setText(nomPlaylist.get(numCancion));
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        mediaPlayer.start();
        mediaPlayer.pause();

        SeekBar volumeControl = findViewById(R.id.volumenSeek);
        volumeControl.setMax(maxVol);
        volumeControl.setProgress(currentVol);

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final SeekBar avanceBar = findViewById(R.id.avanceSeek);

        avanceBar.setMax(mediaPlayer.getDuration());
        avanceBar.setProgress(mediaPlayer.getCurrentPosition());

        avanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                avanceBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }, 0, 1000);

    }
}
