package watermelon.watchblock.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import watermelon.watchblock.MainActivity;
import watermelon.watchblock.R;
import watermelon.watchblock.Submission;

public class HomeFragment extends Fragment
{

    double latti;
    double longi;
    private HomeViewModel homeViewModel;

    // COnverts unix time stamp to a date object
    public Date getDataFromUNIX(long unixTimeStamp) {
        return new Date(unixTimeStamp*1000L);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        System.out.println("1");
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                System.out.println(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {

            }

            @Override
            public void onProviderEnabled(String s)
            {

            }

            @Override
            public void onProviderDisabled(String s)
            {

            }
        };
        System.out.println("2");
        System.out.println("3");


        // check if permissions have been granted
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // request user for location access
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null)
            {
                latti = location.getLatitude();
                longi = location.getLongitude();
                System.out.println("++++++++++++++++++++++++++++ " + latti + " " + longi);
            }
            else
            {
                Snackbar.make(getView(), "Unable to retrieve location", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }



        System.out.println("7");

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
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

                System.out.println(latti);
                System.out.println(longi);



                Snackbar mySnackbar = Snackbar.make(view, getDataFromUNIX(longTime).toString(), duration);
//                Snackbar mySnackbar = Snackbar.make(view, latti + " " + longi, duration);
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