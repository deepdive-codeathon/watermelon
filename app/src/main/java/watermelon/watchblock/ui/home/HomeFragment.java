package watermelon.watchblock.ui.home;



import android.media.AudioManager;
import android.os.Build;

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
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View.OnClickListener;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;



import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;


import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import java.util.Locale;
import java.util.Map;


import javax.net.ssl.HttpsURLConnection;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import watermelon.watchblock.MainActivity;

import watermelon.watchblock.R;

import static watermelon.watchblock.MainActivity.coinId;
import static watermelon.watchblock.MainActivity.uuid;



public class HomeFragment extends Fragment
{

    double latti;
    double longi;
    private HomeViewModel homeViewModel;


    TextToSpeech tts;

//    public Map JSONtoMap(String json) {
//        Gson gson = new Gson();
//        gson = new Gson();
//        Map map = gson.fromJson(json, Map.class);
//        return map;
//    }


    private void speak(){
        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                String text = "Hello 9-1-1, this is Juniper calling.  Kelsie shot me this morning and put me in her phone.  Please help me I am in serious danger.";
       //         String text = "ni hao 9-1-1. wo shi juniper, kelsie hen bu hao ta shi yi ge ne ge hei gui.";
                String text2 = "Hello, I am block chain.  Let me block chain you and win.";
                String text3 = "Uh oh, didn't make toilet!";
                tts.setLanguage(Locale.US);
                if (status == TextToSpeech.SUCCESS){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        String utteranceId = this.hashCode() + "";
                        Bundle params = new Bundle();
                        params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_SYSTEM);
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId);
                        System.out.println("I have spoken");
                    }
                }

            }
        });
    }

    private void makeCall(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:555555555"));
        startActivity(callIntent);
    }





    // Converts unix time stamp to a date object
    public Date getDataFromUNIX(long unixTimeStamp) {
        return new Date(unixTimeStamp*1000L);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
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

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // check if permissions have been granted
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // request user for location access
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else // get phone's location
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null)
            {
                latti = location.getLatitude();
                longi = location.getLongitude();
            }
            else
            {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Unable to retrieve location", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);



        Button buttonSolo = root.findViewById(R.id.submitTipButton);



        //create PhoneStateListener
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);

        final EditText description = root.findViewById(R.id.crimeDescription);
        TextView latlong = root.findViewById(R.id.lat_long);
//        latlong.setText(latlong);
        latlong.setText("Latitude: " + latti + "\nLongitude: " + longi);
        buttonSolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                speak();
                //makeCall();
                Date now = new Date();
                Long longTime = now.getTime() / 1000;
                int duration = 2000;

                System.out.println(latti);
                System.out.println(longi);


                try
                {
                    boolean success = createAsset(description.getText().toString(),Double.toString(latti),Double.toString(longi),longTime);
                    if (success)
                    {
                        Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Crime reported.", 2000);
                        mySnackbar.show();
                        WebView webView = new WebView(getActivity().getApplicationContext());
                        webView.getSettings().setJavaScriptEnabled(true);
                        description.setText("");
                        webView.loadUrl("https://www.cabq.gov/police/file-a-police-report-online");
                        getActivity().setContentView(webView);
                    }
                    else
                    {
                        Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Submission failed, please try again later", 2000);
                        mySnackbar.show();
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
       return root;
    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getActivity().getApplicationContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getActivity().getApplicationContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }


    private boolean createAsset(String desc, String lat, String lon, long time) throws Exception {

        desc = desc.replace('\n',' ');
        String url = "https://test.devv.io/create-asset";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        String urlParameters = "{ \"uuid\": \"" + uuid + "\", \"coin_id\":" +
                "\"" + coinId+ "\", \"properties\": {\"crimeDescription\": \""+desc+"\"," +
                "\"lat\": \""+lat+"\",\"long\": \""+lon+"\",\"time\": "+time+"}}";
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
        }
        return con.getResponseCode() == 200;

    }

    private boolean sendTx() throws Exception {

        String url = "https://test.devv.io/create-asset";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        String urlParameters = "{ \"uuid\": \"" + uuid + "\", \"coin_id\":" +
                "\"" + coinId+ "\", \"properties\": {\"crimeDescription\": \""+"\"," + "\"lat\": \""+"\",\"long\": \""+"\",\"time\": "+"}}";
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
        }
        return con.getResponseCode() == 200;

    }

}