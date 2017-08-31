package vikram.findbyf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ContactUs extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static String userId;
    private EditText subjectET;
    private EditText messageET;
    private Button sendBtn;
    private ProgressDialog mProgressDialog;
    public ContactUs() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        subjectET = (EditText) view.findViewById(R.id.field_contact_subject);
        messageET = (EditText) view.findViewById(R.id.field_contact_message);

        sendBtn = (Button) view.findViewById(R.id.contact_sendBtn);

        mAuth = FirebaseAuth.getInstance();
        userId = getUid();

        DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
        final String date = df.format(Calendar.getInstance().getTime());
            sendBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    if (isNetworkAvailable(getContext())) {
                        if (subjectET.getText().toString().length() < 3) {
                            subjectET.setError("at least 3 characters");
                        } else if (messageET.getText().toString().length() < 20) {
                            subjectET.setError("at least 20 characters");
                        } else {
                            showProgressDialog();
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            ContactUsDetail detail = new ContactUsDetail(messageET.getText().toString(),"",date);
                            mDatabase.child("ContactUs").child(userId).child(subjectET.getText().toString()).setValue(detail);
                            dialog.setTitle("FindByf.. ");
                            dialog.setMessage("Thank you for contacting us.We will response and you will get notification soon !");
                            dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    subjectET.setText("");
                                    messageET.setText("");
                                }
                            });
                            final AlertDialog alert = dialog.create();
                            hideProgressDialog();
                            alert.show();

                        }
                    }else {
                        Toast.makeText(getContext(), "Need Network Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        return view;

    }
    public String getUid() {

        if(mAuth.getCurrentUser() == null)
        {
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    private boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
