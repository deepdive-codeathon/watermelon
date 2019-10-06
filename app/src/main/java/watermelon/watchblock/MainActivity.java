package watermelon.watchblock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}


        setContentView(R.layout.activity_main);

        SharedPreferences prefs = MainActivity.this.getSharedPreferences("mainprefs", 0);

        if (prefs.getBoolean("firstrun", true)) {
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

        new Thread(new Runnable() {
            @Override
            public void run() {
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
    }


    private void sendPost() throws Exception {

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
            String strResp = response.toString();
            uuid = strResp.substring(strResp.indexOf("uuid") + 7, strResp.length()-2);
            myPub = strResp.substring(strResp.indexOf("pub") + 6, strResp.indexOf("testnet")-2);
            System.out.println(uuid);
            System.out.println(myPub);

        }

    }

    private void defineTemplate() throws Exception {

        String url = "https://test.devv.io/define-template";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        String urlParameters = "{ \"uuid\": \"" + uuid +"\", \"name\": \"Crime\"," +
                "\"fungible\": false, \"properties\": " +
                "{\"crimeDescription\": {\"type\": \"string\"," + "\"default\": \"\"}," +
                "\"lat\": {\"type\": \"string\"," + "\"default\": \"\"}," +
                "\"long\": {\"type\": \"string\"," + "\"default\": \"\"}," +
                "\"time\": {\"type\": \"int\"," + "\"default\": 1}" +
                "}, \"required\": [\"crimeDescription\", \"lat\",\"long\", \"time\"]}";
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
            String strResp = response.toString();
            coinId = strResp.substring(strResp.indexOf("coin_id\":\"") + 10, strResp.indexOf("message")-3);
            System.out.println(coinId);
        }

    }

}
