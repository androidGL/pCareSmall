package com.pcare.api.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.pcare.api.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    public void toFace(View view) {
        EditText text = findViewById(R.id.user_id);
        Intent intent = new Intent(this, MajorLookActivity.class);
        intent.putExtra("userid", text.getText().toString());
        switch (view.getTag().toString()) {
            case "verify":
                intent.putExtra("type", "verify");
                break;
            case "register":
                intent.putExtra("type", "register");
                break;

        }
        startActivity(intent);
    }

    public void toSpeak(View view) {
        startActivity(new Intent(this, MajorSpeakActivity.class));
    }
}
