package com.example.androidproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.regex.Pattern;

public class MyMainFragment extends Fragment implements MyNewAdapter.AdapterCallback{

    private File root;
    private MyNewAdapter adapter;
    private int Activity=0;
    final public int MoveFileRequest=2;
    final public int ImportFileRequest=3;
    Pattern JDFormat0 = Pattern.compile("\\d{2}-\\d{2} ");
    Pattern JDFormat1 = Pattern.compile("\\d{2} ");
    Pattern JDFormat2 = Pattern.compile("\\d{2}.\\d{2} ");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        Activity = requireArguments().getInt("Activity");
        switch (Activity){
            case 1:
                break;
            case MoveFileRequest:
                view = inflater.inflate(R.layout.fragment_move_file, container, false);
                Button cancel = view.findViewById(R.id.cancel_button);
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
                        String filename = removeTag(from).getName();
                        File to = fileNaming(new File(root.getAbsolutePath() + "/" + filename));
                        from.renameTo(to);
                        adapter.setFilesAndFolders();
                        getActivity().finish();
                    }
                });
                break;
            case ImportFileRequest:
                view = inflater.inflate(R.layout.fragment_move_file, container, false);
                cancel = view.findViewById(R.id.cancel_button);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                    }
                });
                ok = view.findViewById(R.id.ok_button);
                ok.setVisibility(View.GONE);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_file_list, container, false);
                View createFolder = view.findViewById(R.id.add_Folder);
                createFolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getRootView().getContext());
                        alertDialog.setTitle("Folder Name");
                        EditText editText = new EditText(getContext());
                        alertDialog.setView(editText);
                        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        editText.requestFocus();
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String newPath = root.getAbsolutePath() + "/" + editText.getText().toString();
                                File newFile = new File(newPath);
                                newFile = fileNaming(newFile);
                                if (!newFile.exists()) {
                                    newFile.mkdirs();
                                    adapter.setFilesAndFolders();
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
                View importFile = view.findViewById(R.id.add_File);
                importFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), MoveFile.class);
                        String path = Environment.getExternalStorageDirectory().getPath();
                        intent.putExtra("path",path);
                        intent.putExtra("Activity",ImportFileRequest);
                        intent.putExtra("SelectedFile",root.getAbsolutePath());
                        getContext().startActivity(intent);
                    }
                });

        }

        TextView noFilesText = view.findViewById(R.id.nofiles_textview);
        noFilesText.setVisibility(View.INVISIBLE);

        root = new File(requireArguments().getString("path"));
        adapter = new MyNewAdapter(getContext(),root, Activity);
        adapter.myAdapterCallback = this;

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        if(root.listFiles()==null || root.listFiles().length ==0){
            noFilesText.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void openDirectoryCallback(String directoryPath, int Activity) {
        Bundle bundle = new Bundle();
        bundle.putString("path", directoryPath);
        bundle.putInt("Activity", Activity);
        if (Activity==MoveFileRequest || Activity == ImportFileRequest){
            bundle.putString("SelectedFile",requireArguments().getString("SelectedFile"));
        }
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout_container, MyMainFragment.class, bundle)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void deleteFileCallback(File file) {
        if (file.isDirectory())
            for (File child : file.listFiles())
                deleteFileCallback(child);
        file.delete();
    }

    @Override
    public void moveFileCallback(File file) {
        Intent intent = new Intent(getContext(), MoveFile.class);
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + new MainActivity().rootFolder + "/";
        intent.putExtra("path",path);
        intent.putExtra("Activity",MoveFileRequest);
        intent.putExtra("SelectedFile",file.getAbsolutePath());
        getContext().startActivity(intent);
    }

    @Override
    public void renameFileCallback(File file) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Rename to");
        EditText editText = new EditText(getContext());
        String path = file.getAbsolutePath();
        final File oldfile = new File(path);
        editText.setText(removeTag(oldfile).getName());
        alertDialog.setView(editText);
        editText.requestFocus();
        String tag = oldfile.getName().replace(removeTag(oldfile).getName(),"");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newPath = oldfile.getParentFile().getAbsolutePath() + "/" + tag + editText.getText().toString();
                File newFile = new File(newPath);
                boolean renamed = oldfile.renameTo(newFile);
                if (renamed){
                    Toast.makeText(getContext(),"File Renamed", Toast.LENGTH_SHORT);
                } else{
                    Toast.makeText(getContext(),"Process Failed", Toast.LENGTH_SHORT);
                }
                adapter.setFilesAndFolders();
                dialogInterface.dismiss();
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

    @Override
    public void selectFileCallback(File file) {
        File from = new File(root.getAbsolutePath() + "/" + file.getName());
        File to = new File(requireArguments().getString("SelectedFile") + "/" + file.getName());
        Log.d("from",from.getAbsolutePath());
        Log.d("to",to.getAbsolutePath());
        from.renameTo(to);
        adapter.setFilesAndFolders();
        getActivity().finish();
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

    private File fileNaming(File file){
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

    private File removeTag(File file){
        if(JDFormat0.matcher(file.getName().substring(0,6)).matches()){
            return new File(file.getParentFile().getAbsolutePath() + "/" + file.getName().substring(6,file.getName().length()));
        }else if (JDFormat1.matcher(file.getName().substring(0,3)).matches()){
            return new File(file.getParentFile().getAbsolutePath() + "/" + file.getName().substring(3,file.getName().length()));
        }else if (JDFormat2.matcher(file.getName().substring(0,6)).matches()){
            return new File(file.getParentFile().getAbsolutePath() + "/" + file.getName().substring(6,file.getName().length()));
        }else{
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
