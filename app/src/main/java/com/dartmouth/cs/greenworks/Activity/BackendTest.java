package com.dartmouth.cs.greenworks.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dartmouth.cs.greenworks.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Xiaolei on 2/27/17.
 */

public class BackendTest {

    // Server stuff
//    public static String SERVER_ADDR = "https://lateral-avatar-160118.appspot.com";
    public static String SERVER_ADDR = "http://127.0.0.1:8080";


    public void registerTest(Context context) {
        new GcmRegistrationAsyncTask(context).execute();
    }

    public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
        private Registration regService = null;
        private GoogleCloudMessaging gcm;
        private Context context;

        private static final String SENDER_ID = "643884535238";

        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (regService == null) {
                Registration.Builder builder =
                        new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer
                        // only for local testing,
                        // otherwise they can be skipped
                        .setRootUrl(SERVER_ADDR+"/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end of optional local run code

                //XD: newCompatibleTransport - returns a new thread-safe HTTP transport
                // instance that is compatible with Android SDKs prior to * Gingerbread.
                //XD: use this builder instead if you deploy your backend to the cloud
                // (e.g. https://abstract-arc-123122.appspot.com)
//                Registration.Builder builder =
//                        new Registration.Builder(AndroidHttp.newCompatibleTransport(),
//                                new AndroidJsonFactory(), null)
//                                .setRootUrl(SERVER_ADDR +"/_ah/api/");
                regService = builder.build();
            }

            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                String regId = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regId;

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                regService.register(regId).execute();

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        }
    }

}
