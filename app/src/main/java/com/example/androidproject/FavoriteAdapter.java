package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder>{

    private Context context;

    private ArrayList<File> favFileList = new ArrayList<File>();
    public FavoriteAdapterCallback  myFavoriteAdapterCallback;

    public FavoriteAdapter(Context context){
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("favFile", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if ((boolean) entry.getValue()){
                favFileList.add(new File(entry.getKey()));
            }
        }
    }

    public interface FavoriteAdapterCallback{
        void openDirectoryCallback(String directoryPath,int Activity);
        void setToFavoriteCallback(File file, boolean checked);
    }

    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        File selectedFile = favFileList.get(position);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedFile.isDirectory()){
                    myFavoriteAdapterCallback.openDirectoryCallback(selectedFile.getAbsolutePath(),0);
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

        holder.favoriteToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.favoriteToggleButton.isChecked()){
                    myFavoriteAdapterCallback.setToFavoriteCallback(selectedFile, true);
                }else{
                    myFavoriteAdapterCallback.setToFavoriteCallback(selectedFile, false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return favFileList.size();
    }

    public void updatefavFileList(){
        ArrayList<File> newfavFileList = new ArrayList<File>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("favFile", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if ((boolean) entry.getValue()){
                newfavFileList.add(new File(entry.getKey()));
            }
        }
        favFileList = newfavFileList;
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
