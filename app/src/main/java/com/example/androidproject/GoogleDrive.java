package com.example.androidproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;

public class GoogleDrive extends AppCompatActivity {
    DriveServiceHelper mDriveServiceHelper;
    String path;
    SignInButton mSignInButton = AccountFragment.mSignInButton;
    Button mSignOutButton = AccountFragment.mSignOutButton, changeAccount = AccountFragment.changeAccount;
    TextView pseudo2 = AccountFragment.pseudo;
    LinearLayout mLinearLayout2 = AccountFragment.mLinearLayout;
    String pseudoString;
    Uri profile;
    ImageView profileImage2 = AccountFragment.profileImage;

    SharedPreferences mSharedPreferences2;
    SharedPreferences.Editor mEditor2;




    public GoogleDrive(){}

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        mSharedPreferences2 = getApplicationContext().getSharedPreferences("saveLogin", MODE_PRIVATE);
        mEditor2 = mSharedPreferences2.edit();
        requestSignin();

    }


    public void requestSignin() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
        System.out.println("!!");
        someActivityResultLauncher.launch(client.getSignInIntent());

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        System.out.println("!!!");
                        handleSignInIntent(data);
                    }
                }
            }
    );



    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        GoogleAccountCredential credential = GoogleAccountCredential
                                .usingOAuth2(GoogleDrive.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        String pseudoString = googleSignInAccount.getDisplayName();

                        pseudoString = googleSignInAccount.getDisplayName();
                        profile = googleSignInAccount.getPhotoUrl();
                        if(profile == null){
                            mEditor2.putString("imageView", null);
                            profileImage2.setImageResource(R.drawable.blank_profile);
                        }
                        mEditor2.putString("pseudo", pseudoString);
                        mEditor2.putBoolean("save", true);
                        mEditor2.commit();;


                        Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("My Drive")
                                .build();

                        mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                        uploadFile();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void uploadFile() {
        ProgressDialog progressDialog = new ProgressDialog(GoogleDrive.this);
        progressDialog.setTitle("Uploading to Google Drive");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mDriveServiceHelper.createFile(path).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                progressDialog.dismiss();
                finish();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Check yout google Drive Key", Toast.LENGTH_LONG).show();
                        finish();

                    }
                });
    }

    private void removeAccountInformation() {
        mSignInButton.setVisibility(View.VISIBLE);
        mLinearLayout2.setVisibility(View.INVISIBLE);
        pseudo2.setVisibility(View.INVISIBLE);
        pseudo2.setText("");
        mSignOutButton.setVisibility(View.INVISIBLE);
        changeAccount.setVisibility(View.INVISIBLE);
    }

    private void revealAccountInformation() {
        mSignInButton.setVisibility(View.INVISIBLE);
        mLinearLayout2.setVisibility(View.VISIBLE);
        pseudo2.setVisibility(View.VISIBLE);
        profileImage2.setVisibility(View.VISIBLE);
        mSignOutButton.setVisibility(View.VISIBLE);
        changeAccount.setVisibility(View.VISIBLE);
    }


}