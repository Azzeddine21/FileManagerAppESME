package com.example.androidproject;

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
    public Task<String> createFilePDF(String filepath){
        return Tasks.call(mExecutor, () -> {
            File fileMetaData = new File();
            fileMetaData.setName(filepath);

            java.io.File file = new java.io.File(filepath);
            FileContent mediaContent = new FileContent("application/pdf", file);

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
