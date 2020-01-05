package com.example.twitterclone;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class UsersActivity extends AppCompatActivity {

    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView usersListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        setTitle("User List");

        usersListView = findViewById(R.id.usersListView);
        usersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE); // nowość. wielokrotny wybór jakiś

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, users); // znowu nowość, item check list

        usersListView.setAdapter(arrayAdapter); // trzeba włączyć adapter

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // co będzie jak klikniemy
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view; // sprawdzamy czy jest zaznaczony check. Do view jest przekazywany stan listy przecież.
                if (checkedTextView.isChecked()) {
                    Toast.makeText(UsersActivity.this, "CHECKED!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UsersActivity.this, "NOT CHECKED!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery(); // wyciąganie userów (z wbudowanej klasy User)
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername()); // z wyjątkiem obecnie zalogowanego usera.
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0){ // jeśli nie ma błędów i są obiekty jakieś w klasie
                    for(ParseUser user : objects){ // loopujemy przez te obiekty
                        users.add(user.getUsername()); // i dodajemy userów do naszej arraylisty
                    }
                arrayAdapter.notifyDataSetChanged(); // nowa komenda. Updatuje zmiany w adapterze
                }
            }
        });
    }
}
