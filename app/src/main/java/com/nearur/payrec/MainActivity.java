package com.nearur.payrec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, navigation {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private UserDetails userDetails;
    private Salary salary;
    private Piecerate piecerate;
    private AddEmployee addEmployee;

    private ViewPager viewPager;
    private SectionPagerAdapter pagerAdapter;
    MenuItem item;
    CircleImageView imageView;
    RelativeLayout layout;
    TextView name, email;
    NavigationTabBar navigationTabBar;
    Toolbar toolbar;
    String[] titles = {"Dashboard", "Salaried Workers", "DailyWaged Workers", "Add Employee", "Profile"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dashboard");
        setSupportActionBar(toolbar);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        imageView = hView.findViewById(R.id.imageView);
        layout = (RelativeLayout) hView.findViewById(R.id.layout);
        name = (TextView) hView.findViewById(R.id.name);
        email = (TextView) hView.findViewById(R.id.email);

        initviews();
        initAuth();
        initbroadcast();



    }

    private void initbroadcast() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "Token : " + SharedPrefManager.getInstance(getApplicationContext()).getToken(), Toast.LENGTH_SHORT).show();
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(MyTokenService.broadcast));

    }

    private void initviews() {

        pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        userDetails = new UserDetails();
        salary = new Salary();
        piecerate = new Piecerate();
        addEmployee = new AddEmployee();

        navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.home),
                        //Color.parseColor(colors[0])
                        Color.TRANSPARENT
                ).title("Home")
                        .badgeTitle("NTB")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.worker),
                        Color.TRANSPARENT// Color.parseColor(colors[1])
                ).title("Salaried")
                        .badgeTitle("with")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.worker),
                        Color.TRANSPARENT//Color.parseColor(colors[2])
                ).title("DailyWaged")
                        .badgeTitle("state")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.addemployee),
                        Color.TRANSPARENT//Color.parseColor(colors[3])
                ).title("Add")
                        .badgeTitle("icon")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.profile2),
                        Color.TRANSPARENT//Color.parseColor(colors[4])
                ).title("Profile")
                        .badgeTitle("777")
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setIsTitled(true);
        navigationTabBar.setTitleMode(NavigationTabBar.TitleMode.ALL);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setIsBadged(true);
        navigationTabBar.setBackgroundColor(Color.TRANSPARENT);
        navigationTabBar.setInactiveColor(Color.BLACK);

        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                toolbar.setTitle(titles[position]);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


/*
        navigationTabBar.setTitleMode(NavigationTabBar.TitleMode.ACTIVE);
        navigationTabBar.setBadgeGravity(NavigationTabBar.BadgeGravity.BOTTOM);
        navigationTabBar.setBadgePosition(NavigationTabBar.BadgePosition.CENTER);
        navigationTabBar.setIsBadged(true);
        navigationTabBar.setIsTitled(true);
        navigationTabBar.setIsTinted(true);
        navigationTabBar.setIsBadgeUseTypeface(true);
        navigationTabBar.setBadgeBgColor(Color.RED);
        navigationTabBar.setBadgeTitleColor(Color.WHITE);
        navigationTabBar.setIsSwiped(true);

        navigationTabBar.setBadgeSize(10);
*/


    }

    private void initiuser() {
        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            if (user.getPhotoUrl() == null || user.getDisplayName() == null) {
                Toast.makeText(getApplicationContext(),"Please Provide details",Toast.LENGTH_LONG).show();
                navigationTabBar.setViewPager(viewPager,4);
            } else {
                Glide.with(getApplicationContext()).load(user.getPhotoUrl()).centerCrop().into(imageView);
            }
        }else{
            startActivity(new Intent(MainActivity.this, LoginSignup.class));
            finish();
        }
    }


    private void initAuth() {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                initiuser();
            }
        };

    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (navigationTabBar.getModelIndex() != 0) {
            navigationTabBar.setViewPager(viewPager, 0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        item=menu.getItem(0);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {

           navigationTabBar.setViewPager(viewPager,4);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

            FirebaseMessaging.getInstance().subscribeToTopic("Workers");
            Toast.makeText(getApplicationContext(), "Subscribed", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {

            user = auth.getCurrentUser();
        }
    }

    @Override
    public void setposition(int position) {
        navigationTabBar.setViewPager(viewPager, position);
    }



    private class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return userDetails;
                case 1:
                    return salary;
                case 2:
                    return piecerate;
                case 3:
                    return addEmployee;
                case 4:
                    return new UserProfile();
            }
            return null;

        }
    }


}
