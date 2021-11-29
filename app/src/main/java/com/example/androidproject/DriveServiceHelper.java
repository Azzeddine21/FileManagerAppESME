package com.example.androidproject;

import android.content.Intent;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private Drive mdriveService;

    public DriveServiceHelper(Drive mdriveService){
        this.mdriveService = mdriveService;
    }
    public Task<String> createFile(String filepath){
        java.io.File fileConv = new java.io.File(filepath);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String fileName = fileConv.getName().substring(fileConv.getName().lastIndexOf("/") + 1);
        String extension = fileConv.getName().substring(fileConv.getName().lastIndexOf(".") + 1);
        String mimeTypeFromExtension = mime.getMimeTypeFromExtension(extension);
        return Tasks.call(mExecutor, () -> {
            File fileMetaData = new File();
            fileMetaData.setName(fileName);
            FileContent mediaContent = new FileContent(mimeTypeFromExtension, fileConv);

            File myFile = null;
            try{
                myFile = mdriveService.files().create(fileMetaData, mediaContent).execute();

            } catch(Exception e){
                e.printStackTrace();
            }

            if(myFile == null){
                throw new IOException("Null result");
            }
            return myFile.getId();





        });
    }
}