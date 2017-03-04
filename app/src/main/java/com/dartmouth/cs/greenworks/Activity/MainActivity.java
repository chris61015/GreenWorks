package com.dartmouth.cs.greenworks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.dartmouth.cs.greenworks.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private static final String NAV_ITEM_ID = "navItemId";
    private static final long DRAWER_CLOSE_DELAY_MS = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.dartmouth.cs.greenworks.R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.dartmouth.cs.greenworks.R.id.map);
        mapFragment.getMapAsync(this);


        mDrawerLayout = (DrawerLayout) findViewById(com.dartmouth.cs.greenworks.R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(com.dartmouth.cs.greenworks.R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(com.dartmouth.cs.greenworks.R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        if (null != savedInstanceState) {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
            navigationView.getMenu().findItem(mNavItemId).setChecked(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, com.dartmouth.cs.greenworks.R.string.open, com.dartmouth.cs.greenworks.R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigate(mNavItemId);

        testBackend();

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

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        item.setChecked(true);
        mNavItemId = item.getItemId();

        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(item.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }
    private void navigate(final int itemId) {

        switch (itemId) {
            case R.id.drawer_item_1:
                Intent intent1 = new Intent(this, MyTreesActivity.class);
                startActivity(intent1);
                break;
            case R.id.drawer_item_2:
                Log.d("DEBUG", "Plant a Tree");
                Intent intent2 = new Intent(this, PlantATreeActivity.class);
                startActivity(intent2);
                break;
            default:
                // ignore
                break;
        }
    }


    public void testBackend() {
        BackendTest newTest = new BackendTest();
        newTest.registerTest(this);
        newTest.addTreeTest(this, R.drawable.apple);
        newTest.getMyTreesTest(this);
        newTest.updateTree(this);

    }
}
