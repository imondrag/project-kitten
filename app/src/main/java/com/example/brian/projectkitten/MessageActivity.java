package com.example.brian.projectkitten;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;
import android.content.Context;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private static Button send;
    private static Button back;
    private static ListView listView;
    private static Button btnSend;
    private static EditText editText;
    static boolean isMine = true;
    private static List<ChatMessage> chatMessages;
    private static ArrayAdapter<ChatMessage> adapter;
    private Context current = MessageActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatMessages = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_msg);
        btnSend = (Button) findViewById(R.id.btn_chat_send);
        editText = (EditText) findViewById(R.id.msg_type);

        //set ListView adapter first
        adapter = new MessageAdapter(this, R.layout.item_chat_left, chatMessages);
        listView.setAdapter(adapter);

        // Instantiate the poller if it isn't already runnning
        ResponsePoller.getInstance().setActivity(this);

        //event for button SEND
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Get text from the message box
                final String send = editText.getText().toString();
                makeMessage(send, true, editText, current);

                SendHttp resp = tradeMessages(send);
                if(!resp.receivedMessage.equals(""))
                    makeMessage(resp.receivedMessage, false, editText, current);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                btnSend.setEnabled(enable);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // Update custom fonts
        editText.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.font_title)));
        btnSend.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.font_main)));

    }
    public static SendHttp tradeMessages(final String message){
        return tradeMessages(message, true);
    }
    public static SendHttp tradeMessages(final String message, final boolean post){

        final SendHttp[] httpReturned = new SendHttp[1];

        class WorkerThread extends Thread {
            public void run() {
                // Initialize http request
                SendHttp httpRequest = new SendHttp(message, post);
                httpRequest.setNumber(UserData.phoneNumber);

                // Send message and receive response
                try { httpRequest.sendMessage(); }
                catch( IOException e ) { e.printStackTrace(); }
                httpRequest.receiveMessage();

                httpReturned[0] = httpRequest;
            }
        };

        // Create a new thread and start, send message
        WorkerThread thread = new WorkerThread();
        thread.setDaemon(true);
        thread.start();

        // Wait for completion of HTTP
        while(true) {
            try {
                thread.join(); break;
            } catch (InterruptedException iex) {} // Leave and give it another go
        }

        // Debug and return
        System.out.println("Response Code: " + httpReturned[0].responseCode);
        System.out.println("Key Received : " + httpReturned[0].receivedMessage);
        return httpReturned[0];
    }

    public static void makeMessage(String text, boolean send, EditText editText, Context current) {
        if (text.trim().equals("")) {
            Toast.makeText(current, "Please input some text...", Toast.LENGTH_SHORT).show();
        } else {
            //add message to list
            ChatMessage chatMessage = new ChatMessage(text, send);
            chatMessages.add(chatMessage);
            adapter.notifyDataSetChanged();
            if (send) {
                isMine = true;
                editText.setText("");
            } else {
                isMine = false;
            }
        }
    }

    public static boolean closed = false;
    public static void closeConnection()
    {
        if(closed) return;
        tradeMessages("/leave");
        tradeMessages("/leave");
        closed = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ResponsePoller.getInstance().destroy();
        closeConnection();
    }
}
