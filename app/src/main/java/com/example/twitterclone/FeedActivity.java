package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setTitle("Your Feed");

        final ListView listView = findViewById(R.id.listView); // na tej liście będą zarówno tweety jak i autorzy

        final List<Map<String, String>> tweetData = new ArrayList<>();   // mamy listę map, które zawierają klucz wartość czyli tweet i autor

//        ///// przykład /////
//        for (int i = 0; i <= 5; i++) {
//            Map<String, String> tweetInfo = new HashMap<>();
//            tweetInfo.put("content", "Tweet content " + i);
//            tweetInfo.put("username", "User " + i);
//            tweetData.add(tweetInfo);
//        }
//        SimpleAdapter simpleAdapter = new SimpleAdapter(this, tweetData, android.R.layout.simple_list_item_2, new String[]{"content", "username"}, new int[]{android.R.id.text1, android.R.id.text2}); // simpleadapter dziwne... i tym razem simple list item 2. Sublista jest więc 2 texty. Powalone.
//        listView.setAdapter(simpleAdapter);
//        ///// koniec przykładu /////

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet"); // zapytania z klasy Tweet
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing")); // dotyczące tych, których followujemy
        query.orderByDescending("createdAt"); // malejąco po dacie utworzenia (najnowsze u góry)
        query.setLimit(30); // może być dużo tweetów więc ograniczę do 30.

        query.findInBackground(new FindCallback<ParseObject>() { // wykonujemy zapytanie. Find bo dla wszystkich elemeentów a nie dla jednego
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) { // podobnie jak w przykłądzie, uzupełniamy mapę zawartością tweeta i usera i dodajemy mapę do listy.
                    for (ParseObject tweet : objects) {
                        Map<String, String> tweetInfo = new HashMap<>();
                        tweetInfo.put("content", Objects.requireNonNull(tweet.getString("tweet")));
                        tweetInfo.put("username", Objects.requireNonNull(tweet.getString("username")));
                        tweetData.add(tweetInfo);
                    }
                    SimpleAdapter simpleAdapter = new SimpleAdapter(FeedActivity.this, tweetData, android.R.layout.simple_list_item_2, new String[]{"content", "username"}, new int[]{android.R.id.text1, android.R.id.text2}); // simpleadapter dziwne... i tym razem simple list item 2. Sublista jest więc 2 texty. Powalone.
                    listView.setAdapter(simpleAdapter);
                }
            }
        });

    }
}
