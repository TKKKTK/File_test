package com.wg.file_test;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private EditText input_text;
    private Button write_button;
    private TextView read_text;
    private Button read_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //resquestPermisstion();
        input_text = (EditText) findViewById(R.id.input_text);
        write_button = (Button) findViewById(R.id.write_button);
        read_text = (TextView) findViewById(R.id.read_text);
        read_button = (Button) findViewById(R.id.read_button);

        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save(input_text.getText().toString())){
                    Toast.makeText(MainActivity.this,"文件写入成功",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "dir: "+getFilesDir());

                }else {
                    Toast.makeText(MainActivity.this,"文件写入失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

        read_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                read_text.setText(read());
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