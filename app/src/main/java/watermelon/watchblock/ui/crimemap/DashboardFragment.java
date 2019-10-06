package watermelon.watchblock.ui.crimemap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import watermelon.watchblock.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static watermelon.watchblock.MainActivity.uuid;

public class DashboardFragment extends Fragment implements OnMapReadyCallback
{

    public static final String CRIME_TIME = "crimeTimeKey";

    private DashboardViewModel dashboardViewModel;
    String update;
    private GoogleMap mMap;
    String time = "12:00";
    String description = "Execution of Juniper";
    LatLng location = new LatLng(35.1032075, -106.6011941);
    String[] crimesUnparsed;
    String[][] crimesParsed;
    String[] descriptions;
    String[] lats;
    String[] longs;
    String[] times;
    SharedPreferences sharedpreferences;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                System.out.println("ON MAP READY");
                try
                {
                    update();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                parseUpdate(update);

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(35.1032075,-106.6011941))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();
//


                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(description + " at " + time));

                for (int i = 0; i < descriptions.length; i++){
                    System.out.println("crime: ");
                    System.out.println("Description: " + descriptions[i]);
                    System.out.println("Lats: " + lats[i]);
                    System.out.println("Longs: " + longs[i]);
                    System.out.println("Time: " + times[i]);

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(lats[i]), Double.parseDouble(longs[i])))
                            .title(descriptions[i] + " at " + getDataFromUNIX(Long.parseLong(times[i])).toString()));

                }

            }
        });



        return root;
    }

    private void parseUpdate(String updateString){
        System.out.println("updateString: " + updateString);
        System.out.println("Parsed Information Below: ");
        crimesUnparsed = updateString.split("crimeDescription\":\"");
        System.out.println("There are this many crimes: " + crimesUnparsed.length);
        for (int s = 0; s < crimesUnparsed.length; s++){
            System.out.println(crimesUnparsed[s]);
        }

        crimesParsed = new String[crimesUnparsed.length-1][4];
        descriptions = new String[crimesUnparsed.length-1];
        lats = new String[crimesUnparsed.length-1];
        longs = new String[crimesUnparsed.length-1];
        times = new String[crimesUnparsed.length-1];

        for (int s = 1; s < crimesUnparsed.length; s++){
            //descriptions[s-1] = crimesUnparsed[s].split("\",\"", 1);
            crimesParsed[s-1]= crimesUnparsed[s].split("\",\"", 4);

        }


        sharedpreferences = this.getActivity().getSharedPreferences("mainprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        System.out.println("\n\nBEFORE CRIMES UNPARSED\n\n");
        for (int i = 0; i < crimesUnparsed.length-1; i++){
            descriptions[i] = crimesParsed[i][0];
            lats[i] = crimesParsed[i][1].substring(6);
            longs[i] = crimesParsed[i][2].substring(7);
            times[i] = crimesParsed[i][3].substring(6,16);
//            editor.putString(CRIME_TIME, times[i]);
//            editor.apply();
//            System.out.println("\n\nRIGHT BEFORE PREFERENCES STUFF\n\n");
//            System.out.println("SIZE SHARED PREF SET" + sharedpreferences.getStringSet(CRIME_TIME, null).size());
            Set<String> defVals = new HashSet<>();
            System.out.println("JOHNS PLAYLIST");
            Set<String> fetch = sharedpreferences.getStringSet(CRIME_TIME, defVals);
            if(fetch != null) {
                if(!(fetch.contains(times[i]))) {
                    System.out.println("\n\nTHE STRING IS NEW\n\n");
                }
            }

//            if (fetch.contains((String) times[i])) {
//                System.out.println("\n\n\n YA NEED TO GET IT TOGETHER\n\n\n");
//            }
//            if(!(sharedpreferences.getStringSet(CRIME_TIME, new HashSet<String>()).contains(times[i]))) {
//                System.out.println("\n\n\n\nSEND NOTIFICATION\n\n\n\n");
//                //SEND NOTIFICATION
//            }
        }

        Set<String> timesSet = new HashSet<>(Arrays.asList(times));
        editor.putStringSet(CRIME_TIME, timesSet);
        editor.apply();




//        for (int i = 0; i < descriptions.length; i++){
//            System.out.println("crime: ");
//            System.out.println("Description: " + descriptions[i]);
//            System.out.println("Lats: " + lats[i]);
//            System.out.println("Longs: " + longs[i]);
//            System.out.println("Time: " + times[i]);
//
//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(Double.parseDouble(lats[i]), Double.parseDouble(longs[i])))
//                    .title(descriptions[i] + " at " + getDataFromUNIX(Long.parseLong(times[i])).toString()));
//
//        }
    }

    public Date getDataFromUNIX(long unixTimeStamp) {
        return new Date(unixTimeStamp*1000L);
    }

    private void update() throws Exception {

        String url = "https://test.devv.io/update";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        String urlParameters = "{ \"uuid\": \"" + uuid + "\"}";
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = urlParameters.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        System.out.println(con.getResponseCode());
        System.out.println(con.getResponseMessage());

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            update = response.toString();
            System.out.println("update: " + update);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(4, 5);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}