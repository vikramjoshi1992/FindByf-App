package vikram.findbyf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


public class HelpFaq extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static String userId;
    private EditText nameET;
    private EditText emailET;
    private EditText phoneNoET;
    private Button editProfileBtn;
    private Button profileSaveBtn;

    public HelpFaq() {
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
        View view = inflater.inflate(R.layout.fragment_help_faq, container, false);

        return view;

    }
}
