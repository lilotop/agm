package com.hpe.helloagm;


import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AgmApi {
    public static class Defaults {

        // fill this in with the info you'll get from us when the Hackathon starts
        public static String ServerUrl = "";

        // fill this in with the info you'll get from us when the Hackathon starts
        public static String ClientID = "";

        // fill this in with the info you'll get from us when the Hackathon starts
        public static String ClientSecret = "";

        // first workspace ID is 1000, but this may change, make sure you follow the documentation.
        public static String WorkspaceId = "1000";
    }

    private static AgmApi instance;

    private String token, serverUrl, clientId, clientSecret;

    public static AgmApi getInstance() {
        if (instance == null) {
            instance = new AgmApi();
        }
        return instance;
    }

    private AgmApi() {
    }

    public void login(String serverUrl, String clientId, String clientSecret) throws IOException, JSONException {
        this.serverUrl = serverUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.token = getToken();
    }

    public JSONArray getTeamMembers(String workspaceId) throws JSONException, IOException {

        HttpGet request = new HttpGet(serverUrl + "api/workspaces/" + workspaceId + "/team_members");
        createAuthHeaders(request, token);

        // create the client and execute the server call
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);

        // the status code is the http status code. general list here: http://httpstatus.es/
        int statusCode = response.getStatusLine().getStatusCode();

        // failed to get the token, crash the party!
        if (statusCode != 200){
            throw new IOException("Failed to get team members, error " + statusCode);
        }

        // otherwise, we got the token, convert it to a string
        String responseAsString = EntityUtils.toString(response.getEntity());

        // convert the string to a json object for easy access to the attributes inside it
        JSONObject jsonObject = new JSONObject(responseAsString);

        // return the resulting data.
        return jsonObject.getJSONArray("data");
    }

    // just a simple helper method that adds the token and other needed headers to a given request
    private void createAuthHeaders(HttpGet request, String token) {
        request.addHeader("Authorization", "bearer " + token);
        request.addHeader("Content-Type", "application/json");
    }

    private String getToken() throws IOException, JSONException {

        // create credentials data to be sent to the server
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nameValuePair.add(new BasicNameValuePair("client_id", clientId));
        nameValuePair.add(new BasicNameValuePair("client_secret", clientSecret));

        // create the POST configuration object and load the credentials into it
        HttpPost httpPost = new HttpPost(serverUrl + "oauth/token");
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

        // create an http client to handle the server calls
        HttpClient httpClient = new DefaultHttpClient();

        // this is the actual call to server, it is executed synchronously
        HttpResponse response = httpClient.execute(httpPost);

        // the status code is the http status code. general list here: http://httpstatus.es/
        int statusCode = response.getStatusLine().getStatusCode();

        // failed to get the token, crash the party!
        if (statusCode != 200){
            throw new IOException("Failed to get token, error " + statusCode);
        }

        // otherwise, we got the token, convert it to a string
        String responseAsString = EntityUtils.toString(response.getEntity());

        // convert the string to a json object for easy access to the attributes inside it
        JSONObject jsonObject = new JSONObject(responseAsString);

        // return the resulting token. this token will be used in all subsequent calls to AGM
        return (String) jsonObject.get("access_token");

    }

}
