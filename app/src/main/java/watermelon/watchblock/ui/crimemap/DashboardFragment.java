package watermelon.watchblock.ui.crimemap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

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
    SharedPreferences sharedPreferences;

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

                    LatLng currentLocation = new LatLng(35.081298, -106.627641);
                    double dist = distance(currentLocation.latitude, currentLocation.longitude,
                            Double.parseDouble(lats[i]), Double.parseDouble(longs[i]));

                    sharedPreferences = getActivity().getSharedPreferences("mainprefs", 0);
                    int range = Integer.parseInt(sharedPreferences.getString("10", "10"));
                    int slideTime = Integer.parseInt(sharedPreferences.getString("30", "30"));
                    System.out.println("Time comparison:");
                    System.out.println("Current Time: " + System.currentTimeMillis() / 1000L);
                    System.out.println("Crime Time: " + Long.parseLong(times[i]));
                    int timeInt = timeInterval(System.currentTimeMillis() / 1000L, Long.parseLong(times[i]));
                    if(inDistanceRange(dist, range) && inTimeRange(timeInt, slideTime)){
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(lats[i]), Double.parseDouble(longs[i])))
                                .title(descriptions[i] + " on " + getDataFromUNIX(Long.parseLong(times[i])).toString()));
                    }




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
        for (int i = 0; i < crimesUnparsed.length-1; i++){
            descriptions[i] = crimesParsed[i][0];
            lats[i] = crimesParsed[i][1].substring(6);
            longs[i] = crimesParsed[i][2].substring(7);
            times[i] = crimesParsed[i][3].substring(6,16);
        }
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

    private double distance(double currentLat, double currentLong, double crimeLat, double crimeLong) {
        if ((currentLat == crimeLat) && (currentLong == crimeLong)) {
            return 0;
        }
        else {
            double theta = currentLong - crimeLong;
            double dist = Math.sin(Math.toRadians(currentLat)) * Math.sin(Math.toRadians(crimeLat)) + Math.cos(Math.toRadians(currentLat)) * Math.cos(Math.toRadians(crimeLat)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }
    }

    private boolean inDistanceRange(double distance, int range) {
        return range - distance > 0;
    }


    private int timeInterval(long currentTime, long crimeTime) {
        long diff = crimeTime - currentTime;
        Date timeDiff = getDataFromUNIX(diff);
        int minutes = timeDiff.getMinutes();

        return minutes;
    }

    private boolean inTimeRange(int timeInterval, int range) {
        return range - timeInterval > 0;
    }


}