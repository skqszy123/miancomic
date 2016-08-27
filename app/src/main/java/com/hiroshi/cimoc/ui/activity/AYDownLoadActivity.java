package com.hiroshi.cimoc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String page2path = "";
    private AYDownloadDialog dialog;

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
                    page2path = pathlist.get(position);
                    pathlist_chapter = getFileListOrderByName(page2path);
                    lv_download.setAdapter(new ChapterDapter());
                    page = 2;
                }else {
                    Intent intent = new Intent(AYDownLoadActivity.this, AYReaderActivity.class);
                    intent.putStringArrayListExtra("pathlist", getFileListOrderByName(pathlist_chapter.get(position)));
                    startActivity(intent);
                }
            }
        });

        lv_download.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                dialog = new AYDownloadDialog(AYDownLoadActivity.this);
                dialog.setTitle("正在删除");
                dialog.showDialog();

                if(page == 1){
                    new DeleteTask().execute(pathlist.get(position));
                }else if(page == 2){
                    new DeleteTask().execute(pathlist_chapter.get(position));
                }

                return true;
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

    /**
     * 递归删除文件和文件夹
     *
     * @param file
     *            要删除的根目录
     */
    public void DeleteFile(File file) {

        if (file.exists() == false) {
            mHandler.sendEmptyMessage(0);
            return;
        } else {

            if (file.isFile()) {
                Message msg = new Message();
                msg.what = 4;
                msg.obj = file.getPath();
                mHandler.sendMessage(msg);
                Log.e("delete path",file.getPath());
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    mHandler.sendEmptyMessage(1);
                    if(page == 1){
                        mHandler.sendEmptyMessage(2);
                    }else if(page == 2){
                        mHandler.sendEmptyMessage(3);
                    }
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
                mHandler.sendEmptyMessage(1);
                if(page == 1){
                    mHandler.sendEmptyMessage(2);
                }else if(page == 2){
                    mHandler.sendEmptyMessage(3);
                }
            }
        }
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "文件或文件夹不存在", Toast.LENGTH_LONG).show();

                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "删除成功！", Toast.LENGTH_LONG).show();
                    break;

                case 2:
                    pathlist = getFileListOrderByName(getExternalFilesDir(null) + "/IMAGE");
                    lv_download.setAdapter(new downLoadAdapter());

                    break;

                case 3:
                    pathlist_chapter = getFileListOrderByName(page2path);
                    lv_download.setAdapter(new ChapterDapter());

                    break;
                case 4:
                    if(dialog!=null)
                        dialog.setText((String)msg.obj);
                    break;
                case 5:

                    break;
                default:
                    break;
            }
        };
    };


    class DeleteTask extends AsyncTask<String,Integer,Integer>{

        @Override
        protected Integer doInBackground(String... params) {
            DeleteFile(new File(params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(dialog!=null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }

}
