package com.example.brian.projectkitten;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LaunchscreenActivity extends AppCompatActivity {

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launchscreen);

        login = (Button) findViewById(R.id.button_login);

        // Update custom fonts
        ((TextView) findViewById(R.id.title)).setTypeface(
                Typeface.createFromAsset(getAssets(), getString(R.string.font_supertitle)));
        ((TextView) findViewById(R.id.developedby)).setTypeface(
                Typeface.createFromAsset(getAssets(), getString(R.string.font_title)));
        login.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.font_main)));

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LaunchscreenActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageActivity.closeConnection();
    }
}
