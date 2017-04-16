package com.nexflare.fileexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "path";
    ArrayList<String> filearr;
    RecyclerView rv;
    String path;
    FileAdapter fileAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filearr=new ArrayList<>();
        rv= (RecyclerView) findViewById(R.id.rv);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)&&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1510);
        }
        else{
            populatefile("");
            fileAdapter=new FileAdapter();
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(fileAdapter);
        }

    }

    private void populatefile(String in) {
        path = Environment.getExternalStorageDirectory().toString();
        Log.d(TAG, "populatefile:");
        Log.d("Files", "Path: " + path);
        if(isExternalStorageReadable()){
            Log.d(TAG, "populatefile: "+"readable");
        }
        else{
            Log.d(TAG, "populatefile: "+"Not readable");
        }
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            filearr.add(new String(files[i].getName()));
        }
    }

    public class VH extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv;
        CardView cv;
        public VH(View itemView) {
            super(itemView);
            iv= (ImageView) itemView.findViewById(R.id.ivfile);
            tv= (TextView) itemView.findViewById(R.id.tvfile);
            cv= (CardView) itemView.findViewById(R.id.cv);
        }
    }
    public class FileAdapter extends RecyclerView.Adapter<VH>{

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater lf=getLayoutInflater();
            View v=lf.inflate(R.layout.list_files,parent,false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(final VH holder, int position) {
            String s= filearr.get(position);
            holder.tv.setText(s);
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,insideFile.class);
                    intent.putExtra("path",path+"/"+holder.tv.getText());
                    Log.d(TAG, "GOING TO OTHER ACTIVITY "+path+"/"+holder.tv.getText());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return filearr.size();
        }
    }
    public boolean isExternalStorageReadable() {
         String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
         Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
             return true;
             }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int check=0;
        if(requestCode==1510){
            for(int i=0;i<permissions.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED)
                    check++;
            }
            if(check==2){
                populatefile("");
                fileAdapter=new FileAdapter();
                rv.setLayoutManager(new LinearLayoutManager(this));
                rv.setAdapter(fileAdapter);
            }
            else{
                finish();
            }
        }
        
    }
}
