package com.wg.file_test;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * android:requestLegacyExternalStorage="false" false代表采用分区存储
 * 为true 代表的是采用之前的存储方式
 */
public class MainActivity extends AppCompatActivity {

    private EditText input_text;
    private Button write_button;
    private TextView read_text;
    private Button read_button;
    private Button file_create;
    private Button open_file;
    private Button save_file;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK){
            Log.d("是否选取到文件:", "onActivityResult: ");
            Uri uri = data.getData();
            Log.d(TAG, "文件的uri: "+uri);
            InputStream inputStream = null;
             StringBuilder stringBuilder = new StringBuilder();
            try {
                inputStream = getContentResolver().openInputStream(uri);

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = reader.readLine()) != null){
                      stringBuilder.append(line);
                }
                read_text.setText(stringBuilder);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        resquestPermisstion();
        input_text = (EditText) findViewById(R.id.input_text);
        write_button = (Button) findViewById(R.id.write_button);
        read_text = (TextView) findViewById(R.id.read_text);
        read_button = (Button) findViewById(R.id.read_button);
        file_create = (Button) findViewById(R.id.file_create);
        open_file = (Button) findViewById(R.id.open_file);
        save_file = (Button) findViewById(R.id.save_file);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            boolean isStorage = Environment.isExternalStorageLegacy();
            Log.d(TAG,"是否支持外部存储：" + String.valueOf(isStorage));
        }


        write_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
//                if (save(input_text.getText().toString())){
//                    Toast.makeText(MainActivity.this,"文件写入成功",Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "dir: "+getFilesDir());
//
//                }else {
//                    Toast.makeText(MainActivity.this,"文件写入失败",Toast.LENGTH_SHORT).show();
//                }
                createFile(input_text.getText().toString());
            }
        });

        read_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                read_text.setText(read());
            }
        });

        file_create.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {

            }
        });

        /**
         * 打开文件
         */
        open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = MediaStore.Files.getContentUri("external");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);

//                startActivityForResult(intent, 100);

                startActivityIfNeeded(intent,100);
            }
        });





        /**
         * 创建并保存新文件
         */
        save_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = MediaStore.Files.getContentUri("external");
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/txt");
                intent.putExtra(Intent.EXTRA_TITLE, "invoice.txt");

                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker when your app creates the document.
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);

                startActivityForResult(intent, 1);
            }
        });
    }

    public boolean save(String content){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        Boolean isSave = false;

        try {
            Log.d(TAG, "save: "+getFilesDir());
            out = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(content);
            isSave = true;
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (writer != null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return isSave;
    }

    public String read(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();

        try {
            in = openFileInput("data");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine()) != null){
                content.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (reader != null){
                    reader.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return content.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void createFile(String content){

        //判断当前android 版本是否支持分区存储
        if (!Environment.isExternalStorageLegacy()){
            //采取分区存储方式
            //在Android R 下创建文件
            //获取到一个路径
            Uri uri = MediaStore.Files.getContentUri("external");
            //创建一个ContentValue对象，用来给存储文件数据的数据库进行插入操作
            ContentValues contentValues = new ContentValues();
            //首先创建zee.txt要存储的路径 要创建的文件的上一级存储目录
            String path = Environment.DIRECTORY_DOWNLOADS + "/ZEE";
            Log.d(TAG, "createFile: "+path);
            //给路径的字段设置键值对
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS+"/ZEE");
            //设置文件的名字
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME,"Zee.txt");
            //可有可无
            contentValues.put(MediaStore.Downloads.TITLE,"Zee");

            //插入一条数据，然后把生成的这个文件的路径返回回来
            Uri insert = getContentResolver().insert(uri,contentValues);

            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(insert);
                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                bos.write(content.getBytes());
                bos.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
                //传统File方式
                File file = new File("/sdcard/WG.txt");
                OutputStream outputStream = null;
            if (!file.exists()){
                try {
                    file.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            try {
                outputStream = new FileOutputStream(file);
                outputStream.write(content.getBytes());
                outputStream.close();
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    public void resquestPermisstion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};
            for (String p : perms){
                if (ContextCompat.checkSelfPermission(this,p) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,perms,1);
                }
            }
        }
    }




}