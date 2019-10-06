//package watermelon.watchblock;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.URL;
//
//import javax.net.ssl.HttpsURLConnection;
//
//import androidx.annotation.NonNull;
//
//public class Crime
//{
//
//    public Crime(String title)
//    {
////        this.title = title;
//        try
//        {
//            sendPost();
//            defineTemplate();
//            createAsset();
//            update();
//
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    @NonNull
//    @Override
//    public String toString()
//    {
//        return title;
//    }
//
//    // HTTP POST request
//    private void sendPost() throws Exception {
//
//        String url = "https://test.devv.io/login";
//        URL obj = new URL(url);
//        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//
//        con.setRequestMethod("POST");
//        con.setRequestProperty("Content-Type", "application/json; utf-8");
//        con.setRequestProperty("Accept", "application/json");
//
//        String urlParameters = "{\"user\":\"wb_admin\"," +
//                "\"pass\":\"00f32c3d7a080069012a2a1fff5f385b0d511b7f619d4369c2a3da104848471d" +
//                "\"}";
//        con.setDoOutput(true);
//
//        try(OutputStream os = con.getOutputStream()) {
//            byte[] input = urlParameters.getBytes("utf-8");
//            os.write(input, 0, input.length);
//        }
//
//        System.out.println(con.getResponseCode());
//        System.out.println(con.getResponseMessage());
//
//        try(BufferedReader br = new BufferedReader(
//                new InputStreamReader(con.getInputStream(), "utf-8"))) {
//            StringBuilder response = new StringBuilder();
//            String responseLine = null;
//            while ((responseLine = br.readLine()) != null) {
//                response.append(responseLine.trim());
//            }
//            System.out.println(response.toString());
//            String strResp = response.toString();
//            uuid = strResp.substring(strResp.indexOf("uuid") + 7, strResp.length()-2);
//            System.out.println(uuid);
//        }
//
//    }
//
//    // HTTP POST request
//    private void defineTemplate() throws Exception {
//
//        String url = "https://test.devv.io/define-template";
//        URL obj = new URL(url);
//        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//
//        con.setRequestMethod("POST");
//        con.setRequestProperty("Content-Type", "application/json; utf-8");
//        con.setRequestProperty("Accept", "application/json");
//
//        String urlParameters = "{ \"uuid\": \"" + uuid +"\", \"name\": \"Crime\"," +
//                "\"fungible\": false, \"properties\": {\"crimeDescription\": {\"type\": \"string\"," +
//                "\"default\": \"\"}}, \"required\": [\"crimeDescription\"]}";
//        con.setDoOutput(true);
//
//        try(OutputStream os = con.getOutputStream()) {
//            byte[] input = urlParameters.getBytes("utf-8");
//            os.write(input, 0, input.length);
//        }
//
//        System.out.println(con.getResponseCode());
//        System.out.println(con.getResponseMessage());
//
//        try(BufferedReader br = new BufferedReader(
//                new InputStreamReader(con.getInputStream(), "utf-8"))) {
//            StringBuilder response = new StringBuilder();
//            String responseLine = null;
//            while ((responseLine = br.readLine()) != null) {
//                response.append(responseLine.trim());
//            }
//            System.out.println(response.toString());
//            String strResp = response.toString();
//            coinId = strResp.substring(strResp.indexOf("coin_id\":\"") + 10, strResp.indexOf("message")-3);
//            System.out.println(coinId);
//        }
//
//    }
//
//    private void createAsset() throws Exception {
//
//        String url = "https://test.devv.io/create-asset";
//        URL obj = new URL(url);
//        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//
//        con.setRequestMethod("POST");
//        con.setRequestProperty("Content-Type", "application/json; utf-8");
//        con.setRequestProperty("Accept", "application/json");
//
//        String urlParameters = "{ \"uuid\": \"" + uuid + "\", \"coin_id\":" +
//                "\"" + coinId+ "\", \"properties\": {\"crimeDescription\": \"Kelsie shot Juniper and Alan\"}}";
//        con.setDoOutput(true);
//
//        try(OutputStream os = con.getOutputStream()) {
//            byte[] input = urlParameters.getBytes("utf-8");
//            os.write(input, 0, input.length);
//        }
//
//        System.out.println(con.getResponseCode());
//        System.out.println(con.getResponseMessage());
//
//        try(BufferedReader br = new BufferedReader(
//                new InputStreamReader(con.getInputStream(), "utf-8"))) {
//            StringBuilder response = new StringBuilder();
//            String responseLine = null;
//            while ((responseLine = br.readLine()) != null) {
//                response.append(responseLine.trim());
//            }
//            System.out.println(response.toString());
//        }
//
//    }
//
//
//}
