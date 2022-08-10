package com.chooloo.www.chooloolib.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chooloo.www.chooloolib.dbase.Ad;
import com.chooloo.www.chooloolib.dbase.AdDAO;
import com.chooloo.www.chooloolib.dbase.AdDatabase;
import com.chooloo.www.chooloolib.events.CallEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.TimerTask;

import timber.log.Timber;

public class AdService extends Service {
    MediaPlayer player;
    AdDatabase database;
    Ad[] ads;
    public AdService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        super.onCreate();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Timber.tag("SHYAM").d("registered");
        database=   Room.databaseBuilder(getApplicationContext(), AdDatabase.class,"ad.db").allowMainThreadQueries().build();
        ads= database.adDAO().getAds(false);
        if(ads.length>0){

                File f=new File(ads[0].path);
                this.player=MediaPlayer.create(this, Uri.fromFile(f));



        }else{
            stopSelf();
        }
        return START_STICKY;
    }



    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCallEvent(CallEvent event){
        if(event.message.contentEquals("start")){
            startPlayer();
        }else if (event.message.contentEquals("stop")){
            stopPlayer();
        }

    }

    private void startPlayer(){
        if(this.player!=null){

            this.player.start();

            this.player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(player.isPlaying()){
                        database.adDAO().updatePlayed(true,ads[0].path);
                        if(true){
                            RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
                            SharedPreferences preferences=getSharedPreferences("kotlinsharedpreference",MODE_PRIVATE);
                            String phone= preferences.getString("user_number","empty");
                            if(!phone.contentEquals("empty")){
                                String url="http://159.223.197.192:3000/api/user/updateCampaign/" + ads[0].campaignUid + "/" + phone;
                                StringRequest request=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                database.adDAO().updateUpdated(true,ads[0].path);
                                            }
                                        });

                                queue.add(request);
                                queue.start();
                            }

                        }
                    }


                }
            }, 4000);
        }
    }

    public void stopPlayer(){
        if(this.player!=null&&this.player.isPlaying()){
            this.player.release();
            stopSelf();
        }
    }


    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }
}