package com.dartmouth.cs.greenworks.Service;

/**
 * Created by chris61015 on 3/4/17.
 */

public class TrackingService {

    // A request to connect to Location Services
//    private LocationRequest mLocationRequest;
//
//    // Stores the current instantiation of the location client in this object
//    private GoogleApiClient mGoogleApiClient;
//
//    // Set up binder for the TrackingService using IBinder
//    private final IBinder binder = new TrackingServiceBinder();
//
//    private NotificationManager mNotificationManager;
//
//    // service started flag
//    private boolean mIsStarted;
//
//    // activity's messenger
//    private Messenger mClientMessenger;
//    // messenger object that is used to receive messages from the activity
//    private final Messenger mMessenger = new Messenger(
//            new IncomingMessageHandler());
//
//    /**
//     * Handle incoming messages from MainActivity
//     */
//    @SuppressLint("HandlerLeak")
//    private class IncomingMessageHandler extends Handler { // Handler of
//        // incoming messages
//        // from clients.
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case ServiceMessageType.MSG_REG_CLIENT:
//                    // get activity's messenger
//                    mClientMessenger = msg.replyTo;
//                    // send exercise entry's reference to the activity
//                    sendExerceEntryToActivity();
//                    break;
//                case ServiceMessageType.MSG_UNREG_CLIENT:
//                    // set mClientMessenger to null when activity unregister itself
//                    mClientMessenger = null;
//                    break;
//                default:
//                    super.handleMessage(msg);
//            }
//        }
//    }
//
//    // set up the MyRunsBinder
//    public class TrackingServiceBinder extends Binder {
//        public ExerciseEntry getExerciseEntry() {
//            return mEntry;
//        }
//
//        TrackingService getService() {
//            return TrackingService.this;
//        }
//
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mIsStarted = false;
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        // return messenger's binder
//        return mMessenger.getBinder();
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//}
}
