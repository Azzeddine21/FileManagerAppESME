package com.example.androidproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.regex.Pattern;

public class FileList extends Fragment implements MyAdapter.AdapterCallback {

    private File root;
    private MyAdapter adapter;
    Pattern JDFormat0 = Pattern.compile("\\d{2}-\\d{2} ");
    Pattern JDFormat1 = Pattern.compile("\\d{2} ");
    Pattern JDFormat2 = Pattern.compile("\\d{2}.\\d{2} ");

    @Override
    public void onMethodCallback(File file) {
        Log.d("Callback",file.getAbsolutePath());
        getActivity().finish();
    }

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
            ok.setVisibility(View.GONE);
            if (!requireArguments().getBoolean("ImportFile")){
                Log.d("Not ImportFile","true");
                ok.setVisibility(View.VISIBLE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File to;
                        File from = new File(requireArguments().getString("SelectedFile"));
                        Log.d("substring",from.getName().substring(0,5));

                        if(JDFormat0.matcher(from.getName().substring(0,6)).matches()){
                            Log.d("JDFormat","0");
                            to = fileNaming(new File(root.getAbsolutePath() + "/" + from.getName().substring(6,from.getName().length())));
                        }else if (JDFormat1.matcher(from.getName().substring(0,3)).matches()){
                            Log.d("JDFormat","1");
                            to = fileNaming(new File(root.getAbsolutePath() + "/" + from.getName().substring(3,from.getName().length())));
                        }else if (JDFormat2.matcher(from.getName().substring(0,6)).matches()){
                            Log.d("JDFormat","2");
                            to = fileNaming(new File(root.getAbsolutePath() + "/" + from.getName().substring(6,from.getName().length())));
                        }else{
                            Log.d("JDFormat","-1");
                            to = fileNaming(new File(root.getAbsolutePath() + "/" + from.getName()));
                        }
                        from.renameTo(to);
                        adapter.updatefilesAndFolders();
                        getActivity().finish();
                    }
                });
            }

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
                            newFile = fileNaming(newFile);
                            if (!newFile.exists()) {
                                boolean created = newFile.mkdirs();
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
            View addFile = view.findViewById(R.id.add_File);
            addFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MoveFile.class);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path",path);
                    intent.putExtra("ImportFile","true");
                    getContext().startActivity(intent);
                }
            });
        }

        TextView noFilesText = view.findViewById(R.id.nofiles_textview);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        root = new File(requireArguments().getString("path"));
        adapter = new MyAdapter(getContext(), root,requireArguments().getBoolean("MoveFile"),requireArguments().getString("SelectedFile"));
        adapter.mAdapterCallback = this;
        recyclerView.setAdapter(adapter);
        if(root.listFiles()==null || root.listFiles().length ==0){
            noFilesText.setVisibility(View.VISIBLE);
        }
        noFilesText.setVisibility(View.INVISIBLE);
        return view;
    }

    public int depth(File file,int _count){
        int count = _count;
        if (file.getAbsolutePath().contains(new MainActivity().rootFolder)){
            if (file.getParentFile().getName().equals(new MainActivity().rootFolder)){
                return count;
            }else{
                count++;
                return depth(file.getParentFile(),count);
            }
        }else{
            return -1;
        }
    }

    public File fileNaming(File file){
        int length = file.getParentFile().list().length;
        String fileName = file.getName();
        File parentFile = file.getParentFile();
        String parentPath = parentFile.getAbsolutePath() + "/";
        switch (depth(file,0)){
            case 0:
                if(length>10){
                    onError();
                    return file;
                }
                return new File(parentPath + String.valueOf(length) + "0-" + String.valueOf(length) +"9 " +  fileName);
            case 1:
                if(length>10){
                    onError();
                    return file;
                }
                return new File(parentPath + parentFile.getName().charAt(0) + String.valueOf(length) + " " + fileName);
            case 2:
                if (length>100){
                    onError();
                }
                return new File(parentPath + parentFile.getName().substring(0,2) + "." + String.format("%02d", length) + " " + fileName);
            default:
                return file;
        }
    }

    private void onError(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getView().getRootView().getContext());
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("The number of items is above the Johny Decimal Limits\nThe Folder won't have any number");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
}