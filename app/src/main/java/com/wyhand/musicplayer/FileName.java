package com.wyhand.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileName {

    public static String [] getFileName(String path) {
        File file = new File(path);
        String [] fileName = file.list();
        return fileName;
    }

    public static String[] getAllFileName(String path){//获取某一路径下所有文件名
        File file = new File(path);
        String[] names = file.list();//API-File-list() 返回一个字符串数组，这些字符串指定此抽象路径名表示的目录中的文件和目录。
        return names;
    }

//    public static void getAllFileName(String path,ArrayList<String> fileName) {
//        File file = new File(path);
//        File [] files = file.listFiles();
//        String [] names = file.list();
//        if(names != null)
//            fileName.addAll(Arrays.asList(names));
//        for(File a:files)
//        {
//            if(a.isDirectory())
//            {
//                getAllFileName(a.getAbsolutePath(),fileName);
//            }
//        }
//    }
}
