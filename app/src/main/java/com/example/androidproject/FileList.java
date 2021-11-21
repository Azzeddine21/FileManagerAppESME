package com.example.androidproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class FileList extends Fragment {

    private File root;
    private MyAdapter adapter;

    public FileList() {
        // Required empty public constructor
        super(R.layout.fragment_file_list);
    }

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        // Inflate the layout for this fragment

        if(requireArguments().getBoolean("MoveFile")){
            view = inflater.inflate(R.layout.fragment_move_file, container, false);
            Button cancel = view.findViewById(R.id.cancel_button);
            Bundle bundle = new Bundle();
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
            Button ok = view.findViewById(R.id.ok_button);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File from = new File(requireArguments().getString("SelectedFile"));
                    File to = new File(root.getAbsolutePath() + "/" + from.getName());
                    Log.d("from", from.getAbsolutePath());
                    Log.d("to", to.getAbsolutePath());
                    from.renameTo(to);
                    adapter.updatefilesAndFolders();
                    getActivity().finish();
                }
            });
        }
        else{
            view = inflater.inflate(R.layout.fragment_file_list, container, false);
            View CreateFolder = view.findViewById(R.id.add_Folder);
            CreateFolder.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getRootView().getContext());
                    alertDialog.setTitle("Folder Name");
                    EditText editText = new EditText(getContext());
                    alertDialog.setView(editText);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    editText.requestFocus();
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String newPath = root.getAbsolutePath() + "/" + editText.getText().toString();
                            File newFile = new File(newPath);
                            Log.d("New folder path", newPath);
                            if (!newFile.exists()) {
                                boolean created = newFile.mkdirs();
                                Log.d("EditText", String.valueOf(created));
                                adapter.updatefilesAndFolders();
                            }
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.create()
                            .show();
                }
            });
        }
        TextView noFilesText = view.findViewById(R.id.nofiles_textview);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        root = new File(requireArguments().getString("path"));
        adapter = new MyAdapter(getContext(), root,requireArguments().getBoolean("MoveFile"),requireArguments().getString("SelectedFile"));
        recyclerView.setAdapter(adapter);
        if(root.listFiles()==null || root.listFiles().length ==0){
            noFilesText.setVisibility(View.VISIBLE);
        }
        noFilesText.setVisibility(View.INVISIBLE);
        return view;
    }
}