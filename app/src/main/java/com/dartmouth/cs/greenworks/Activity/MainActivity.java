package com.dartmouth.cs.greenworks.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import android.widget.Toast;

import com.dartmouth.cs.greenworks.Fragment.AllTreesFragment;
import com.dartmouth.cs.greenworks.Fragment.MapFragment;
import com.dartmouth.cs.greenworks.Fragment.MyTreesFragment;
import com.dartmouth.cs.greenworks.Fragment.TreesIUpdatedFragment;
import com.dartmouth.cs.greenworks.R;
import com.google.android.gms.maps.GoogleMap;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String myRegId = "";
    public static final String TAG = "MAIN ACTIVITY";
    public static final int PERMISSIONS_REQUEST = 1;

    private GoogleMap mMap;

    private final Handler mDrawerActionHandler = new Handler();
    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private static final String NAV_ITEM_ID = "navItemId";
    private static final long DRAWER_CLOSE_DELAY_MS = 250;

    private final MapFragment mFirstFragment = new MapFragment();
    private final MyTreesFragment mSecondFragment = new MyTreesFragment();
    private final TreesIUpdatedFragment mThirdFragment = new TreesIUpdatedFragment();
    private final AllTreesFragment mFifthFragment = new AllTreesFragment();
    private NavigationView mNavigationView;
    public  static boolean mIsPermitted = true;

    // Main Activity Start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        // Navigation view and Drawer Layout
        mDrawerLayout = (DrawerLayout) findViewById(com.dartmouth.cs.greenworks.R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(com.dartmouth.cs.greenworks.R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationView = (NavigationView) findViewById(com.dartmouth.cs.greenworks.R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(this);

        if (null != savedInstanceState) {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        } else {
            mNavItemId = R.id.drawer_item_1;
        }
        Log.d("DEBUG",mNavigationView.getMenu().findItem(mNavItemId).toString());
        mNavigationView.getMenu().findItem(mNavItemId).setChecked(true);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, com.dartmouth.cs.greenworks.R.string.open, com.dartmouth.cs.greenworks.R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        navigate(mNavItemId);
        // Test Backend
        testBackend();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
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

    // Navigation view choose
    private void navigate(final int itemId) {
        switch (itemId) {
            case R.id.drawer_item_1:
                this.setTitle("Green Works");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, mFirstFragment).addToBackStack(null)
                        .commit();
                break;
            case R.id.drawer_item_2:
                this.setTitle("Trees I Planted");

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, mSecondFragment).addToBackStack(null)
                        .commit();
                break;
            case R.id.drawer_item_3:
                this.setTitle("Trees I Updated");

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
            case R.id.drawer_item_5:
                this.setTitle("All Trees");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, mFifthFragment).addToBackStack(null)
                        .commit();
                break;
            default:
                // ignore
                break;
        }
    }


    // testBackend Method
    public void testBackend() {
        BackendTest newTest = new BackendTest();
        if(!newTest.registerTest(this)) {
            Toast.makeText(this, "Registration failed. Please try later",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Registration failed. Please try later");
        }

//        newTest.addTreeTest(this, "1.jpg");
//        newTest.getMyTreesTest(this);

//        newTest.addTreeTest(this, "1.jpg");
//        newTest.addTreeTest(this, "2.jpg");
//        newTest.addTreeTest(this, "3.jpg");
//        newTest.addTreeTest(this, "4.jpg");
//        newTest.addTreeTest(this, "5.jpg");
//        newTest.updateTree(this, "U1_1.jpg", 4);
//        newTest.updateTree(this, "U1_2.jpg", 1);
//        newTest.updateTree(this, "U2_1.jpg", 2);
//        newTest.getTimelineTest(this, (long) 4);
//        newTest.getTreeByIDTest(6);
//        newTest.getTreesIUpdatedTest(this);


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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantRes) {
        boolean allPermitted = true;
        for (int grant : grantRes){
            if (grant != PackageManager.PERMISSION_GRANTED){
                allPermitted = false;
                break;
            }
        }

        if (allPermitted) return;
        else {
            Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                    .show();
            //while(1)
        }
    }
}



/*
* 1. Tree List Titles
* 2. Activities other main need navigation
* 3. Tree/Timeline Detail => photo larger
*
*
* */