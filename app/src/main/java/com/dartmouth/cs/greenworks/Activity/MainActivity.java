package com.dartmouth.cs.greenworks.Activity;

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
import android.view.MenuItem;
import android.Manifest;

import com.dartmouth.cs.greenworks.Fragment.MapFragment;
import com.dartmouth.cs.greenworks.Fragment.MyTreesFragment;
import com.dartmouth.cs.greenworks.Fragment.TreesIUpdatedFragment;
import com.dartmouth.cs.greenworks.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(com.dartmouth.cs.greenworks.R.layout.activity_main);

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
        testBackend();

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
                        .replace(R.id.content, mSecondFragment)
                        .commit();
                break;
            case R.id.drawer_item_3:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, mThirdFragment)
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


    public void testBackend() {
        BackendTest newTest = new BackendTest();
        newTest.registerTest(this);
        newTest.addTreeTest(this, R.drawable.getty);

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
