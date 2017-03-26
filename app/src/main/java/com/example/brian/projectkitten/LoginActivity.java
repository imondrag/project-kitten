package com.example.brian.projectkitten;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Button chat_room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        chat_room = (Button) findViewById(R.id.button);
        chat_room.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                EditText phone_input = (EditText) findViewById(R.id.phone);
                EditText key_input = (EditText) findViewById(R.id.key);

                Intent intent = new Intent(LoginActivity.this, MessageActivity.class);
                UserData.phoneNumber = phone_input.getText().toString();
//                intent.putExtra("Phone number", phone_input.getText().toString());

                boolean phone = checkPhone();
                boolean key = checkKey(key_input);

                if(phone && key) {
                    sendFind(phone_input.getText().toString());
                    startActivity(intent);
                } else {
                    if(!phone) {
                        phone_input.setError("Please enter a valid phone number");
                    }
                    if(!key) {
                        key_input.setError("Incorrect key");
                    }
                }

            }
        });

        // Update custom fonts
        ((TextView) findViewById(R.id.title_login)).setTypeface(
                Typeface.createFromAsset(getAssets(), getString(R.string.font_supertitle)));
        ((TextView) findViewById(R.id.key)).setTypeface(
                Typeface.createFromAsset(getAssets(), getString(R.string.font_title)));
        ((TextView) findViewById(R.id.phone)).setTypeface(
                Typeface.createFromAsset(getAssets(), getString(R.string.font_title)));
        chat_room.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.font_main)));
    }

    private boolean checkPhone() {
        String number = UserData.phoneNumber;

        if(isNumeric(number)) {
            if(number.length() == 11 && number.charAt(0) == '1' ) {
                UserData.phoneNumber = "+" + UserData.phoneNumber;
                return true;
            } else if(number.length() == 10) {
                UserData.phoneNumber = "+1" + UserData.phoneNumber;
                return true;
            }
        } else {
            if(number.substring(0, 1).equals("+1") && number.length() == 12) {
                return true;
            }
        }
        return false;
    }

    private boolean checkKey(EditText key) {
        final StringBuffer key_received = new StringBuffer();

        // Get text from the message box
        String send = key.getText().toString();

        SendHttp response = MessageActivity.tradeMessages(send);

        return response.receivedMessage.equals("valid");
    }

    private void sendFind(final String phone) {

        // Send find request
        final String send = "/find";
        SendHttp response = MessageActivity.tradeMessages(send);

        if( response.receivedMessage.equals("queue") )
        {
            View v = findViewById(android.R.id.content);
            Snackbar.make(v, "Locating a new partner...", Snackbar.LENGTH_SHORT).show();
        }
        else if ( response.receivedMessage.equals("connect") )
        {
            View v = findViewById(android.R.id.content);
            Snackbar.make(v, "You've been connected!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public boolean isNumeric(String s) {
        for(int i = 0; i < s.length(); ++i) {
            if(!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageActivity.closeConnection();
    }
}
