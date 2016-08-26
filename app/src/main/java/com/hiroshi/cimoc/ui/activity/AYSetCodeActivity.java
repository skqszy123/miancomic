package com.hiroshi.cimoc.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hiroshi.cimoc.R;

/**
 * Project：Cimoc-master
 * Author: sunkeqiang
 * Version: 1.0.0
 * Description：
 * Date：2016/8/26 17:05
 * Modification  History:
 * Why & What is modified:
 */
public class AYSetCodeActivity extends Activity {

    private EditText et_code;
    private Button btn_save;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setcode);
        et_code = (EditText) findViewById(R.id.et_code);
        btn_save = (Button) findViewById(R.id.btn_save);

        et_code.setText(loadData(this));

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(AYSetCodeActivity.this,et_code.getText().toString());
                Toast.makeText(AYSetCodeActivity.this,"密钥设置成功："+et_code.getText().toString(), Toast.LENGTH_SHORT).show();
                AYSetCodeActivity.this.finish();
            }
        });

    }

    private void saveData(Context context, String string){
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("code", string);
        editor.commit();
    }
    private String loadData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
//        Toast.makeText(this, sp.getString("content", "").toString(), 0).show();
        return sp.getString("code", "").toString();
    }
}
