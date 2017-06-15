package com.apt5.propulsion.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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
import com.apt5.propulsion.ReceiveNotificationService;
import com.apt5.propulsion.fragment.AddIdeaFragment;
import com.apt5.propulsion.fragment.DraftFragment;
import com.apt5.propulsion.fragment.MyIdeaFragment;
import com.apt5.propulsion.fragment.WorldIdeaFragment;
import com.apt5.propulsion.object.Idea;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ImageView imgUserAvatar;
    private TextView tvUserName;
    private TextView tvUserMail;
    private NavigationView navigationView;
    private ImageView imgLogout;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    //sign out
                    FirebaseAuth.getInstance().signOut();
                    //log out facebook
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            currentUser = firebaseAuth.getCurrentUser();
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WorldIdeaFragment()).commit();
            }
        }

        //google api client
        //init google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toasty.error(MainActivity.this, "Your account doesnot exists", Toast.LENGTH_LONG, true).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_worldidea).setChecked(true);

        //display user info
        if (currentUser != null) {
            View header = navigationView.getHeaderView(0);
            imgUserAvatar = (ImageView) header.findViewById(R.id.img_user_avatar);
            tvUserName = (TextView) header.findViewById(R.id.tv_user_name);
            tvUserMail = (TextView) header.findViewById(R.id.tv_user_mail);
            imgLogout = (ImageView) header.findViewById(R.id.img_logout);

            tvUserMail.setText(firebaseAuth.getCurrentUser().getEmail());
            tvUserName.setText(firebaseAuth.getCurrentUser().getDisplayName());
            Glide.with(header.getContext()).load(firebaseAuth.getCurrentUser().getPhotoUrl()).fitCenter().into(imgUserAvatar);
            imgLogout.setOnClickListener(this);
            startService(new Intent(MainActivity.this, ReceiveNotificationService.class));
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

    @Override
    public void onClick(View v) {
        if (v == imgLogout) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int choice) {
                    switch (choice) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //sign out
                            FirebaseAuth.getInstance().signOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                        }
                                    });
                            LoginManager.getInstance().logOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.announce_log_out)
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }
}
