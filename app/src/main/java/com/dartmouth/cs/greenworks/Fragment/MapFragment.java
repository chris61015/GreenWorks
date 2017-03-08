package com.dartmouth.cs.greenworks.Fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dartmouth.cs.greenworks.Activity.BackendTest;
import com.dartmouth.cs.greenworks.Activity.TreeDetailActivity;
import com.dartmouth.cs.greenworks.Model.TreeEntry;
import com.dartmouth.cs.greenworks.R;
import com.dartmouth.cs.greenworks.Utils.ActivityEntriesAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.dartmouth.cs.greenworks.Activity.BackendTest.GET_TREES_AROUND_ME;
import static com.dartmouth.cs.greenworks.Fragment.AllTreesFragment.ENTRY;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private static View view;
    private GoogleApiClient mGoogleApiClient;
    private static Location mLastLocation;
    private static Location currentLocation;
    private LocationRequest locationRequest;

    private Marker currentMarker, itemMarker;
    private ArrayList<TreeEntry> mTreeList;
    private ActivityEntriesAdapter mAdapter;

    private boolean isInitial = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        configGoogleApiClient();
        configLocationRequest();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        mTreeList = new ArrayList<TreeEntry>();
        mAdapter = new ActivityEntriesAdapter(getActivity());
    }

    private synchronized void configGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // Build Location Request Object
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // Set the interval for reading the location information to one second（1000ms）
        locationRequest.setInterval(1000);
        // Set the fastest interval for reading the location information to one second（1000ms）
        locationRequest.setFastestInterval(1000);
        // Set priority to read high-precision location information（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().
                    findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    void getTreesAroundMe(){
        if (mLastLocation != null) {
            new BackendTest.DatastoreTask(mAdapter, mTreeList).execute(GET_TREES_AROUND_ME, 10000, currentLocation.getLongitude(), currentLocation.getLatitude());
//            plotTreesOnMap();
            runThread();
        } else if (currentLocation != null){
            new BackendTest.DatastoreTask(mAdapter, mTreeList).execute(GET_TREES_AROUND_ME, 10000, currentLocation.getLongitude(), currentLocation.getLatitude());
//            plotTreesOnMap();
            runThread();
        } else {
            Log.d("ERROR!!!!!!!!", "It is impossible to come here!!!!!!!!!!!!");
        }
    }

    private void runThread() {
        new Thread() {
            public void run() {

                while (!BackendTest.isFinish) { }

                if (getActivity()!=null) {

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            plotTreesOnMap();
                        }
                    });
                }
            }

        }.start();
    }

     void plotTreesOnMap(){
        if (mTreeList != null){
            currentMarker = null;
            mMap.clear();
            synchronized (this){
                int size = mTreeList.size();

                for (TreeEntry entry:mTreeList){
                    Marker marker = mMap.addMarker(new MarkerOptions().
                            position(entry.location).
                            icon(BitmapDescriptorFactory.
                            defaultMarker(HUE_GREEN)));
                    marker.setTag(entry);
                    Log.d("DEBUG", entry.toString());
                }
            }
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                TreeEntry entry = (TreeEntry) arg0.getTag();
                Intent intent = new Intent(getActivity(), TreeDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(ENTRY,entry);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }

        });
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected(@Nullable Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Google Services Connection Failed
        // ConnectionResult The parameter is a connection failure message
        int errorCode = connectionResult.getErrorCode();

        // Google Play services are not installed on your device
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(getActivity(), R.string.google_play_service_missing,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Location Changed
        // Location parameter is the location now
        currentLocation = location;
        LatLng latLng = new LatLng(
                location.getLatitude(), location.getLongitude());

        if (BackendTest.isFinish && (mLastLocation != null || currentLocation != null)){
            BackendTest.isFinish = false;
            getTreesAroundMe();
        }

        // Set the mark of the current location
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            currentMarker.setZIndex(1.0f);
        }
        else {
            currentMarker.setPosition(latLng);
        }
        // Move the map to current location
        moveMap(latLng);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Green Works");
        // Connect to Google API Client
        if (!mGoogleApiClient.isConnected() && currentMarker != null) {
            mGoogleApiClient.connect();
        }
//        isInitial = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove location request
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove the connection between Client and Google API
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    // Move map to the location according to parameter
    private void moveMap(LatLng place) {
        // Create a location object for the map camera
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(17)
                        .build();

        // Use the effect of the animation to move the map
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static Location getLocation() throws Exception {
        if (currentLocation != null) return currentLocation;
        else if (mLastLocation !=null) return mLastLocation;
        else throw new Exception("Not Such Location");
    }
}



/* Q1: Whether We should have a activity of fragment in planting a tree?
*
*
*
* */