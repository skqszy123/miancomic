package com.hiroshi.cimoc.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Project：imagedownload
 * Author: sunkeqiang
 * Version: 1.0.0
 * Description：
 * Date：2016/8/25 15:17
 * Modification  History:
 * Why & What is modified:
 */
public class DownLoadManager {

    private static DownLoadManager instance=null;
    private DownLoadManager(){
        //do something
    }
    public static DownLoadManager getInstance(){
        if(instance==null){
            synchronized(DownLoadManager.class){
                if(instance==null)
                {
                    instance=new DownLoadManager();
                }
            }
        }
        return instance;
    }

    /**
     * 获取网络图片
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl,String source){
        Log.e("imageurl:",imageurl);

        URL url;
        HttpURLConnection connection=null;
        Bitmap bitmap=null;


        StringBuffer url_s = new StringBuffer();

        char[] chars=imageurl.toCharArray();

        for(int i=0;i<chars.length;i++){
            byte[] bytes=(""+chars[i]).getBytes();
            if(bytes.length>1){
                int[] ints=new int[2];
                ints[0]=bytes[0]& 0xff;
                ints[1]=bytes[1]& 0xff;
                if(ints[0]>=0x81 && ints[0]<=0xFE && ints[1]>=0x40 && ints[1]<=0xFE){
                    try {
                        url_s.append(URLEncoder.encode(chars[i]+"", "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    url_s.append(chars[i]+"");
                }
            }
            else{
                url_s.append(chars[i]+"");
            }
        }


        try {
            url = new URL(url_s.toString());
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            connection.addRequestProperty("Referer",source);
            InputStream inputStream=connection.getInputStream();
            bitmap= BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存位图到本地
     * @param bitmap
     * @param path 本地路径
     * @return void
     */
    public void SavaImage(Bitmap bitmap, String path,String name){
      //  File file=new File(path);
        FileOutputStream fileOutputStream=null;
        //文件夹不存在，则创建它
//        if(!file.exists()){
//            file.mkdir();
//        }
        try {
            fileOutputStream=new FileOutputStream(path+"/"+name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDir(String BasePath, String ComicName,String ChapterName){
        File file=new File(BasePath);
        if(!file.exists()){
            file.mkdir();
        }

        File file1 = new File(BasePath+"/"+ComicName);
        if(!file1.exists()){
            file1.mkdir();
        }

        File file2 = new File(BasePath+"/"+ComicName+"/"+ChapterName);
        if(!file2.exists()){
            file2.mkdir();
        }



    }




}
