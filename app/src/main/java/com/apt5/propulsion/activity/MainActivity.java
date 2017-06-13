package com.apt5.propulsion.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apt5.propulsion.Keys;
import com.apt5.propulsion.R;
import com.apt5.propulsion.fragment.AddIdeaFragment;
import com.apt5.propulsion.fragment.DraftFragment;
import com.apt5.propulsion.fragment.MyIdeaFragment;
import com.apt5.propulsion.fragment.WorldIdeaFragment;
import com.apt5.propulsion.object.Idea;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ImageView imgUserAvatar;
    private TextView tvUserName;
    private TextView tvUserMail;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddIdeaFragment()).commit();
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            currentUser = firebaseAuth.getCurrentUser();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_addidea).setChecked(true);

        //display user info
        if (currentUser != null) {
            View header = navigationView.getHeaderView(0);
            imgUserAvatar = (ImageView) header.findViewById(R.id.img_user_avatar);
            tvUserName = (TextView) header.findViewById(R.id.tv_user_name);
            tvUserMail = (TextView) header.findViewById(R.id.tv_user_mail);

            tvUserMail.setText(firebaseAuth.getCurrentUser().getEmail());
            tvUserName.setText(firebaseAuth.getCurrentUser().getDisplayName());
            Glide.with(header.getContext()).load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(imgUserAvatar);
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                navigationView.getMenu().findItem(R.id.nav_addidea).setChecked(true);

                Bundle bundle = intent.getBundleExtra("data");
                String pos = bundle.getString("position");
                Realm.init(context);
                RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
                Realm realm = Realm.getInstance(realmConfiguration);

                Idea ideaedit = realm.where(Idea.class).equalTo("titletime",pos).findFirst();
                Toast.makeText(context,ideaedit.getTitle(),Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AddIdeaFragment addIdeaFragment = new AddIdeaFragment(ideaedit);
                fragmentTransaction.replace(R.id.fragment_container,addIdeaFragment);
                fragmentTransaction.commit();

            }
        },new IntentFilter(Keys.CHANGE_FRAGMENT_START_SEND));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switchFragment(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchFragment(int id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if (id == R.id.nav_addidea) {
            fragmentTransaction.replace(R.id.fragment_container, new AddIdeaFragment());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_worldidea) {
            fragmentTransaction.replace(R.id.fragment_container, new WorldIdeaFragment());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_myidea) {
            fragmentTransaction.replace(R.id.fragment_container, new MyIdeaFragment());
            fragmentTransaction.commit();
        } else if (id == R.id.nav_draft) {
            fragmentTransaction.replace(R.id.fragment_container, new DraftFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
