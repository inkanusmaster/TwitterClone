package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;

    public void redirectToUsers() { // przejście do listy userów (nowa aktywność)
        if (ParseUser.getCurrentUser() != null) { // jeśli ktoś jest zalogowany...
            Intent intent = new Intent(getApplicationContext(),UsersActivity.class); // ...przejdź do listy userów
            startActivity(intent); // no odpal intent
        }
    }

    public void signupLogin(View view) {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                    redirectToUsers(); // jeśli zalogowanie się powiodło to przechodzimy do listy userów
                } else {
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(usernameEditText.getText().toString());
                    newUser.setPassword(passwordEditText.getText().toString());
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(MainActivity.this, "Signup success!", Toast.LENGTH_SHORT).show();
                                redirectToUsers(); // po udanej rejestracji przechodzimy do listy userów
                            } else {                                                     // ten substring usuwa do spacji znaki, czyli kod bledu, dzieki czemu zostanie sam sensowny komunikat
                                Toast.makeText(MainActivity.this, e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
