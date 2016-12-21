/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.marcus.groupon_one.Firebase;

import android.util.Log;
import android.widget.Toast;

import com.example.marcus.groupon_one.Config.DatabaseVariable;
import com.example.marcus.groupon_one.Database.PostProcessor;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        //send token to your app server.
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(DatabaseVariable.REGISTRATION_TOKEN, token);
            String jsonString = PostProcessor.load(new URL(DatabaseVariable.INSERT_FIREBASE_REGISTRATION_TOKENS_URL), params);

            //parse json string into deal object
            JSONObject jo = new JSONObject(jsonString);

            //if no error do nothing, else toast message
            if(!jo.getBoolean(DatabaseVariable.ERROR)) {
                Log.d(TAG, jo.getString(DatabaseVariable.MESSAGE) + ": uploaded registration token to database");
            } else {
                Toast.makeText(MyFirebaseInstanceIDService.this, jo.getString(DatabaseVariable.MESSAGE), Toast.LENGTH_SHORT).show();
                Log.e(TAG, jo.getString(DatabaseVariable.MESSAGE));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
