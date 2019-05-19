package com.example.swipeeditlionheart;

import android.app.ProgressDialog;
import android.content.Intent;
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

    private Button loginButton;
    private Button localImagesButton;

    ProgressDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        dialog = new ProgressDialog(LoginActivity.this);

        // my custom Facebook button
        loginButton = findViewById(R.id.fb);
        // local store images
        localImagesButton = findViewById(R.id.app_images);

    }

    public void onClickFacebookButton(View view) {
        if (view == loginButton) {
            AccessToken.setCurrentAccessToken(
                    new AccessToken(getString(R.string.fb_accessToken),getString(R.string.facebook_app_id),getString(R.string.fb_user_id), null,
                            null,
                            AccessTokenSource.TEST_USER,
                            new Date(System.currentTimeMillis() + 43200),
                            new Date(43200), new Date(43200)));

            FindProfileDirectory();

        }
    }

    public void onClickLocalImages(View view)
    {
        if (view == localImagesButton)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("bLocalImages", true);
            this.startActivity(intent);
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

                        String albumID;
                        try {
                            JSONObject json = response.getJSONObject();
                            JSONArray jarray = json.getJSONArray("data");
                            if (jarray != null) {
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
