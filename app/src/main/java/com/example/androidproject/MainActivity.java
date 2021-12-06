package com.example.androidproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    final public String rootFolder = "AndroidProject";
    public static Button mSignInButton, mSignOutButton;
    public static TextView pseudo;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String path;
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSignInButton = (Button) findViewById(R.id.signin);
        mSignOutButton = (Button) findViewById(R.id.signout);
        pseudo = (TextView) findViewById(R.id.pseudo);


        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);
        bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                String id = item.getTitle().toString();
                Bundle bundle;
                switch(id){
                    case "fichiers":
                        System.out.println("fichier");
                        bundle = new Bundle();
                        bundle.putString("path", pathRoot());
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .replace(R.id.frame_layout_container, MyMainFragment.class, bundle)
                                .commit();
                        break;
                    case "favoris":
                        System.out.println("favoris");
                        bundle = new Bundle();
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .replace(R.id.frame_layout_container, FavoriteFragment.class, bundle)
                                .commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        if(checkPermission()){
            //permission allowed
            Bundle bundle = new Bundle();
            bundle.putString("path", pathRoot());
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.frame_layout_container, FavoriteFragment.class, bundle)
                    .commit();

        }else{
            //permission not allowed
            requestPermission();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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


    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "User SignOut", Toast.LENGTH_SHORT).show();
                        mSignInButton.setVisibility(View.VISIBLE);
                        pseudo.setVisibility(View.INVISIBLE);
                        mSignOutButton.setVisibility(View.INVISIBLE);


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
            String pseudoString = account.getDisplayName();
            pseudo.setText(pseudoString);
            Toast.makeText(this, "Sign-in successful", Toast.LENGTH_SHORT).show();
                mSignInButton.setVisibility(View.INVISIBLE);
                pseudo.setVisibility(View.VISIBLE);
                mSignOutButton.setVisibility(View.VISIBLE);

            //updateMenu();


        } catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void updateMenu() {
        System.out.println("aaa");
        mSignInButton.setText("Connecté");

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }





    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this,"Storage permission is requires, please allow from settings",Toast.LENGTH_SHORT).show();
        }else
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
    }

    private String pathRoot(){
        String path = Environment.getExternalStorageDirectory().getPath() ;
        File rootDirectory = new File(path + "/" + "AndroidProject");
        if (rootDirectory.exists()){
            return rootDirectory.getAbsolutePath();
        }else{
            boolean created = rootDirectory.mkdirs();
            if (created){
                path = rootDirectory.getAbsolutePath();
                return path;
            }else {
                return path;
            }
        }
    }

}