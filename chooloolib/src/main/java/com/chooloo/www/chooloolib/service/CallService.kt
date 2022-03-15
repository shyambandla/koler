package com.chooloo.www.chooloolib.service

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.telecom.Call.Callback
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.interactor.callaudio.CallAudiosInteractor
import com.chooloo.www.chooloolib.interactor.calls.CallsInteractor
import com.chooloo.www.chooloolib.model.Call
import com.chooloo.www.chooloolib.notification.CallNotification
import com.chooloo.www.chooloolib.repository.calls.CallsRepository
import com.chooloo.www.chooloolib.ui.call.CallActivity
import dagger.hilt.android.AndroidEntryPoint
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

    val calls = MutableLiveData<List<Call>>()
    private val callListener = object : Callback() {
        override fun onStateChanged(call: android.telecom.Call?, state: Int) {
            super.onStateChanged(call, state)
            if (state == android.telecom.Call.STATE_DIALING) {
               Toast.makeText(applicationContext,"dialing",Toast.LENGTH_LONG).show();
                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.ring)
                mediaPlayer.setOnCompletionListener {
                    Toast.makeText(applicationContext,"ad completed",Toast.LENGTH_LONG).show();
                }
//                mediaPlayer.setAudioStreamType(AudioManager.MODE_IN_CALL);
                mediaPlayer.start()
                isPlayed=true;
                Toast.makeText(applicationContext,"media playing",Toast.LENGTH_SHORT).show()
            }
            if (state != android.telecom.Call.STATE_DIALING) {
            if(isPlayed){
                mediaPlayer.stop();
            }
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

        Toast.makeText(this,"Call Added",Toast.LENGTH_LONG).show();
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

        val intent = Intent(this, CallActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
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


    companion object {
        var sIsActivityActive = false
        var sInstance: CallService? = null
    }
}