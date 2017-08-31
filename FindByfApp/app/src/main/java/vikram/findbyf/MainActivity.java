package vikram.findbyf;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private TextView mName;
    private TextView mEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view =  navigationView.getHeaderView(0);

        mName=(TextView)view.findViewById(R.id.signed_in_name);
        mEmail=(TextView)view.findViewById(R.id.signed_in_email);
        setNameEmail();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_frag1);

        //NOTE:  Open fragment1 initially.
        //setNameEmail();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, new SearchMobileNo());
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        }
        else if (id == R.id.action_notification) {
            startActivity(new Intent(this, myNotification.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_frag1) {
            fragment = new SearchMobileNo();
        } else if (id == R.id.nav_frag2) {
            fragment = new MyProfile();
        }else if (id == R.id.nav_frag3) {
            fragment= new HelpFaq();

        }else if (id == R.id.nav_frag4) {
            fragment=new ContactUs();

        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "With whom your Friend is Busy ?\nWant to known ?\nThen download \'FindByf App\' from play store.\nFacebook Page :  fb.me/FindByf";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "With whom your Friend is Busy ?");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }else if (id == R.id.nav_about) {
            fragment=new About();

        }

        //NOTE: Fragment changing code
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void setNameEmail()
    {
        DatabaseReference mDatabase;

        mAuth = FirebaseAuth.getInstance();
        String userId = getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            if (isNetworkAvailable(getApplication())) {
                mDatabase.child("users").child(userId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user information
                                    User detail = dataSnapshot.getValue(User.class);
                                    if (detail.fullname!=null) {
                                        mName.setText("Name: "+detail.fullname);

                                    }
                                    else
                                    {
                                        mName.setText("Name: "+"Default");
                                    }
                                        mEmail.setText("Email: "+detail.email);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(MainActivity.this,databaseError.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

    }
    private boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public String getUid() {
        if(mAuth.getCurrentUser() == null)
        {
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
