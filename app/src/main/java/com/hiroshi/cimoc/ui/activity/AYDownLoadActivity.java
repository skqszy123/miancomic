package com.hiroshi.cimoc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hiroshi.cimoc.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Project：Cimoc-master
 * Author: sunkeqiang
 * Version: 1.0.0
 * Description：
 * Date：2016/8/26 14:26
 * Modification  History:
 * Why & What is modified:
 */
public class AYDownLoadActivity extends Activity {

    ArrayList<String> pathlist;
    ArrayList<String> pathlist_chapter;
    private ListView lv_download;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aydownload);
        lv_download = (ListView) findViewById(R.id.lv_download);

        File file = new File(getExternalFilesDir(null) + "/IMAGE");
        if(!file.exists()){
            return;
        }
        pathlist = getFileListOrderByName(getExternalFilesDir(null) + "/IMAGE");

        lv_download.setAdapter(new downLoadAdapter());

        lv_download.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(page == 1){
                    pathlist_chapter = getFileListOrderByName(pathlist.get(position));
                    lv_download.setAdapter(new ChapterDapter());
                    page = 2;
                }else {
                    Intent intent = new Intent(AYDownLoadActivity.this, AYReaderActivity.class);
                    intent.putStringArrayListExtra("pathlist", getFileListOrderByName(pathlist_chapter.get(position)));
                    startActivity(intent);
                }
            }
        });



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0
                &&page == 2) {
            pathlist = getFileListOrderByName(getExternalFilesDir(null) + "/IMAGE");
            lv_download.setAdapter(new downLoadAdapter());
            page = 1;
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    class downLoadAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return pathlist.size();
        }

        @Override
        public Object getItem(int position) {
            return pathlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(AYDownLoadActivity.this).inflate(R.layout.item_aydownload,parent,false);
                holder = new Holder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            }else{
                holder = (Holder)convertView.getTag();
            }
            String path = pathlist.get(position);

            holder.tv_name.setText(path.substring(path.lastIndexOf("/")+1,path.length()));

            return convertView;
        }

        class Holder{
            TextView tv_name;
        }
    }

    class ChapterDapter extends BaseAdapter{

        @Override
        public int getCount() {
            return pathlist_chapter.size();
        }

        @Override
        public Object getItem(int position) {
            return pathlist_chapter.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(AYDownLoadActivity.this).inflate(R.layout.item_aydownload,parent,false);
                holder = new Holder();
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            }else{
                holder = (Holder)convertView.getTag();
            }
            String path = pathlist_chapter.get(position);

            holder.tv_name.setText(path.substring(path.lastIndexOf("/")+1,path.length()));

            return convertView;
        }

        class Holder{
            TextView tv_name;
        }
    }
    //按照文件名称排序
    public  ArrayList<String> getFileListOrderByName(String fliePath) {
        List<File> files = Arrays.asList(new File(fliePath).listFiles());
        Collections.sort(files, new Comparator< File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        ArrayList<String> pathlist= new ArrayList<>();
        for(File f:files){
            pathlist.add(f.getPath());
        }
      return pathlist;
    }
}
