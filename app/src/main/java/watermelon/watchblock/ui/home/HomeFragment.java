package watermelon.watchblock.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import watermelon.watchblock.R;
import watermelon.watchblock.Submission;

public class HomeFragment extends Fragment
{

    private HomeViewModel homeViewModel;

    // COnverts unix time stamp to a date object
    public Date getDataFromUNIX(long unixTimeStamp) {
        return new Date(unixTimeStamp*1000L);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        //Start button that initiates the game.
        Button buttonSolo = root.findViewById(R.id.submitTipButton);
        buttonSolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Submission BDSM = new Submission();

                Snackbar mySnackbar = Snackbar.make(view, "Oh Luuuuueeeeeeegiii", 2000);
                mySnackbar.show();
            }
        });

        Button fileUploadButton = root.findViewById(R.id.fileUploadButton);
        fileUploadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Date now = new Date();
                Long longTime = now.getTime() / 1000;
                int duration = 2000;


                Snackbar mySnackbar = Snackbar.make(view, getDataFromUNIX(longTime).toString(), duration);
                mySnackbar.show();
            }
        });

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                textView.setText(s);
            }
        });
        return root;
    }
}