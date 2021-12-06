package com.example.androidproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MyNewAdapter extends RecyclerView.Adapter<MyNewAdapter.ViewHolder>{

    private Context context;
    private File root;
    private File[] filesAndFolders;
    public AdapterCallback myAdapterCallback;
    private int Activity=0;

    public MyNewAdapter(Context context, File root, int Activity){
        this.context = context;
        this.root = root;
        this.Activity = Activity;
        setFilesAndFolders();
    }


    public interface AdapterCallback{
        void openDirectoryCallback(String directoryPath,int Activity);
        void deleteFileCallback(File file);
        void moveFileCallback(File file);
        void renameFileCallback(File file);
        void forceRenameFileCallback(File file);
        void selectFileCallback(File file);
        void setToFavoriteCallback(File file, boolean checked);
        void sharetoDriveCallback(File file);
    }

    @Override
    public MyNewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNewAdapter.ViewHolder holder, int position) {

        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());

        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        }else{
            holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("favFile", Context.MODE_PRIVATE);
        boolean favFileToggleButton = sharedPreferences.getBoolean(selectedFile.getAbsolutePath(),false);

        if (favFileToggleButton){
            holder.favoriteToggleButton.setChecked(true);
        }else{
            holder.favoriteToggleButton.setChecked(false);
        }

        holder.favoriteToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.favoriteToggleButton.isChecked()){
                    Log.d("onClickSelected","true");
                    myAdapterCallback.setToFavoriteCallback(selectedFile, true);
                }else{
                    Log.d("onClickSelected","false");
                    myAdapterCallback.setToFavoriteCallback(selectedFile, false);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedFile.isDirectory()){
                    myAdapterCallback.openDirectoryCallback(selectedFile.getAbsolutePath(), Activity);
                }else if(Activity==new MyMainFragment().ImportFileRequest){
                    myAdapterCallback.selectFileCallback(selectedFile);
                }else{
                    Intent intent = new Intent();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".") + 1);
                    String mimeTypeFromExtension = mime.getMimeTypeFromExtension(extension);

                    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + selectedFile.getAbsolutePath()), mimeTypeFromExtension);
                    intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(intent);
                }
            }
        });
        if(Activity!= new FavoriteFragment().FavoriteFileRequest){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context,view);
                    popupMenu.getMenu().add("DELETE");
                    popupMenu.getMenu().add("MOVE");
                    popupMenu.getMenu().add("RENAME");
                    popupMenu.getMenu().add("FORCE RENAME");
                    popupMenu.getMenu().add("SHARE ON GOOGLE DRIVE");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if(menuItem.getTitle().equals("DELETE")){
                                myAdapterCallback.deleteFileCallback(selectedFile);
                                setFilesAndFolders();
                            }
                            if(menuItem.getTitle().equals("MOVE")){
                                myAdapterCallback.moveFileCallback(selectedFile);
                            }
                            if(menuItem.getTitle().equals("RENAME")){
                                myAdapterCallback.renameFileCallback(selectedFile);
                            }
                            if(menuItem.getTitle().equals("FORCE RENAME")){
                                myAdapterCallback.forceRenameFileCallback(selectedFile);
                            }
                            if(menuItem.getTitle().equals("SHARE ON GOOGLE DRIVE")){
                                myAdapterCallback.sharetoDriveCallback(selectedFile);
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return filesAndFolders.length;
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

    public void setFilesAndFolders() {
        this.filesAndFolders = sort(root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isHidden();
            }
        }));
        notifyDataSetChanged();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        //Selection des icones à afficher dans le RecylerView
        TextView textView;
        ImageView imageView;
        ToggleButton favoriteToggleButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text_view);
            imageView = itemView.findViewById(R.id.icon_view);
            favoriteToggleButton = itemView.findViewById(R.id.favorite_toggle);
        }
    }
}
