package com.example.brian.projectkitten;

import android.app.Activity;
import android.os.SystemClock;
import android.os.Handler;

/**
 * Created by Keaton on 3/25/2017.
 */

public class ResponsePoller
{
    public static ResponsePoller singleton;
    public static final Object threadLock = new Object();

    public static ResponsePoller getInstance()
    {
        if(singleton == null)
            singleton = new ResponsePoller();
        return singleton;
    }

    public MessageActivity activity;
    public void setActivity( MessageActivity activity )
    {
        this.activity = activity;
    }

    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            SendHttp resp = activity.tradeMessages("", false);
            if(!resp.receivedMessage.equals("nopending"))
                activity.makeMessage(resp.receivedMessage, false, null, activity);
            mHandler.postDelayed(this, 3000);
        }
    };

    public ResponsePoller()
    {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    public void destroy() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        singleton = null;
    }

}
