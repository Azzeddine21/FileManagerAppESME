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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
    Button mSignInButton = MainActivity.mSignInButton, mSignOutButton = MainActivity.mSignOutButton;
    TextView pseudo2 = MainActivity.pseudo;



    public GoogleDrive(){}

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
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
                        pseudo2.setText(pseudoString);
                        mSignInButton.setVisibility(View.INVISIBLE);
                        pseudo2.setVisibility(View.VISIBLE);
                        mSignOutButton.setVisibility(View.VISIBLE);
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

}