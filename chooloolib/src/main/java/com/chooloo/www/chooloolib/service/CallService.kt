package com.chooloo.www.chooloolib.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.telecom.Call.Callback
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.dbase.Ad
import com.chooloo.www.chooloolib.dbase.AdDAO
import com.chooloo.www.chooloolib.dbase.AdDatabase
import com.chooloo.www.chooloolib.events.CallEvent
import com.chooloo.www.chooloolib.interactor.callaudio.CallAudiosInteractor
import com.chooloo.www.chooloolib.interactor.calls.CallsInteractor
import com.chooloo.www.chooloolib.model.Call
import com.chooloo.www.chooloolib.notification.CallNotification
import com.chooloo.www.chooloolib.repository.calls.CallsRepository
import com.chooloo.www.chooloolib.ui.call.CallActivity

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

@SuppressLint("NewApi")
@AndroidEntryPoint
class CallService : InCallService() {
    @Inject lateinit var callAudios: CallAudiosInteractor
    @Inject lateinit var callsRepository: CallsRepository
    @Inject lateinit var callsInteractor: CallsInteractor
    @Inject lateinit var callNotification: CallNotification
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    private var isPlayed:Boolean = false


    private val sharedPrefFile = "kotlinsharedpreference"
//    private lateinit var db:RoomDatabase
//    private lateinit var adDAO: AdDAO
//    private lateinit var  ads:List<Ad>

    val calls = MutableLiveData<List<Call>>()
    private val callListener = object : Callback() {
        override fun onStateChanged(call: android.telecom.Call?, state: Int) {
            super.onStateChanged(call, state)
            if (state == android.telecom.Call.STATE_DIALING) {
               Toast.makeText(applicationContext,"dialing",Toast.LENGTH_LONG).show();


                EventBus.getDefault().post(CallEvent("start"));




//                runBlocking {
//                    launch {
//
//                            val db= Room.databaseBuilder(applicationContext,AdDatabase::class.java,"ad.db").allowMainThreadQueries().build();
//                            val adDao=db.adDAO();
//                            val ad=adDao.getAds(false)
//
//
//                            if (ad.isNotEmpty()) {
//
//
//                                val file = File(ad[0].path);
//
//                                if (file.exists()) {
//
//                                    mediaPlayer = MediaPlayer.create(applicationContext, Uri.fromFile(file));
//
//
//
//
//                                    mediaPlayer.setOnCompletionListener {
//
//                                    }
//                                    val timer = object : CountDownTimer(4000, 1000) {
//                                        override fun onTick(millisUntilFinished: Long) {
//                                            // do something
//                                            Log.d("SHYAM", "tick");
//                                        }
//
//                                        override fun onFinish() {
//                                            // do something
//                                            if (mediaPlayer.isPlaying && ad.isNotEmpty()) {
//                                                adDao.updatePlayed(true, ad[0].path);
//                                                if (isOnline(applicationContext)) {
//                                                    val queue = Volley.newRequestQueue(applicationContext)
//                                                    val sharedPreferences: SharedPreferences =
//                                                        getSharedPreferences(
//                                                            sharedPrefFile,
//                                                            Context.MODE_PRIVATE
//                                                        )
//                                                    // val editor: SharedPreferences.Editor =  sharedPreferences.edit()
//                                                    val phone =
//                                                        sharedPreferences.getString("user_number", "empty");
//
//
//                                                    // editor.apply()
//                                                    //val phone= Firebase.auth.currentUser?.phoneNumber;
//                                                    Toast.makeText(
//                                                        applicationContext,
//                                                        phone,
//                                                        Toast.LENGTH_LONG
//                                                    )
//                                                        .show();
//                                                    Toast.makeText(
//                                                        applicationContext,
//                                                        ad[0].path,
//                                                        Toast.LENGTH_LONG
//                                                    ).show();
//                                                    if (phone != "empty") {
//                                                        val url =
//                                                            "http://159.223.197.192:3000/api/user/updateCampaign/" + ad[0].campaignUid + "/" + phone;
//
//// Request a string response from the provided URL.
//                                                        val stringRequest = StringRequest(
//                                                            Request.Method.GET, url,
//                                                            { response ->
//                                                                // Display the first 500 characters of the response string.
//                                                                Toast.makeText(
//                                                                    applicationContext,
//                                                                    response,
//                                                                    Toast.LENGTH_LONG
//                                                                ).show();
//                                                                adDao.updateUpdated(true, ad[0].path);
//
//                                                            },
//                                                            {
//                                                                Toast.makeText(
//                                                                    applicationContext,
//                                                                    it.localizedMessage,
//                                                                    Toast.LENGTH_LONG
//                                                                ).show();
//                                                            })
//
//// Add the request to the RequestQueue.
//                                                        queue.add(stringRequest)
//                                                        queue.start();
//                                                    }
//
//                                                }
//                                            }
//
//                                        }
//                                    }
//                                    timer.start()
//
////                mediaPlayer.setAudioStreamType(AudioManager.MODE_IN_CALL);
//                                    mediaPlayer.start()
//                                    isPlayed = true;
//                                }
//                            }
//
//                    }
//                }


             //   Toast.makeText(applicationContext,"media playing",Toast.LENGTH_SHORT).show()
            }
            if (state!=android.telecom.Call.STATE_DIALING) {
                EventBus.getDefault().post(CallEvent("stop"));
            }
//            if (state == android.telecom.Call.STATE_ACTIVE) {
//
//            } else if (state == android.telecom.Call.STATE_DISCONNECTED || state == android.telecom.Call.STATE_DISCONNECTING) {
//
//            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        callNotification.attach()
    }

    override fun onDestroy() {
        callNotification.detach()
        callNotification.cancel()
        super.onDestroy()
    }

    override fun onCallAdded(telecomCall: android.telecom.Call) {
        super.onCallAdded(telecomCall)
        addCall(Call(telecomCall))
        telecomCall.registerCallback(callListener)

        Toast.makeText(this@CallService,"Call Added",Toast.LENGTH_LONG).show();
        callsInteractor.entryAddCall(Call(telecomCall))
        if (!sIsActivityActive) {
            startCallActivity()
        }
    }

    override fun onCallRemoved(telecomCall: android.telecom.Call) {
        super.onCallRemoved(telecomCall)
        removeCall(Call(telecomCall))
        callsInteractor.getCallByTelecomCall(telecomCall)
            ?.let(callsInteractor::entryRemoveCall)
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState) {
        super.onCallAudioStateChanged(audioState)
        Log.d("SHYAM", audioState.toString())
        callAudios.entryCallAudioStateChanged(callAudioState)
    }

    private fun startCallActivity() {

        val intent = Intent(this@CallService, CallActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun addCall(call: Call) {
        val list = calls.value?.toMutableList()
        list?.add(call)
        println("Call added");
        Log.d("SHYAM","added");
        calls.value = list
    }

    private fun removeCall(call: Call) {
        val list = calls.value?.toMutableList()
        list?.remove(call)
        calls.value = list
    }
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    companion object {
        var sIsActivityActive = false
        var sInstance: CallService? = null


    }

}