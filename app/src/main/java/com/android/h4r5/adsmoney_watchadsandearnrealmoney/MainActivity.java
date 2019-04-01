package com.android.h4r5.adsmoney_watchadsandearnrealmoney;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity  {
//    FirebaseAuth mAuth2;
//    GoogleSignInAccount acct;
   // private RewardedVideoAd mRewardedVideoAd;
   // private InterstitialAd mInterstitialAd;
    FirebaseAuth mAuth;
    AdRequest AdLoder;
    TextView Money_text;
    public static int money_data;



    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
//            mAuth =FirebaseAuth.getInstance();
//            String user_uid = mAuth.getUid();
//            Money_text = findViewById(R.id.coins_value);
//            FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
//            DatabaseReference myRef3 = mdatabase.getReference("mApp_users");
//            DatabaseReference myRef4 = myRef3.child(user_uid).child("Money");
//            myRef4.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String money_value = dataSnapshot.getValue(String.class);
//                    money_data = Integer.valueOf(money_value);
//                    Money_text.setText(money_value + " Coins");
//                    Toast.makeText(MainActivity.this,money_value,Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        }
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInAccount acct3 = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

        //Ads///

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        ////////////////////

        Money_text = findViewById(R.id.coins_value);
        ///database//////
        mAuth = FirebaseAuth.getInstance();
        String user_Uid = mAuth.getUid();
        //assert user_Uid != null;


        if (acct3 != null) {
            mAuth =FirebaseAuth.getInstance();
            String user_uid = mAuth.getUid();
            Money_text = findViewById(R.id.coins_value);
            FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef3 = mdatabase.getReference("mApp_users");
            DatabaseReference myRef4 = myRef3.child(user_uid).child("Money");
            myRef4.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String money_value = dataSnapshot.getValue(String.class);
                    money_data = Integer.valueOf(money_value);
                    Money_text.setText(money_value + " Coins");
                    Toast.makeText(MainActivity.this,money_value,Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        //////////

        Money_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushFragment(new payment());
            }
        });



        CircleImageView header_img = findViewById(R.id.header_image);

        if (acct3 != null) {

            Uri personPhoto2 = acct3.getPhotoUrl();
            new DownloadImageTask(header_img)
                    .execute(String.valueOf(personPhoto2));
        }

        header_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushFragment(new profile());
            }
        });

        setupNavigationView();
    }



    private void setupNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        if (bottomNavigationView != null) {

            // Select first menu item by default and show Fragment accordingly.
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));

            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    private void selectFragment(MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.navigation_home:
                // Action to perform when Home Menu item is selected.
                pushFragment(new home());
                break;
            case R.id.navigation_payment:
                // Action to perform when Bag Menu item is selected.
                pushFragment(new payment());
                break;
            case R.id.navigation_profile:
                // Action to perform when Account Menu item is selected.
                pushFragment(new profile());
                break;
        }

    }
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }




}
