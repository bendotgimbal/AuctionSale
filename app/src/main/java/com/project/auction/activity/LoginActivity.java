package com.project.auction.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.project.auction.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

  Button btnLogin;
  private EditText name;
  private EditText password;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }

    btnLogin = findViewById(R.id.btnLogin);
    btnLogin.setOnClickListener(this);
    name = findViewById(R.id.edtName);
    password = findViewById(R.id.edtPassword);
  }

  @SuppressLint("NonConstantResourceId")
  @Override public void onClick(View v) {
    if (v.getId() == R.id.btnLogin) {
      if (!name.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
        loggedIn(name.getText().toString(), password.getText().toString());
      }
    }
  }

  private void loggedIn(String name, String password) {
    if (name.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {
      Toast.makeText(this, "Selamat anda berhasil login", Toast.LENGTH_SHORT).show();
      Intent login = new Intent(this, MainActivity.class);
      startActivity(login);
      finish();
    }
  }
}
