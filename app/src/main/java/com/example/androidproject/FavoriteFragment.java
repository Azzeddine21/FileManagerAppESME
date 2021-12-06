package com.example.androidproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class FavoriteFragment extends Fragment implements FavoriteAdapter.FavoriteAdapterCallback {

    private FavoriteAdapter adapter;
    final public int FavoriteFileRequest=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_display_file, container, false);

        TextView noFilesText = view.findViewById(R.id.nofiles_textview);
        noFilesText.setVisibility(View.INVISIBLE);

        adapter = new FavoriteAdapter(getContext());
        adapter.myFavoriteAdapterCallback = this;

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void openDirectoryCallback(String directoryPath, int Activity) {
        Bundle bundle = new Bundle();
        bundle.putString("path", directoryPath);
        bundle.putInt("Activity", FavoriteFileRequest);
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout_container, MyMainFragment.class, bundle)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void setToFavoriteCallback(File file, boolean checked) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("favFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(file.getAbsolutePath(),checked);
        editor.commit();
        adapter.updatefavFileList();
    }
}
