package watermelon.watchblock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import watermelon.watchblock.ui.settings.ProfileActivity;

public class MainActivity extends AppCompatActivity
{

    static String title;
    public static String uuid;
    public static String coinId;
    public static String myPub;
    public static String update;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        disableSSLCertificateChecking();
        try
        {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e)
        {
        }


        setContentView(R.layout.activity_main);

        final SharedPreferences prefs = MainActivity.this.getSharedPreferences("mainprefs", 0);

        if (prefs.getBoolean("firstrun", true))
        {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
            Intent intent = new Intent(MainActivity.this.getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        }


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    sendPost();
                    defineTemplate();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(5000);
                        update();
                        // update is the raw string
                        String raw_string[] = update.split("\"time\":");
                        for (int i = 1; i < raw_string.length; i++)
                        {
                            long time = Long.parseLong(raw_string[i].substring(0, 10));
                            Date now = new Date();
                            long longTime = now.getTime() / 1000L;

                            int minInterval = 10;
                            if (longTime - time <= minInterval)
                            {
                                System.out.println(longTime + " " + time);
                                //parse information\
                                String crimesUnparsed[] = raw_string[i - 1].split("crimeDescription\":\"");
                                String eventDetails[] = crimesUnparsed[1].split("\",\"");
                                for (String s : eventDetails)
                                {
                                    System.out.println("help: " + s);
                                }
                                String description = eventDetails[0];
                                double lat = Double.parseDouble(eventDetails[1].replaceAll("[^\\d.]", ""));
                                double longi = Double.parseDouble(eventDetails[2].replaceAll("[^\\d.]", ""));


                                //submit a notification
                                //System.out.println(distance(35.081, -106.627, lat, longi));

                                Snackbar mySnackbar = Snackbar.make(findViewById(android.R.id.content), description + " at " + lat + "," + longi, 5000);
                                mySnackbar.show();
                            }
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private static double distance(double currentLat, double currentLong, double crimeLat, double crimeLong)
    {
        if ((currentLat == crimeLat) && (currentLong == crimeLong))
        {
            return 0;
        } else
        {
            double theta = currentLong - crimeLong;
            double dist = Math.sin(Math.toRadians(currentLat)) * Math.sin(Math.toRadians(crimeLat)) + Math.cos(Math.toRadians(currentLat)) * Math.cos(Math.toRadians(crimeLat)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }
    }

    public Date getDataFromUNIX(long unixTimeStamp)
    {
        return new Date(unixTimeStamp * 1000L);
    }

    private boolean inDistanceRange(double distance, int range)
    {
        return range - distance > 0;
    }


    private int timeInterval(long currentTime, long crimeTime)
    {
        long diff = crimeTime - currentTime;
        Date timeDiff = getDataFromUNIX(diff);
        int minutes = timeDiff.getMinutes();

        return minutes;
    }

    private boolean inTimeRange(int timeInterval, int range)
    {
        return range - timeInterval > 0;
    }

    private void update() throws Exception
    {

        String url = "https://test.devv.io/update";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        String urlParameters = "{ \"uuid\": \"" + uuid + "\"}";
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream())
        {
            byte[] input = urlParameters.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        System.out.println(con.getResponseCode());
        System.out.println(con.getResponseMessage());

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8")))
        {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null)
            {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            update = response.toString();
            System.out.println("update: " + update);
        }

    }

    private static void disableSSLCertificateChecking()
    {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager()
                {

                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException
                    {
                        // not implemented
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException
                    {
                        // not implemented
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers()
                    {
                        return null;
                    }

                }
        };

        try
        {

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
            {

                @Override
                public boolean verify(String s, SSLSession sslSession)
                {
                    return true;
                }

            });
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (KeyManagementException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    private void sendPost() throws Exception
    {

        String url = "https://test.devv.io/login";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        String urlParameters = "{\"user\":\"kelsiesucks\"," +
                "\"pass\":\"d58e461ccafdd86d88ee49607aefc4882850eabf9e59f56d9d9c5c1c2f3d6abe" +
                "\"}";
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream())
        {
            byte[] input = urlParameters.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        System.out.println(con.getResponseCode());
        System.out.println(con.getResponseMessage());

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8")))
        {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null)
            {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            String strResp = response.toString();
            uuid = strResp.substring(strResp.indexOf("uuid") + 7, strResp.length() - 2);
            myPub = strResp.substring(strResp.indexOf("pub") + 6, strResp.indexOf("testnet") - 2);
            System.out.println(uuid);
            System.out.println(myPub);

        }

    }

    private void defineTemplate() throws Exception
    {

        String url = "https://test.devv.io/define-template";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        String urlParameters = "{ \"uuid\": \"" + uuid + "\", \"name\": \"Crime\"," +
                "\"fungible\": false, \"properties\": " +
                "{\"crimeDescription\": {\"type\": \"string\"," + "\"default\": \"\"}," +
                "\"lat\": {\"type\": \"string\"," + "\"default\": \"\"}," +
                "\"long\": {\"type\": \"string\"," + "\"default\": \"\"}," +
                "\"time\": {\"type\": \"int\"," + "\"default\": 1}" +
                "}, \"required\": [\"crimeDescription\", \"lat\",\"long\", \"time\"]}";
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream())
        {
            byte[] input = urlParameters.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        System.out.println(con.getResponseCode());
        System.out.println(con.getResponseMessage());

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8")))
        {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null)
            {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            String strResp = response.toString();
            coinId = strResp.substring(strResp.indexOf("coin_id\":\"") + 10, strResp.indexOf("message") - 3);
            System.out.println(coinId);
        }

    }

}
