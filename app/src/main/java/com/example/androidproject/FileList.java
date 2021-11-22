package com.example.androidproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Arrays;

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

        if(requireArguments().getBoolean("move_file")){
            view = inflater.inflate(R.layout.fragment_move_file, container, false);
            View Cancel = view.findViewById(R.id.cancel_button);
            Cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("path", requireArguments().getString("path"));
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    FileList nextFrag= new FileList();
                    for(int i=0;i< activity.getSupportFragmentManager().getBackStackEntryCount(); i++){
                        activity.getSupportFragmentManager().popBackStack();
                    }
                    //activity.getSupportFragmentManager().beginTransaction()
                     //       .replace(R.id.frame_layout_container, FileList.class, bundle)
                       //     .commit();
                }
            });
            View ok = view.findViewById(R.id.ok_button);
            ok.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().popBackStack("moveFileTag", getActivity().getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                    Toast.makeText(view.getContext(), "OK", Toast.LENGTH_SHORT);
                }
            });

        }else{
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
        adapter = new MyAdapter(getContext(), root);
        if (requireArguments().getBoolean("move_file")){
            adapter.setmovingfile(true);
        }
        recyclerView.setAdapter(adapter);
        if(root.listFiles()==null || root.listFiles().length ==0){
            noFilesText.setVisibility(View.VISIBLE);
        }
        noFilesText.setVisibility(View.INVISIBLE);
        Log.d("Files", Arrays.toString(root.listFiles()));
        return view;
    }

}