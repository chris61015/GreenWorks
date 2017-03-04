package com.dartmouth.cs.greenworks.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.dartmouth.cs.greenworks.Fragment.MapFragment;
import com.dartmouth.cs.greenworks.Fragment.MyTreesFragment;
import com.dartmouth.cs.greenworks.Fragment.TreesIUpdatedFragment;
import com.dartmouth.cs.greenworks.R;
import com.google.android.gms.maps.GoogleMap;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String TAG = "MAIN ACTIVITY";
    public static final int PERMISSIONS_REQUEST = 1;

    private GoogleMap mMap;

    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private static final String NAV_ITEM_ID = "navItemId";
    private static final long DRAWER_CLOSE_DELAY_MS = 250;

    private final MapFragment mFirstFragment = new MapFragment();
    private final MyTreesFragment mSecondFragment = new MyTreesFragment();
    private final TreesIUpdatedFragment mThirdFragment = new TreesIUpdatedFragment();
  //  private final PlantATreeFragment mFourthFragment = new PlantATreeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(com.dartmouth.cs.greenworks.R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(com.dartmouth.cs.greenworks.R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(com.dartmouth.cs.greenworks.R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        if (null != savedInstanceState) {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        } else {
            mNavItemId = R.id.drawer_item_1;
        }
        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, com.dartmouth.cs.greenworks.R.string.open, com.dartmouth.cs.greenworks.R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigate(mNavItemId);

        checkPermissions();
//        testBackend();

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
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, mFirstFragment)
                        .commit();
                break;
            case R.id.drawer_item_2:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, mSecondFragment).addToBackStack(null)
                        .commit();
                break;
            case R.id.drawer_item_3:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, mThirdFragment).addToBackStack(null)
                        .commit();
                break;
//            case R.id.drawer_item_4:
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.content, mFourthFragment)
//                        .commit();
//                break;
            case R.id.drawer_item_4:
                Intent intent = new Intent(this, PlantATreeActivity.class);
                startActivity(intent);
                break;
            default:
                // ignore
                break;
        }
    }




    /**
     * Checks if user has CAMERA, READ/WRITE external storage
     * and access fine location permissions
     */
    private void checkPermissions2 () {

        boolean permitted = true;

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "No write SD permssion!");
            permitted = false;
        }
        else {
            Log.d(TAG, "Has write SD permssion!");
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "No Read SD permssion!");
            permitted = false;

        }
        else {
            Log.d(TAG, "Has Read SD permssion!");
        }

        if (!permitted) {
            acquirePermssions();
        }

    }

    /**
     * Ask for permission using dialog.
     */
    private void acquirePermssions () {

        Log.d(TAG, "Requesting permssions...");
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Returned from request");
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission Granted!");
                }
        }
        // Check again
//        checkPermissions();
    }

    public void testBackend() {
        BackendTest newTest = new BackendTest();
        newTest.registerTest(this);
        newTest.addTreeTest(this, "1.jpg");
        newTest.addTreeTest(this, "2.jpg");
        newTest.addTreeTest(this, "3.jpg");
        newTest.addTreeTest(this, "4.jpg");
        newTest.addTreeTest(this, "5.jpg");
        newTest.updateTree(this, "U1_1.jpg", 1);
        newTest.updateTree(this, "U1_2.jpg", 1);
        newTest.updateTree(this, "U2_1.jpg", 2);

    }

    // Ask users to give permisson to get fetch data
    private void checkPermissions() {
        // If SDK version is less then 23, it will not check the permission in the runtime
        if (Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ) {
            // TODO: Consider calling
            String[] permissions = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            requestPermissions(permissions, 0);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }
}
