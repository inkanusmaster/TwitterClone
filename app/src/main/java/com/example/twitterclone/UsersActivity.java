package com.example.twitterclone;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UsersActivity extends AppCompatActivity {

    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView usersListView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // robimy menu w górnym prawym rogu
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.tweeet_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // co będzie jak ktos wybierze dana opcję z menu
        if (item.getItemId() == R.id.tweet) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // robimy tweeta alert dialogiem
            builder.setTitle("Send a tweet"); // nagłówek
            final EditText tweetEditText = new EditText(this); // pole edittext na tweet
            builder.setView(tweetEditText);
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() { // co będzie jak klikniemy Send
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (tweetEditText.getText().toString().isEmpty()) { // nie chcę pustych tweetów
                        Toast.makeText(UsersActivity.this, "Tweet cannot be empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        ParseObject tweet = new ParseObject("Tweet"); // zapisujemy tweety do klasy Tweet
                        tweet.put("tweet", tweetEditText.getText().toString()); // do kolumny tweet wpisujemy zawartość tweeta
                        tweet.put("username", ParseUser.getCurrentUser().getUsername()); // do kolumny username wpisujemy aktualnego usera
                        tweet.saveInBackground(new SaveCallback() { // zapisujemy z callbackiem oczywiście
                            @SuppressLint("Assert")
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    assert false;
                                    Toast.makeText(UsersActivity.this, "Tweet sent!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UsersActivity.this, e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // co będzie jak klikniemy Cancel
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); // wyłączamy dialog interface
                }
            });
            builder.show(); // pokazujemy alertdialog builder

        } else if (item.getItemId() == R.id.logout) {
            ParseUser.logOut(); // wylogowujemy się...
            Intent intent = new Intent(getApplicationContext(), MainActivity.class); // ...i wracamy do menu logowania
            startActivity(intent);
        } else if (item.getItemId() == R.id.viewFeed) { // wyświetlamy feed
            Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

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
