package com.example.androidproject;

import static com.example.androidproject.MainActivity.mSignInButton;
import static com.example.androidproject.MainActivity.mSignOutButton;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AccountFragment extends Fragment {
    public static SignInButton mSignInButton;
    public static Button mSignOutButton, changeAccount;
    public static TextView pseudo;
    LinearLayout mLinearLayout;
    GoogleSignInClient mGoogleSignInClient;
    String pseudoString;
    ImageView profileImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        mSignInButton = (SignInButton) view.findViewById(R.id.signin);
        mSignOutButton = (Button) view.findViewById(R.id.signout);
        pseudo = (TextView) view.findViewById(R.id.pseudo);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.infoaccount);
        profileImage = (ImageView) view.findViewById(R.id.profileimage);
        changeAccount = (Button) view.findViewById(R.id.changeaccount);
        System.out.println(getTag());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()){
                    case R.id.signin:
                        signIn();
                        break;
                }
            }
        });

        changeAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "User SignOut", Toast.LENGTH_SHORT).show();

                            }
                        });
                signIn();
            }
        });

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.signout:
                        signOut();
                        break;

                }

            }
        });

        return view;
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "User SignOut", Toast.LENGTH_SHORT).show();
                        mSignInButton.setVisibility(View.VISIBLE);
                        mLinearLayout.setVisibility(View.INVISIBLE);
                        pseudo.setVisibility(View.INVISIBLE);
                        pseudo.setText("");
                        mSignOutButton.setVisibility(View.INVISIBLE);
                        changeAccount.setVisibility(View.INVISIBLE);


                    }
                });
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        System.out.println("!!!");
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    }
                }
            }
    );
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            pseudoString = account.getDisplayName();
            Uri profile = account.getPhotoUrl();
            Toast.makeText(getContext(), "Sign-in successful", Toast.LENGTH_SHORT).show();
            mSignInButton.setVisibility(View.INVISIBLE);
            mLinearLayout.setVisibility(View.VISIBLE);
            pseudo.setVisibility(View.VISIBLE);
            pseudo.setText(pseudoString);
            profileImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(profile).into(profileImage);
            mSignOutButton.setVisibility(View.VISIBLE);
            changeAccount.setVisibility(View.VISIBLE);


        } catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }
}