package com.example.brian.projectkitten;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataOutputStream;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * Created by Brian on 3/25/2017.
 */

public class SendHttp {
    public static String domain = "http://54.202.89.204:5000/api";
    String to_send, receivedMessage = "unset";
    int responseCode = 0;
    boolean post;

    public SendHttp(String message, boolean post) {
        to_send = message;
        this.post = post;
    }

    public void sendMessage() throws IOException {

        URL url = new URL(domain + (post ? "" : "/" + UserData.phoneNumber));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        String urlParameters;

        //add request header
        System.out.println( "Post: " + post );
        if( post ) {
            con.setRequestMethod("POST");
            con.setRequestProperty("Body", to_send);
            con.setRequestProperty("From", UserData.phoneNumber);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            urlParameters = "Body=" + URLEncoder.encode(to_send, "UTF-8") + "&From=" +
                    URLEncoder.encode(UserData.phoneNumber, "UTF-8");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        } else {
            con.setRequestMethod("GET");
            con.setRequestProperty("To", UserData.phoneNumber);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlParameters = "To=" + URLEncoder.encode(UserData.phoneNumber, "UTF-8");
        }

        responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println("Send class: " + response);

        extractResponse(response);
        System.out.println("Extracted Response: " + receivedMessage);
    }

    public String extractResponse(StringBuffer response) {
        try{
            int begin, end;
            String str_response;

            str_response = response.toString();
            begin = str_response.indexOf("<Body>") + 6;
            end = str_response.indexOf("</Body>");

            receivedMessage = str_response.substring(begin, end);
        } catch (StringIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return receivedMessage = "";
        }

        return receivedMessage;
    }

    public String receiveMessage() {
        return receivedMessage;
    }

    public static void setNumber(String number_in) {
        UserData.phoneNumber = number_in;
    }

    public static String getNumber(String number_in) {
        return UserData.phoneNumber;
    }
}
