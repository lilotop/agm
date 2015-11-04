package com.hpe.helloagm;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AgmApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create the view from the layout xml
        setContentView(R.layout.activity_main);

        // init the text fields with defaults
        // (the defaults are empty in this example for obvious security reasons)
        initTextView(R.id.server, AgmApi.Defaults.ServerUrl);
        initTextView(R.id.clientId, AgmApi.Defaults.ClientID);
        initTextView(R.id.clientSecret, AgmApi.Defaults.ClientSecret);
        initTextView(R.id.workspaceId, AgmApi.Defaults.WorkspaceId);

        // set a listener for the login button
        View btn = findViewById(R.id.loginBtn);
        btn.setOnClickListener(this);
    }

    // helper function for initializing a text view
    private void initTextView(int id, String text) {
        TextView view = (TextView) findViewById(id);
        view.setText(text);
    }

    // helper function for getting text from a text view
    private String getTextFromView(int id) {
        TextView view = (TextView) findViewById(id);
        return view.getText().toString();
    }


    // the button handler, since there's only a single button, we don't bother checking the origin
    @Override
    public void onClick(View v) {

        this.api = AgmApi.getInstance();

        // show the progress bar and hide the login button
        final View progressBar = findViewById(R.id.progressBar);
        final View loginBtn = findViewById(R.id.loginBtn);
        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.GONE);

        // this code will run asynchronously to keep the ui thread responsive
        Thread  thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // perform the login operation with data you got from the user (or defaults)
                    api.login(getTextFromView(R.id.server),
                            getTextFromView(R.id.clientId),
                            getTextFromView(R.id.clientSecret));


                    // get team members for the specified workspace
                    JSONArray teamMembers = api.getTeamMembers(getTextFromView(R.id.workspaceId));

                    // create an array list with just the names
                    final ArrayList<String> memberNames = new ArrayList<>();
                    for (int i = 0; i < teamMembers.length(); i++) {
                        JSONObject member = teamMembers.getJSONObject(i);
                        String name = member.getString("member_name");
                        memberNames.add(name);
                    }


                    // this method runs the given code on the UI thread (posts it on the queue)
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // hide the progress bar
                            progressBar.setVisibility(View.GONE);

                            // create an intent for starting the next activity
                            // the next activity will show the list of users
                            // so we need to add that list to the intent
                            Intent intent = new Intent(MainActivity.this, Users.class);
                            intent.putExtra("names", memberNames);
                            startActivity(intent);
                        }
                    });


                } catch (IOException | JSONException e) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialog(e.getMessage());
                            progressBar.setVisibility(View.GONE);
                            loginBtn.setVisibility(View.VISIBLE);
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        View progressBar = findViewById(R.id.progressBar);
        View loginBtn = findViewById(R.id.loginBtn);
        progressBar.setVisibility(View.GONE);
        loginBtn.setVisibility(View.VISIBLE);

    }

    private void showDialog(final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hello AGM");
        builder.setMessage(message);
        builder.create().show();

    }
}
