package com.example.androidproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    private File[] filesAndFolders;
    private File root;
    private boolean MoveFile=false;
    private String SelectedFile="";

    public MyAdapter(Context context, File root){
        this.context = context;
        this.root = root;
        updatefilesAndFolders();
    }

    public MyAdapter(Context context, File root, boolean MoveFile, String SelectedFile){
        this(context,root);
        this.MoveFile = MoveFile;
        this.SelectedFile = SelectedFile;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());

        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        }else{
            holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedFile.isDirectory()){
                    Bundle bundle = new Bundle();
                    bundle.putString("path", selectedFile.getAbsolutePath());
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    if(MoveFile){
                        bundle.putBoolean("MoveFile",true);
                        bundle.putString("SelectedFile",SelectedFile);
                    }
                    FileList nextFrag= new FileList();
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout_container, FileList.class, bundle)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenu().add("DELETE");
                popupMenu.getMenu().add("MOVE");
                popupMenu.getMenu().add("RENAME");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("DELETE")){
                            boolean deleted = selectedFile.delete();
                            if(deleted){
                                Toast.makeText(context.getApplicationContext(),"DELETED ",Toast.LENGTH_SHORT).show();
                                //v.setVisibility(View.GONE);
                                updatefilesAndFolders();
                            }
                        }
                        if(item.getTitle().equals("MOVE")){
                            Intent intent = new Intent(v.getContext(), MoveFile.class);
                            String path = Environment.getExternalStorageDirectory().getPath();
                            intent.putExtra("path",path);
                            intent.putExtra("SelectedFile",selectedFile.getAbsolutePath());
                            ((Activity) context).startActivity(intent);
                        }
                        if(item.getTitle().equals("RENAME")){
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());
                            alertDialog.setTitle("Rename to");
                            EditText editText = new EditText(context);
                            String path = selectedFile.getAbsolutePath();
                            final File file =new File(path);
                            editText.setText(file.getName());
                            alertDialog.setView(editText);
                            editText.requestFocus();

                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String newPath = file.getParentFile().getAbsolutePath() + "/" + editText.getText().toString();
                                    File newFile = new File(newPath);
                                    boolean renamed = file.renameTo(newFile);
                                    if (renamed){
                                        updatefilesAndFolders();
                                        Toast.makeText(context,"File Renamed", Toast.LENGTH_SHORT);
                                    } else{
                                        Toast.makeText(context,"Process Failed", Toast.LENGTH_SHORT);
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
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesAndFolders.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
    //permet de chosiir l'image et le texte à afficher pour chaque fichier
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text_view);
            imageView = itemView.findViewById(R.id.icon_view);
        }
    }

    public void updatefilesAndFolders() {
        this.filesAndFolders = sort(root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isHidden();
            }
        }));
        notifyDataSetChanged();
    }

    private File[] sort(File[] _filesAndFolders){
        Arrays.sort(_filesAndFolders, new Comparator<File>() {
            public int compare(File file1, File file2) {
                if(file1.isDirectory()){
                    if (file2.isDirectory()){
                        return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
                    }else{
                        return -1;
                    }
                }else {
                    if (file2.isDirectory()){
                        return 1;
                    }else{
                        return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
                    }
                }

            }
        });
        Collections.reverse(Arrays.asList(_filesAndFolders));
        return _filesAndFolders;
    }

    public boolean move_file(String filePath){
        return root.renameTo(new File(filePath));
    }

}
