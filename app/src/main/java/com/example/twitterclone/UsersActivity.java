package com.example.twitterclone;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // co będzie jak klikniemy. To będzie dodawanie osób, które followujemy
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view; // sprawdzamy czy jest zaznaczony check. Do view jest przekazywany stan listy przecież.
                if (checkedTextView.isChecked()) {
                    ParseUser.getCurrentUser().add("isFollowing", users.get(i)); //dodajemy do tablicy userów, których followujemy
                } else { // usuwamy userów z followowanych. To jest bardziej skomplikowane niż dodawanie
                    Objects.requireNonNull(ParseUser.getCurrentUser().getList("isFollowing")).remove(users.get(i)); // usuwamy usera
                    List tempUsers = ParseUser.getCurrentUser().getList("isFollowing");  // dodajemy do tymczasowej listy userów obecną liste userów
                    ParseUser.getCurrentUser().remove("isFollowing"); // usuwamy całą listę
                    assert tempUsers != null;
                    ParseUser.getCurrentUser().put("isFollowing", tempUsers); // dodajemy od nowa listę temp (tą bez usera)
                }
                ParseUser.getCurrentUser().saveInBackground(); // zapisujemy stan. Czy to dodanych userów czy to usuniętych
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery(); // wyciąganie userów (z wbudowanej klasy User)
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername()); // z wyjątkiem obecnie zalogowanego usera.
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) { // jeśli nie ma błędów i są obiekty jakieś w klasie
                    for (ParseUser user : objects) { // loopujemy przez te obiekty
                        users.add(user.getUsername()); // i dodajemy userów do naszej arraylisty
                    }
                    arrayAdapter.notifyDataSetChanged(); // nowa komenda. Updatuje zmiany w adapterze
                    for (String username : users) {  // przy wyświetleniu userów musimy wyświetlić zaznaczenia przy followowanych userach. Żeby przy kolejnym zalogowaniu było pokazane
                        if (Objects.requireNonNull(ParseUser.getCurrentUser().getList("isFollowing")).contains(username)) { // sprawdzamy czy dany user zawiera w tablicy isFollowing konkretnego usera
                            usersListView.setItemChecked(users.indexOf(username), true); // jeśli tak to dajemy true
                        }
                    }
                }
            }
        });
    }
}
