package watermelon.watchblock;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;

public class Crime
{
    String title;
    public Crime(String title)
    {
        this.title = title;
        try
        {
            sendPost();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString()
    {
        return title;
    }

    // HTTP POST request
    private void sendPost() throws Exception {

        String url = "https://test.devv.io/login";
//        String url = "google.com";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
//        con.setRequestProperty("User-Agent", USER_AGENT);
//        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "{\"user\":\"wb_admin\"," +
                "\"pass\":\"00f32c3d7a080069012a2a1fff5f385b0d511b7f619d4369c2a3da104848471d" +
                "\"}";
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = urlParameters.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Send post request
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(urlParameters);
//        wr.flush();
//        wr.close();
        System.out.println(con.getResponseCode()
);
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

    }
}
