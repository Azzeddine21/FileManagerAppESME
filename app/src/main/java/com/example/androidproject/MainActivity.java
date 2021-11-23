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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    final public String rootFolder = "AndroidProject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String path;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkPermission()){
            //permission allowed
            File rootDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "AndroidProject");
            if (!rootDirectory.exists()){
                boolean created = rootDirectory.mkdirs();
                if (created){path = rootDirectory.getAbsolutePath();}
                else path = Environment.getExternalStorageDirectory().getPath();
            }else path = rootDirectory.getAbsolutePath();
            Log.d("path",path);
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.frame_layout_container, MyMainFragment.class, bundle)
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

}