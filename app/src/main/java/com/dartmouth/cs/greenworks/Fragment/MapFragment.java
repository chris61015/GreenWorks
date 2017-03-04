package com.dartmouth.cs.greenworks.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dartmouth.cs.greenworks.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_map, container, false);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
//                .findFragmentById(R.id.map);
//
//        mapFragment.getMapAsync(this);
//
//        return view;
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
//                .findFragmentById(R.id.map);
//        if (mapFragment != null)
//            getFragmentManager().beginTransaction().remove(mapFragment).commit();
//    }

}


/* Q1: Whether We should have a activity of fragment in planting a tree?
*
*
*
* */