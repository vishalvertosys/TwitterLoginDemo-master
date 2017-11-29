package importcontact.com.twitterlogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {
    private TwitterLoginButton loginButton;
    private Button button, button1;
    private static final String TAG = "TwitterLogin";
    String token;
    String secret;

    // Create a static final TWITTER_KEY and TWITTER_SECRET using the values you retrieved from
    // the Twitter Application Management console. Just make sure you obfuscate this Key and
    // Secret from your source code before releasing your app

    private static final String TWITTER_KEY = "RIuPUZcXIeja8tWcpmS3Livwc";
    private static final String TWITTER_SECRET = "IlVupbW525OG0rUVdAsqAs6lyQ0wzmMtJ7akmGOtF5XpjRJo1G";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
        setContentView(R.layout.activity_main);

        // Get a shared instance of the FirebaseAuth object//
        mAuth = FirebaseAuth.getInstance();

        // Set up an AuthStateListener that responds to changes in the user's sign-in state//
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Retrieve the user’s account data, using the getCurrentUser method//
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // If the user signs in, then display the following message//
                    Log.d(TAG, "onAuthStateChanged" + user.getUid());
                }
            }
        };
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);

        // Create a callback that’ll handle the results of the login attempts//
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            // If the login is successful...//
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin" + result);
                handleTwitterSession(result.data);
                getUserAccount();
            }

            @Override
            // If the login attempt fails...//
            public void failure(TwitterException exception) {
                //Do something//
            }
        });

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PayOnlineActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // Pass the Activity result to the onActivityResult method//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    //Exchange the OAuth access token and OAuth secret for a Firebase credential//
    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);
        token = session.getAuthToken().token;
        secret = session.getAuthToken().secret;

        AuthCredential credential = TwitterAuthProvider.getCredential(
                token, secret);

        //If the call to signInWithCredential succeeds, then get the user’s account data//
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential" + task.isSuccessful());

                    }
                });

        //Toast.makeText(getApplicationContext(), "Token " + token, Toast.LENGTH_SHORT).show();
    }

    //getting the twitter account
    private void getUserAccount(){
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                // Do something with the result, which provides the email address
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }
}
