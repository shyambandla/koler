package com.chooloo.www.chooloolib;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chooloo.www.chooloolib.dbase.Ad;
import com.chooloo.www.chooloolib.dbase.AdDAO;
import com.chooloo.www.chooloolib.dbase.AdDatabase;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

public class DownLoaderService extends Service {
    private Fetch fetch;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    public DownLoaderService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            createNotificationChannel();
        }


       try{
           startTimer();
       }catch(Exception e){

       }


        return super.onStartCommand(intent, flags, startId);
    }



    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer() {
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        DownloadFiles();

                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 1000*60*60);

    }

    public void DownloadFiles(){


        Toast.makeText(getApplicationContext(),"datab",Toast.LENGTH_LONG).show();
        AdDatabase db= Room.databaseBuilder(this,
                AdDatabase.class,"ad.db").allowMainThreadQueries().build();
        AdDAO adDAO=db.adDAO();


        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(3)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url2 = "http://159.223.197.192:3000/api/user/getCampaigns";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url2,
                response -> {
                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    // Display the first 500 characters of the response string.
                    Timber.tag("SHYAM").d(response);
                    try {
                        JSONObject object2=new JSONObject(response);
                        JSONArray object=object2.getJSONArray("docs");
                        for(int i=0;i<object.length();i++){
                            JSONObject object1=object.getJSONObject(i);
                           String filename= object1.getString("file");
                           String campaignUid=object1.getString("uid");
                          Toast.makeText(getApplicationContext(),filename,Toast.LENGTH_LONG).show();
                            String url = "https://shyambandla.com/api/"+filename;

                            //File path=Environment.getExternalStorageDirectory();
                            File path = new File(getFilesDir(), filename);
                            Toast.makeText(this, path.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                            String file = path.getAbsolutePath();

                            final Request request = new Request(url, file);
                            request.setPriority(Priority.HIGH);
                            request.setNetworkType(NetworkType.ALL);
                            //request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG");

                                fetch.enqueue(request, updatedRequest -> {
                                    Toast.makeText(getApplicationContext(), "enqued", Toast.LENGTH_LONG).show();
                                    //Request was successfully enqueued for download.



                                        Ad ad = new Ad(campaignUid, false, false, false, file);
                                        adDAO.insert(ad);


                                }, error -> {
                                   Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                                    //An error occurred enqueuing the request.
                                });

                        }



                        FetchListener fetchListener = new FetchListener() {
                            @Override
                            public void onWaitingNetwork(@NonNull Download download) {

                            }

                            @Override
                            public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> list, int i) {

                            }

                            @Override
                            public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int i) {

                            }

                            @Override
                            public void onAdded(@NonNull Download download) {

                            }

                            @Override
                            public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {

                                    Toast.makeText(getApplicationContext(),download.getFile(),Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onCompleted(@NotNull Download download) {
                                Toast.makeText(getApplicationContext(),"file downloaded"+download.getFile(),Toast.LENGTH_LONG).show();
                                Timber.tag("SHYAM").d(download.getFile());
                                adDAO.updateDownloaded(true,download.getFile());

                            }

                            @Override
                            public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {

                            }

                            @Override
                            public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {

                                int progress = download.getProgress();
                                Log.d("SHYAM", String.valueOf(progress));
                            }

                            @Override
                            public void onPaused(@NotNull Download download) {

                            }

                            @Override
                            public void onResumed(@NotNull Download download) {

                            }

                            @Override
                            public void onCancelled(@NotNull Download download) {

                            }

                            @Override
                            public void onRemoved(@NotNull Download download) {

                            }

                            @Override
                            public void onDeleted(@NotNull Download download) {

                            }
                        };

                        fetch.addListener(fetchListener);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // textView.setText("Response is: " + response.substring(0,500));
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SHYAM",error.getMessage());
               // textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();





    }


    public void createNotificationChannel(){
        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.drawable.icon_full_144);

        startForeground(1001, notification.build());
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}