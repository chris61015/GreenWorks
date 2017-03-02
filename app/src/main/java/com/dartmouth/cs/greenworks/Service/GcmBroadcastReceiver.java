package com.dartmouth.cs.greenworks.Service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Varun on 2/18/16.
 */
//XD: starting a service in WakefulBroadcastReceiver will hold and won't let the CPU sleep until
// you finish the work inside service and fire completeWakefulIntent()
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.

        //XD: this works too - startWakefulService(context, intent.setClass(context, GcmIntentService.class));
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}

//XD: check sender id to decide if the msg needs to be propagated to the next receiver (not in this example
// as we have only one receiver) intent.getExtras().get("from").equals (SENDER_ID_OF_YOUR_APP).
// RESULT_OK - propagate; RESULT_CANCEL do not propagate