package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    final public String rootFolder = "AndroidProject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String path;
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);
        bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                String id = item.getTitle().toString();
                Bundle bundle;
                switch(id){
                    case "fichiers":
                        bundle = new Bundle();
                        bundle.putString("path", pathRoot());
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .replace(R.id.frame_layout_container, MyMainFragment.class, bundle)
                                .commit();
                        break;
                    case "favoris":
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