package com.nexflare.fileexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class insideFile extends AppCompatActivity {
    private static final String TAG = "ERR00s";
    public static final String TAGs="NEXTACT";
    TextView tec,men;
    String path;
    String copy_path,paste_path; //variable to store the paths for coping the file
    RV mRecyclerView;
    Button copy,pas;
    SharedPreferences sharedpreferences;

    ArrayList<String> filearr;
    @Override
    protected void onStart() {
        super.onStart();
        filearr=new ArrayList<>();
        populatefile(path);
        mRecyclerView.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        filearr=new ArrayList<>();
        populatefile(path);
        mRecyclerView.notifyDataSetChanged();
    }


    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_file);
        rv= (RecyclerView) findViewById(R.id.rvInside);
        Intent intent=getIntent();
        sharedpreferences = getSharedPreferences("file_explorer", Context.MODE_PRIVATE);
        path=intent.getStringExtra("path");
        Log.d(TAGs, "onCreate: "+path);
        filearr=new ArrayList<>();
        populatefile(path);
        mRecyclerView =new RV();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mRecyclerView);
    }
    public class VH extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv,men;
        CardView cv;
        public VH(View itemView) {
            super(itemView);
            iv= (ImageView) itemView.findViewById(R.id.ivfile);
            tv= (TextView) itemView.findViewById(R.id.tvfile);
            cv= (CardView) itemView.findViewById(R.id.cv);
            men= (TextView) itemView.findViewById(R.id.menu);
        }
    }
    public class RV extends RecyclerView.Adapter<VH>{


        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=getLayoutInflater();
            View v=inflater.inflate(R.layout.list_files,parent,false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(final VH holder, int position) {
            final String s=filearr.get(position);
            holder.tv.setText(s);
            final SharedPreferences.Editor editor = sharedpreferences.edit();
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        String filenameArray[] = s.split("\\.");
                        String extension = "";
                        extension = filenameArray[filenameArray.length - 1];
                        Log.d(TAG, "onClick EXTENSION: " + extension);

                        if (extension.equals("mp3")) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            Log.d(TAG, "onClick: PATH" + path + "/" + s);

                            File file = new File(path + "/" + holder.tv.getText());
                        /*intent.setData(Uri.parse("file://" + file.getPath()));
                        intent.setType("audio*//*");*/
                            intent.setDataAndType(Uri.fromFile(file),"audio/mp3");
//                            intent.setDataAndType(FileProvider.getUriForFile(insideFile.this, "com.nexflare.fileprovider", file), "audio/mp3");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);

                        } else if (extension.equals("pdf")) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
//                            Toast.makeText(insideFile.this, "Intent for pdf Called", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "onClick: PATH" + path + "/" + s);
                            File file = new File(path + "/" + holder.tv.getText());
//                            Toast.makeText(insideFile.this, "File:" + file.getName(), Toast.LENGTH_SHORT).show();
                            intent.setDataAndType(Uri.fromFile(file),"application/pdf");
//                            intent.setDataAndType(FileProvider.getUriForFile(insideFile.this, BuildConfig.APPLICATION_ID + ".provider", file), "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(insideFile.this, insideFile.class);
                            intent.putExtra("path", path + "/" + holder.tv.getText());
                            startActivity(intent);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e("EXC",e.getMessage());
                    }
                }
            });
            //Toast.makeText(insideFile.this, "outside", Toast.LENGTH_SHORT).show();
            holder.men.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Toast.makeText(insideFile.this, "Entered", Toast.LENGTH_SHORT).show();
                    PopupMenu pop_menu=new PopupMenu(insideFile.this,holder.men);
                    pop_menu.inflate(R.menu.option_menu);
              //      Toast.makeText(insideFile.this, "Inflted", Toast.LENGTH_SHORT).show();
                    pop_menu.show();
                    pop_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId())
                            {
                                case R.id.copy_menu:
                                    copy_path= path +  "/" + holder.tv.getText();
                                    editor.putString("copy_path",copy_path);
                                    editor.putString("name_copy",  (String) holder.tv.getText());
                                    Toast.makeText(insideFile.this, copy_path, Toast.LENGTH_SHORT).show();
                                    editor.apply();
                                    break;
                                case R.id.paste_menu:
                                    if(!filearr.contains(sharedpreferences.getString("name_copy" ," ")))
                                        paste();
                                    else
                                        name_exists(sharedpreferences.getString("name_copy"," "));
                                    break;

                                case R.id.move_from:
                                    copy_path= path +  "/" + holder.tv.getText();
                                    editor.putString("move_file",copy_path);
                                    editor.putString("name_move", (String) holder.tv.getText());
                                    editor.apply();
                                    break;
                                case R.id.move_to:
                                    if(path==sharedpreferences.getString("move_file"," "))
                                        move_exist(1);
                                    else if(filearr.contains(sharedpreferences.getString("name_move"," ")))
                                        move_exist(2);
                                    else
                                        move();
                                    break;
                                case R.id.delete:
                                    File file = new File(path +  "/" + holder.tv.getText());
                                    boolean deleted = file.delete();
                                    if(!deleted)
                                        Toast.makeText(insideFile.this, "Error 900 Not deleted ", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(insideFile.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(insideFile.this, "you are an asss hole ", Toast.LENGTH_SHORT).show();


                            }
                            return false;
                        }
                    });


                }

            });
        }

        @Override
        public int getItemCount() {
            return filearr.size();
        }
    }
    public void populatefile(String in) {
        Log.d(TAG, "populatefile:");
        Log.d("Files", "Path: " + path);

        File directory = new File(in);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            filearr.add(new String(files[i].getName()));

        }
       // Collections.sort(filearr);
    }
    public void name_exists(String name)
    {
        Toast.makeText(insideFile.this, "same file aleready exisit having name " + name, Toast.LENGTH_SHORT).show();
        Toast.makeText(insideFile.this, "We will change the name for you :D", Toast.LENGTH_LONG).show();
        File source = new File(sharedpreferences.getString("copy_path"," "));
        String destinationPath = path + "/"+"-copy"+name;
        Toast.makeText(insideFile.this,path+ "", Toast.LENGTH_SHORT).show();
        File destination = new File(destinationPath);
        try
        {
            FileUtils.copyFile(source, destination);
            filearr.add("-copy"+sharedpreferences.getString("name_copy"," "));
            mRecyclerView.notifyDataSetChanged();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return;
    }
    public void paste()
    {
        File source = new File(sharedpreferences.getString("copy_path"," "));
        String destinationPath = path + "/"+sharedpreferences.getString("name_copy" ," ");
        Toast.makeText(insideFile.this,path+ "", Toast.LENGTH_SHORT).show();
        File destination = new File(destinationPath);
        try
        {
            FileUtils.copyFile(source, destination);
            filearr.add(sharedpreferences.getString("name_copy"," "));
            mRecyclerView.notifyDataSetChanged();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return;
    }
    public void move()
    {
        File from = new File(sharedpreferences.getString("move_file"," "));
        File to=new File(path + "/"+sharedpreferences.getString("name_move"," "));
        from.renameTo(to);
        filearr.add(sharedpreferences.getString("name_move"," "));
        mRecyclerView.notifyDataSetChanged();
        return;
    }
    public void move_exist(int ca)
    {
        switch (ca)
        {
            case 1:
                Toast.makeText(this, "Cannot move th foler to the same folder. Error 406", Toast.LENGTH_LONG).show();
                break;
            case 2:
                File from = new File(sharedpreferences.getString("move_file"," "));
                File to=new File(path + "/"+"-moved"+sharedpreferences.getString("name_move"," "));
                from.renameTo(to);
                filearr.add("-moved"+sharedpreferences.getString("name_move"," "));
                mRecyclerView.notifyDataSetChanged();
                break;
            default:
                break;

        }
        return;
    }
    public void delete(String name)
    {

    }
}
