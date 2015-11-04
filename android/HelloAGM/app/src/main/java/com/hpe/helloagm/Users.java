package com.hpe.helloagm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

// a simple activity that displays a scrollable list of users
public class Users extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // extract the list of names from the intent that
        Intent intent = this.getIntent();
        ArrayList<String> names = intent.getStringArrayListExtra("names");

        // create the view from the layout xml
        setContentView(R.layout.activity_users);

        // get the UI element and setup an adapter to hold the data to view
        ListView usersView = (ListView) findViewById(R.id.usersView);
        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        usersView.setAdapter(listAdapter);

    }

}
