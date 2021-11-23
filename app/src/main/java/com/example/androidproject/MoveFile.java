package com.example.androidproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MoveFile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_file_layout);

        String path = getIntent().getStringExtra("path");
        Bundle bundle = new Bundle();
        bundle.putInt("Activity",getIntent().getIntExtra("Activity",0));
        bundle.putString("path", path);
        bundle.putString("SelectedFile", getIntent().getStringExtra("SelectedFile"));
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.frame_layout_container, MyMainFragment.class, bundle)
                .commit();
    }

}