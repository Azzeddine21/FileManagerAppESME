package com.example.androidproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MoveFile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_file_layout);
        getSupportActionBar().hide();
        String path = getIntent().getStringExtra("path");
        Bundle bundle = new Bundle();
        bundle.putInt("Activity",getIntent().getIntExtra("Activity",new MyMainFragment().MoveFileRequest));
        bundle.putString("path", path);
        bundle.putString("SelectedFile", getIntent().getStringExtra("SelectedFile"));
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .add(R.id.frame_layout_container, MyMainFragment.class, bundle)
                .commit();
    }
}