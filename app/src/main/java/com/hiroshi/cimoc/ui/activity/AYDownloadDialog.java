package com.hiroshi.cimoc.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hiroshi.cimoc.R;


/**
 * Project：haloshop-android
 * Author: sunkeqiang
 * Version: 1.0.0
 * Description：
 * Date：2016/5/24 10:13
 * Modification  History:
 * Why & What is modified:
 */
public class AYDownloadDialog {
    private Context context;
    private View view;
    private Dialog dialog;

    private TextView mTv_dialog_cancel;
    private TextView mTv_dialog_sure;
    private TextView tv_text;
    private String content;
    public AYDownloadDialog(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
        findview();
    }

    public void showDialog() {
        //创建dialog并进行相关设置
        dialog = new Dialog(context, R.style.ViewDialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        // view.setAnimation(AnimationUtils.loadAnimation(context, R.anim));
        //dialogWindow.setWindowAnimations(R.style.down_up);
        //dialogWindow.setGravity(Gravity.TOP);


        initData();


        dialog.show();
    }

    private void findview(){
        tv_text = (TextView) view.findViewById(R.id.tv_down);


    }

    private void initData(){

    }

    public void setText(String text){
        tv_text.setText(text);
    }

    public void dismiss(){
        dialog.dismiss();
    }
}
