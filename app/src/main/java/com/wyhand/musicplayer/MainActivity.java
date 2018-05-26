package com.wyhand.musicplayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    private Button play;
    private Button pause;
    private Button stop;
    private Button next;
    private Button precious;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int Position = 0;//mCurrentPosition记录当前播放歌曲的位置
    private ArrayList<File> playList = new ArrayList<File>();//存储指定目录下音频文件的path，示例共6个文件
    private String path = "/MusicTest/";
    private String[] filename;
    private AlertDialog dlgSpecItem;
    private View specItemView;
    private ArrayList name = new ArrayList();;
    private ListView list;
    private View v;
    //private HashMap map = new HashMap();

    private Handler mHandler = new Handler();//Handler实例化
    private SeekBar seekBar;
    private TextView mTextView;
    private SimpleDateFormat time = new SimpleDateFormat("m:ss");//进度条下面的当前进度文字，将毫秒化为m:ss格式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);
        next = (Button) findViewById(R.id.next);
        precious = (Button) findViewById(R.id.precious);
        list = (ListView) findViewById(R.id.list);//通过资源Id获得控件
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        next.setOnClickListener(this);
        precious.setOnClickListener(this);
        //list.setOnClickListener(this);
        File filer = new File(Environment.getExternalStorageDirectory(), path);
        System.out.println("文件" + filer.getPath());
        getFile(filer);//递归遍历文件夹中指定格式文件存入playList中
        SimpleAdapter adapter = new SimpleAdapter(this, name, R.layout.list, new String[] {"Name"}, new int[] {R.id.listname});
        list.setAdapter(adapter);//适配器与控件绑定
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){//动态申请权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
        }else{
            for(int i = 0; i < playList.size(); i++) {
                initMediaPlayer(i);//初始化MediaPlayer
            }
        }
        seekBar.setOnSeekBarChangeListener(new MySeekBarListener());
    }

    private void initMediaPlayer(int position) {
        //System.out.println("");
        try {//file(File parent, String child)根据 parent 抽象路径名和 child 路径名字符串创建一个新 File 实例。
            //System.out.println(Environment.getExternalStorageDirectory());
            File file00 = playList.get(position);
            File filete = playList.get(0);
            File file = new File(Environment.getExternalStorageDirectory(), "/MusicTest/imf.MP3");//Environment.getExternalStorageDirectory()获得sd卡根目录：?手机内部存储
            //System.out.println("找到音乐");
            mediaPlayer.setDataSource(file00.getPath());//getPath将此抽象路径名转换为一个路径名字符串。
            System.out.println(file00.getPath());
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initMediaPlayer(0);
                } else {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                System.out.println("switch" + Position);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());//设置进度条长度
                    //发送一个Runnable, handler收到之后就会执行run()方法
//                    handler.post(new Runnable() {
//                        public void run() {
//                            // 更新进度条状态
//                            if (!isStartTrackingTouch)
//                                //获取当前播放音乐的位置
//                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                            // 1秒之后再次发送
//                            handler.postDelayed(this, 1000);
//                        }
//                    });
                    //setOnCompletionListener 当当前多媒体对象播放完成时发生的事件
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer arg0) {//如果当前歌曲播放完毕,自动播放下一首.
                            if(!mediaPlayer.isPlaying()&& Position < playList.size() && Position >= 0){
                                mediaPlayer.reset();//切换歌曲reset()很重要很重要很重要，没有会报IllegalStateException
                                if (Position >= playList.size()-1) {//这里的if只要是为了不让歌曲的序号越界,Position=playList.size()-1使其=0
                                    Position = 0;
                                    initMediaPlayer(Position);
                                    mediaPlayer.start();
                                    seekBar.setMax(mediaPlayer.getDuration());//设置进度条长度
                                } else {
                                    Position = Position + 1;
                                    initMediaPlayer(Position);
                                    mediaPlayer.start();
                                    seekBar.setMax(mediaPlayer.getDuration());//设置进度条长度
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            case R.id.stop:
                if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
                initMediaPlayer(0);
            }
                break;
            case R.id.next:
                //System.out.println("下一首：" + Position);
                if (mediaPlayer != null && Position < playList.size() && Position >= 0) {
                    mediaPlayer.reset();//切换歌曲reset()很重要很重要很重要，没有会报IllegalStateException
                    if (Position >= playList.size()-1) {//这里的if只要是为了不让歌曲的序号越界,Position=playList.size()-1使其=0
                        Position = 0;
                        initMediaPlayer(Position);
                        mediaPlayer.start();
                    } else {
                        Position = Position + 1;
                        initMediaPlayer(Position);
                        mediaPlayer.start();
                    }
                }
                break;
            case R.id.precious:
                //System.out.println("上一首" + Position);
                if (mediaPlayer != null && Position < playList.size() && Position >= 0) {
                    mediaPlayer.reset();
                    if (Position == 0) {
                        Position = playList.size() - 1;
                        //System.out.println("当前" + Position);
                        initMediaPlayer(Position);
                        mediaPlayer.start();
                    } else {
                        Position = Position - 1;
                        initMediaPlayer(Position);
                        mediaPlayer.start();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    public static File[] getAllFileName(String path){//获取某一路径下所有文件
        File file = new File(path);
        File[] filelist = file.listFiles();//API-File-list() 返回一个字符串数组，这些字符串指定此抽象路径名表示的目录中的文件和目录。
        System.out.println(path);
        System.out.println(filelist[0]);
        return filelist;

    }

    public void  getFile(File file) {
        String filename;//文件名
        String suf;//文件后缀
        //System.out.println("path1:" + path);
        //File dir = new File(path);//文件夹dir
        System.out.println("path2:" + file.getName() + "-----" + file.getPath());
        File[] files = file.listFiles();//文件夹下的所有文件或文件夹

        if (files == null)
            System.out.println("ERROR!files is null");

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {////如果path表示的是一个目录则返回true
                System.out.println("---" + files[i].getAbsolutePath());//返回抽象路径名的绝对路径名字符串。
                //getFile(files[i]);//递归文件夹！！！
            } else {
                filename = files[i].getName();
                int j = filename.lastIndexOf(".");//表示b字符串在a字符串中最后出现的位置
                suf = filename.substring(j + 1);//截取掉str从首字母起长度为beginIndex的字符串，将剩余字符串赋值给str；得到文件后缀
                if (suf.equalsIgnoreCase("MP3")) {//判断是不是msml后缀的文件，equalsIgnoreCase忽略大小写差异
                    String strFileName = files[i].getAbsolutePath();//.toLowerCase()大写转小写
                    playList.add(files[i]);//对于文件才把它的路径加到filelist中
                }
            }


        }
        //显示列表准备：
        for (int i = 0; i < playList.size(); i++) {
            String filenamen = playList.get(i).toString().substring(playList.get(i).toString().lastIndexOf("/")+1);
            System.out.println("文件名称：" + filenamen);
            HashMap map = new HashMap();
            map.put("Name", filenamen);
            name.add(map);
        }
        System.out.println(name.size());
        System.out.println(name.get(4));
    }

    //进度条
    private final class MySeekBarListener implements SeekBar.OnSeekBarChangeListener {
        //移动触发
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
        //起始触发
        public void onStartTrackingTouch(SeekBar seekBar) {
            //isStartTrackingTouch = true;
        }
        //结束触发
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekBar.getProgress());
           //isStartTrackingTouch = false;
        }
    }
}
