package com.example.swipeeditlionheart;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    ProgressDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        dialog = new ProgressDialog(LoginActivity.this);

        //my custom Facebook button
        loginButton = (Button) findViewById(R.id.fb);

    }

    public void onClickFacebookButton(View view) {
        if (view == loginButton) {
            AccessToken.setCurrentAccessToken(
                    new AccessToken(getString(R.string.fb_accessToken),getString(R.string.facebook_app_id),getString(R.string.fb_user_id), null,
                            null,
                            AccessTokenSource.TEST_USER,
                            new Date(System.currentTimeMillis() + 100000000 * 60 * 60 * 5),
                            new Date(), null));

            FindProfileDirectory();

        }
    }

    private void FindProfileDirectory()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/albums",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        String albumID = null;
                        try {
                            JSONObject json = response.getJSONObject();
                            JSONArray jarray = json.getJSONArray("data");
                            if (json != null) {
                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject oneAlbum = jarray.getJSONObject(i);
                                    //get albums id
                                    if (oneAlbum.getString("name").equals("Profile Pictures")) {
                                        albumID = oneAlbum.getString("id");
                                        getPhotos(albumID);
                                    }
                                }
                            }
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
        request.executeAsync();
    }

    private void getPhotos(String photoUrl)
    {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "images");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + photoUrl + "/photos",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        try {
                            JSONObject json = response.getJSONObject();
                            JSONArray jarray = json.getJSONArray("data");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject image = jarray.getJSONObject(i);
                                JSONArray Images = image.getJSONArray("images");
                                //get albums id
                                if (Images.length() >0) {
                                    String imagePath = Images.getJSONObject(0).getString("source");
                                    new MyAsyncTask(LoginActivity.this, LoginActivity.this).execute(imagePath);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }).executeAsync();
    }
}
