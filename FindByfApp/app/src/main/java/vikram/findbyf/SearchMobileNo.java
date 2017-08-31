package vikram.findbyf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;


public class SearchMobileNo extends Fragment {
    private String mobileno;
    private DatabaseReference mDatabase;
    private EditText mSearchfield;
    private Button msearchBtn;
    private FirebaseAuth mAuth;
    private String subFolder = "/userdata";
    private String file = "test.ser";
    private HashMap<String, String> searchResult = new HashMap<String, String>();
    CountryCodePicker ccp;
    ProgressDialog mProgressDialog;
    String targetName = "***",targetNumber;

    public SearchMobileNo() {
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
        final View view= inflater.inflate(R.layout.fragment_search_mobile_no, container, false);
        mAuth = FirebaseAuth.getInstance();
        try {

            readFile(); //it will show all search result in current page
            if (searchResult!=null)
            {
                addSearchResult(view);
            }
        }catch (Exception e)
        {

        }
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),R.style.AlertDialogCustom);
            dialog.setCancelable(false);
        dialog.setIcon(R.drawable.findbyf_small_logo);

            ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
            mSearchfield = (EditText) view.findViewById(R.id.field_search);


            mDatabase = FirebaseDatabase.getInstance().getReference();

            msearchBtn = (Button) view.findViewById(R.id.button_search);

            msearchBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mSearchfield.length()<10 || mSearchfield.length()>10) {
                        mSearchfield.setError("Mobile No. is not Valid");

                    } else {
                        if (isNetworkAvailable(getContext())) {
                            showProgressDialog();
                            mobileno = ccp.getFullNumberWithPlus() + mSearchfield.getText().toString();
                            mDatabase.child("MobileNo").child(mobileno)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // Get user information
                                            if (dataSnapshot.getValue() != null) {
                                                final BusyDetail detail = dataSnapshot.getValue(BusyDetail.class);

                                                targetName = detail.BusyWithName;
                                                targetNumber=detail.BusyWithContactNo;
                                                if (targetNumber == "***" || targetNumber == null || targetNumber == "") {

                                                    dialog.setTitle("FindByf Result..");
                                                    dialog.setMessage("Sorry No Search result found !");
                                                    dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int id) {
                                                        }
                                                    });
                                                    final AlertDialog alert = dialog.create();
                                                    hideProgressDialog();
                                                    alert.show();
                                                }
                                                else {
                                                    Log.e("Search",targetName);
                                                    if (targetName.equalsIgnoreCase("Unknown Name") ||targetName.equalsIgnoreCase("***") || targetName == null || targetName.equalsIgnoreCase(""))
                                                    {
                                                        Log.e("Search","Entered 1");
                                                        FirebaseDatabase.getInstance().getReference().child("ContactList").child(targetNumber)
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        // Get user information
                                                                        if (dataSnapshot.getValue() != null) {
                                                                            targetName=dataSnapshot.getValue(String.class);
                                                                            String modified_targetNumber;
                                                                            modified_targetNumber="******"+targetNumber.substring(targetNumber.length()-4);

                                                                            dialog.setTitle("FindByf Result..");
                                                                            searchResult.put(mobileno, "Last Busy  With \n\tName: \"" + targetName + "\",\n\tMobile No.: "+modified_targetNumber+",\n\tDate and Time: " + detail.DateAndTime);
                                                                            writeFile();
                                                                            addSearchResult1(view, mobileno);
                                                                            dialog.setMessage(mobileno+":\nLast Busy With \nName: \"" + targetName + "\",\nMobile No.: "+modified_targetNumber+",\nDate and Time: " + detail.DateAndTime);
                                                                            dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int id) {
                                                                                }
                                                                            });
                                                                            final AlertDialog alert = dialog.create();
                                                                            hideProgressDialog();
                                                                            alert.show();
                                                                        }
                                                                        else {
                                                                            String modified_targetNumber;
                                                                            modified_targetNumber="******"+targetNumber.substring(targetNumber.length()-4);

                                                                            dialog.setTitle("FindByf Result..");
                                                                            searchResult.put(mobileno, "Last Busy  With \n\tName: \"" + targetName + "\",\n\tMobile No.: "+modified_targetNumber+",\n\tDate and Time: " + detail.DateAndTime);
                                                                            writeFile();
                                                                            addSearchResult1(view, mobileno);
                                                                            dialog.setMessage(mobileno+":\nLast Busy With \nName: \"" + targetName + "\",\nMobile No.: "+modified_targetNumber+",\nDate and Time: " + detail.DateAndTime);
                                                                            dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int id) {
                                                                                }
                                                                            });
                                                                            final AlertDialog alert = dialog.create();
                                                                            hideProgressDialog();
                                                                            alert.show();

                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                    }
                                                                });

                                                    }

                                                  }

                                            } else {
                                                dialog.setTitle("Search Result..");

                                                dialog.setMessage("Sorry!Search Not found");
                                                dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                    }
                                                });
                                                final AlertDialog alert = dialog.create();
                                                hideProgressDialog();
                                                alert.show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            dialog.setTitle("Search Result..");

                                            dialog.setMessage(databaseError.toString());
                                            dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });
                                            final AlertDialog alert = dialog.create();
                                            hideProgressDialog();
                                            alert.show();

                                        }
                                    });
                            mSearchfield.setText("");

                        } else {
                            Toast.makeText(getContext(), "Need Network Connection", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            });
        return view;
    }

    private boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void writeFile() {
        final File path =

                Environment.getExternalStoragePublicDirectory
                        (
                                Environment.getDataDirectory().getPath()+"/CallBusyName"
                        );

        if (!path.exists()) {
            path.mkdirs();
        }

        final File file = new File(path, "mydemo2.txt");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(searchResult);
        } catch (IOException ex) {
            ex.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.flush();
                fos.close();
                if (out != null)
                    out.flush();
                out.close();
            } catch (Exception e) {

            }
        }
    }


    public void readFile() throws FileNotFoundException {
        final File path =

                Environment.getExternalStoragePublicDirectory
                        (
                                Environment.getDataDirectory().getPath()+"/CallBusyName"
                        );

        if (!path.exists()) {
            return;
        }

        final File file = new File(path, "mydemo2.txt");

            if (!file.exists()) {
                return;
            }
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(file);
            in = new ObjectInputStream(fis);
            HashMap<String, String> myHashMap = (HashMap<String, String> ) in.readObject();
            searchResult = myHashMap;
            //System.out.println("count of hash map::"+searchResult.size() + " " + searchResult);

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            try {
                if(fis != null) {
                    fis.close();
                }
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void addSearchResult(View view)
    {
        Context context=getContext();
        LinearLayout main_layout=(LinearLayout) view.findViewById(R.id.list_content);
        for (String key: searchResult.keySet()) {
//children of parent linearlayout

            LinearLayout parent = new LinearLayout(context);
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setBackgroundResource(R.drawable.layout_textview_bg);
            main_layout.addView(parent);

            TextView MobileNo = new TextView(getContext());
            MobileNo.setText(key+":");
            MobileNo.setTextColor(Color.parseColor("#0b84aa"));
            MobileNo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);;
            parent.addView(MobileNo);


            LinearLayout layout = new LinearLayout(context);

            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);

            parent.addView(layout);

//children of layout2 LinearLayout

            TextView detail = new TextView(context);
            detail.setText(searchResult.get(key));
            detail.setTextColor(Color.parseColor("#3B444B"));
            detail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);;
            layout.addView(detail);
            LinearLayout parent1 = new LinearLayout(context);
            parent1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent1.setOrientation(LinearLayout.VERTICAL);
            parent1.setBackgroundResource(R.drawable.layout_dummy_bg);
            TextView demo1 = new TextView(getContext());
            demo1.setText("");
            parent1.addView(demo1);
            main_layout.addView(parent1);
        }


        //List<TextView> textList = new ArrayList<TextView>(2);


            //textList.add(newTV);
    }
    public void addSearchResult1(View view,String key)
    {
        Context context=getContext();
        LinearLayout main_layout=(LinearLayout) view.findViewById(R.id.list_content);

            LinearLayout parent = new LinearLayout(context);
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setBackgroundResource(R.drawable.layout_textview_bg);
            main_layout.addView(parent);

            TextView MobileNo = new TextView(getContext());
            MobileNo.setText(key+":");
            MobileNo.setTextColor(Color.parseColor("#0b84aa"));
            MobileNo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);;
            parent.addView(MobileNo);



            LinearLayout layout = new LinearLayout(context);

            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);

            parent.addView(layout);

            TextView detail = new TextView(context);
            detail.setText(searchResult.get(key));
            detail.setTextColor(Color.parseColor("#3B444B"));
            detail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);;
            layout.addView(detail);
        LinearLayout parent1 = new LinearLayout(context);
        parent1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent1.setOrientation(LinearLayout.VERTICAL);
        parent1.setBackgroundResource(R.drawable.layout_bg);
        TextView demo1 = new TextView(getContext());
        parent1.addView(demo1);
        main_layout.addView(parent1);

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
