package vikram.findbyf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private boolean all_permission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        take_permission();
        Button btn = (Button) findViewById(R.id.button_home_resfresh);
        btn.setOnClickListener(this);
        if (all_permission==true)
        {

            Intent myIntent = new Intent(Home.this, SignInActivity.class);
            finish();
            startActivity(myIntent);


        }

    }

    public void take_permission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 3);

            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 4);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, 5);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            all_permission=true;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                //Toast.makeText(getApplicationContext(), "read_contacts is given", Toast.LENGTH_SHORT).show();
                take_permission();
            } else {
                //Toast.makeText(getApplicationContext(), "PERMISSION:READ_CONTACTS is not given", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                //Toast.makeText(getApplicationContext(), "external storage is given", Toast.LENGTH_SHORT).show();
                take_permission();
            } else {
                //Toast.makeText(getApplicationContext(), "PERMISSION:WRITE_EXTERNAL_STORAGE is not given", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                //Toast.makeText(getApplicationContext(), "network is given", Toast.LENGTH_SHORT).show();
                take_permission();
            } else {
                //Toast.makeText(getApplicationContext(), "PERMISSION:ACCESS_NETWORK_STATE is not given", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 4) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                //Toast.makeText(getApplicationContext(), "network is given", Toast.LENGTH_SHORT).show();
                take_permission();
            } else {
                //Toast.makeText(getApplicationContext(), "PERMISSION:READ_PHONE_STATE is not given", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                //Toast.makeText(getApplicationContext(), "network is given", Toast.LENGTH_SHORT).show();
                take_permission();
            } else {
                //Toast.makeText(getApplicationContext(), "PERMISSION:PROCESS_OUTGOING_CALLS is not given", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_home_resfresh) {
            Intent myIntent = new Intent(Home.this, Home.class);
            finish();
            startActivity(myIntent);
        }

        }
}
