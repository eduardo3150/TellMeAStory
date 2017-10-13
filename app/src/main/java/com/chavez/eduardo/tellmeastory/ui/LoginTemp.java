package com.chavez.eduardo.tellmeastory.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginTemp extends AppCompatActivity {

    @BindView(R.id.ip_input)
    EditText input;

    @BindView(R.id.ip_saver)
    Button button;
    private boolean loggedIn =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_temp);
        ButterKnife.bind(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input.getText().toString().isEmpty()){
                    String value = "http://"+ input.getText().toString()+":82";
                    SharedPreferences sharedPreferences = getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(ConfigurationUtils.IP_SET_KEY, true);
                    editor.putString(ConfigurationUtils.IP_VALUE_KEY, value);
                    editor.apply();
                    Intent intent = new Intent(LoginTemp.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
        loggedIn = sharedPreferences.getBoolean(ConfigurationUtils.IP_SET_KEY, false);
        if (loggedIn){
            startActivity(new Intent(LoginTemp.this, MainActivity.class));
            LoginTemp.this.finish();
        }
    }
}
