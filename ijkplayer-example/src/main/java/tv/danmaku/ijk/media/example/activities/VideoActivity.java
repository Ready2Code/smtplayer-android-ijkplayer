/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.example.activities;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;


import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.application.Settings;
import tv.danmaku.ijk.media.example.content.RecentMediaStorage;
import tv.danmaku.ijk.media.example.fragments.TracksFragment;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.example.widget.media.MeasureHelper;


public class VideoActivity extends AppCompatActivity implements TracksFragment.ITrackHolder {
    private static final String TAG = "VideoActivity";

    private String mVideoPath;
    private String mNtpServerIp;
    private String mLogServerIp;
    private String bluetoothClientIp;
    private String mDeviceName;
    private boolean playfile;
    private Uri    mVideoUri;
    private String mVideoType;
    private EditText bluetoothdata;
    private int    SendMode;
    private boolean mEnableBluetooth;
    private boolean mAddFlag;

    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private TextView mToastTextView;
    private TableLayout mHudView;
    private DrawerLayout mDrawerLayout;
    private ViewGroup mRightDrawer;

    private ImageView imageView;
    private ImageView infoimage;
    private Animation animation;
    private AnimationSet animationSet;
    private DatagramSocket receiveSocket;
    private boolean listenStatus = true;
    private int RECVPORT = 9430;
    private MyAsyncTask myAsyncTask=null;
    private Settings mSettings;
    private boolean mBackPressed;
    public static VideoActivity videoA;
    private  final static String ACTION="com.sjtu.wefirechat.ACTION_GATT_MESSAGE";
    public static Intent newIntent(Context context, String videoPath, String videoTitle, String videoType) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        intent.putExtra("videoType", videoType);
        return intent;
    }



    public static void intentTo(Context context, String videoPath, String videoTitle, String videoType) {
        Intent intent = newIntent(context, videoPath, videoTitle, videoType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
       //         .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        videoA = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (mSettings == null) {
            mSettings = new Settings(this);
        }
        //add blutetooth
        mEnableBluetooth = mSettings.getEnableBluetoothSendData();
        Button mode_button = (Button)findViewById(R.id.FQ);
        final Button blt_button = (Button)findViewById(R.id.bluetooth);
        bluetoothdata = (EditText)findViewById(R.id.bluetoothText);
        if(!mEnableBluetooth)
        {
            mode_button.setVisibility(View.GONE);
            blt_button.setVisibility(View.GONE);
            bluetoothdata.setVisibility(View.GONE);

        }
        mode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
               //sendCommand("del");
         /*       if(SendMode == 0)
                    SendMode = 1;// use socket send data
                else
                    SendMode = 0;*/
            }
        });

        blt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    switchVideo();
                }

        });
        // handle arguments
        mVideoPath = getIntent().getStringExtra("videoPath");
      //  mVideoPath = getVideoPath();
        mDeviceName = mSettings.getSocketClientIp();
        mLogServerIp = mSettings.getLogServerIp();
        mNtpServerIp = mSettings.getNtpServerIp();//+":"+bluetoothClientIp+":"+mLogServerIp; //send to ff_ffplay.c use one string
        if(mNtpServerIp.isEmpty())
            mNtpServerIp = "127.0.0.1";
        if(mLogServerIp.isEmpty())
            mLogServerIp = "127.0.0.1:1234";
        if(mDeviceName.isEmpty())
            mDeviceName = "mobile";
       // sendCommand("add");
        mNtpServerIp = mNtpServerIp+":"+mLogServerIp+":"+mDeviceName; //send to ff_ffplay.c use one string
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction.equals(Intent.ACTION_VIEW)) {
                mVideoPath = intent.getDataString();
            } else if (intentAction.equals(Intent.ACTION_SEND)) {
                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    String scheme = mVideoUri.getScheme();
                    if (TextUtils.isEmpty(scheme)) {
                        Log.e(TAG, "Null unknown scheme\n");
                        finish();
                        return;
                    }
                    if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                        mVideoPath = mVideoUri.getPath();
                    } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                        Log.e(TAG, "Can not resolve content below Android-ICS\n");
                        finish();
                        return;
                    } else {
                        Log.e(TAG, "Unknown scheme " + scheme + "\n");
                        finish();
                        return;
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(mVideoPath)) {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
        }

        // init UI
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);

        mToastTextView = (TextView) findViewById(R.id.toast_text_view);
        mHudView = (TableLayout) findViewById(R.id.hud_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRightDrawer = (ViewGroup) findViewById(R.id.right_drawer);

        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        int playfile  = (mSettings.getEnablePlayFile())?1:0;
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        IjkMediaPlayer.set_ntpServerIp(mNtpServerIp);
        IjkMediaPlayer.set_playfile(playfile);


        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        if (mVideoPath != null)
            mVideoView.setVideoPath(mVideoPath);
        else if (mVideoUri != null)
            mVideoView.setVideoURI(mVideoUri);
        else {
            Log.e(TAG, "Null Data Source\n");
            finish();
            return;
        }
        mVideoView.start();

        imageView = (ImageView) findViewById(R.id.reddot_image);

        infoimage = (ImageView) findViewById(R.id.info_image);
        mVideoType = getIntent().getStringExtra("videoType");
        Log.i("SmtVideActivity", mVideoType);
        if (mVideoType.equals("broadcast")){
            Bitmap typeBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.broadcast);
            setImage(100, 100);
            infoimage.setImageBitmap(typeBitmap);
        }else if(mVideoType.equals("broadband")){
            Bitmap typeBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.broadband);
            setImage(100, 100);
            infoimage.setImageBitmap(typeBitmap);
        }
        Log.i("SmtVideActivity", mVideoType);



        animationSet = new AnimationSet(true);
        animation = new AlphaAnimation(0, 1.0f);
        animation.setDuration(500);
        animation.setRepeatCount(1);
        animationSet.addAnimation(animation);
        if(myAsyncTask !=null){
            myAsyncTask.cancel(true);
        }
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    private class MyAsyncTask extends AsyncTask<Void, Object, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String[] ss;
            Object[] mImage = new Object[4];
            Bitmap recvimage;
            try{
                Log.i("Smt", "start receivesocket******************");
                if (receiveSocket == null) {
                    Log.i("Smt", "new receivesocket******************");
                    receiveSocket = new DatagramSocket(null);
                    receiveSocket.setReuseAddress(true);
                    receiveSocket.bind(new InetSocketAddress(RECVPORT));
                }

                byte[] inBuf = new byte[8182];

                while (listenStatus){
                    if (isCancelled()) {
                        break;
                    }
                    Log.i("Smt", "Video receive reddo*****************************************");
                    DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
                    receiveSocket.receive(inPacket);

                    String recvInfo = new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength());
                    Log.i("SmtVideoActivity ss2==","********************"+recvInfo);
                    ss = recvInfo.split("-");
                   // if(ss.length<2)
                       // return null;
                    if (ss[0].equals("reddot")) {
                        //redot
//                        recvimage = null;
//                        mImage[0] = ss[0];
//                        mImage[1] = recvimage;
                        //image display test
                        recvimage=null;
                        Log.i("SmtVideoActivity ss1=",ss[1]);
                        recvimage = getImageFromNet(ss[1]);
                        if(recvimage==null || ss.length < 2){
                            Log.i("SmtVideoActivity", "do InBackground recvimage===null");
                            //return null;
                        }
                        mImage[0] = ss[0];
                        mImage[1] = recvimage;
                        mImage[2]=100;
                        mImage[3]=100;

                        Log.i("SmtVideoActivity", "do InBackground reddot");
                    } else if (ss[0].equals("stop")) {
                        recvimage = null;
                        Log.i("SmtVideoActivity", "do InBackground stop");
                    } else {
                        //recvimage = null;
                       // recvimage = getImageFromNet(ss[0]);
                        Log.i("SmtVideoActivity", "do InBackground " + ss[1] +"--");
                    }
                  //  Log.i("SmtVideoActivity ss1=",ss[1]);
                 //   Log.i("SmtVideoActivity ss2=",ss[2]);
       //             if(ss.length<2){
       //               mImage[0] = ss[0];
           //           mImage[1] = recvimage;
//   //                 }else if(ss.length>2){
//                      mImage[0] = ss[0];
//                      mImage[1] = recvimage;
//                      mImage[2] = Integer.parseInt(ss[1]);
//                      mImage[3] = Integer.parseInt(ss[2]);
//                    }
                    publishProgress(mImage);
                    Log.i("SmtVideoActivity", "InBackground");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values){
<<<<<<< HEAD
            if(isCancelled()){
=======
            if (isCancelled()) {
>>>>>>> cb4030de57c8d91b670a24b20a747c2488c4835d
                return;
            }
            super.onProgressUpdate(values);
            Log.i("SmtVideoActivity", "onProgressUpdate"+values[0]);
            if (values[0].equals("reddot")) {
                getAlphaAnimation();
                infoimage.setImageBitmap((Bitmap)values[1]);
                setImage((int)values[2], (int)values[3]);
                Log.i("SmtVideoActivity", "onProgressUpdate reddot"+values[1]+" " +values[2]);
            } else if (values[0].equals("stop")) {
                infoimage.setImageDrawable(null);
                Log.i("SmtVideoActivity", "onProgressUpdate stop"+values[1]+" " +values[2]);
            } else {
                infoimage.setImageBitmap((Bitmap)values[1]);
                setImage((int)values[2], (int)values[3]);
                Log.i("SmtVideoActivity", "onProgressUpdate info"+values[2]+" " +values[3]);
            }
        }

    }

    public void getAlphaAnimation() {

        imageView.startAnimation(animationSet);
        Log.i("SmtListenService","getAplhaAnimation");
    }

    private Bitmap getImageFromNet(String url) {
        HttpURLConnection connection = null;
        try {
            URL mUrl = new URL(url);
            connection = (HttpURLConnection) mUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(5000);
            connection.connect();
            int resCode = connection.getResponseCode();
            Log.i("SmtVideoActivityrescode", String.valueOf(resCode));
            if (resCode == 200) {
                InputStream is = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Log.i("SmtVideoActivity", "get image from net");
                return bitmap;
            }else {
                Log.i("SmtVideoActivity", "no get image from net: "+resCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    private void setImage(int x, int y) {

        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(infoimage.getLayoutParams());
        margin.setMargins(x, y, 0, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        infoimage.setLayoutParams(layoutParams);
    }

    public void switchVideo()
    {
        Intent intente =new Intent(this,VideoSwitchActivity.class);
        startActivity(intente);
        finish();

    }
    public void stopRender(){
        String sendData = "{\"type\" : \"render\", \"format\" : {\"name\" : \"\"}}";
        int localport=8811;
        String localIp=getLocalIpAddr();
        sendData(localIp,localport,sendData);
    }
    public void sendData()
    {
       /* String sendData = bluetoothdata.getText().toString();
        try {
            String[] ss = bluetoothClientIp.split("\\:");
            int port = 8000;
            InetAddress server_ipaddr = InetAddress.getByName(ss[0]);
            DatagramSocket sendsocket = new DatagramSocket(port);
            byte data[] = sendData.getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(data, data.length, server_ipaddr, port);
            sendsocket.send(packet);
            sendsocket.close();
            bluetoothdata.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            bluetoothdata.setText("");
        }*/
         String sendData = "{\"type\" : \"render\", \"format\" : {\"name\" : \"\"}}";
         String ipaddr = getVideoPath();
         Log.i("SmtVideo", ipaddr);
         String[] ss = ipaddr.split("\\//");
         String[] ip=ss[1].toString().split("\\:");
         int port=8080;
         int localport=8811;
         String localIp=getLocalIpAddr();

         sendData(ip[0],port,sendData);
         sendData(localIp,localport,sendData);
    }

    public String getLocalIpAddr(){

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }
    public String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    public void sendData(String ipddr,int port,String command){

        try {
            InetAddress server_ipaddr = InetAddress.getByName(ipddr);
            DatagramSocket sendsocket = new DatagramSocket();
            byte data[] = command.getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(data, data.length, server_ipaddr, port);
            sendsocket.send(packet);
            sendsocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public String getVideoPath()
    {
        SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
        String videopath = smtgetSharedPreferences.getString("smturl","");
        return videopath;
    }
    public void sendCommand(String command)
    {
        String sendCommand = null;
        try {
            String[] ss = bluetoothClientIp.split("\\:");
            int port = Integer.parseInt(ss[1]);
            InetAddress server_ipaddr = InetAddress.getByName(ss[0]);
            DatagramSocket sendsocket = new DatagramSocket(port);
            if(sendCommand == null)
            {
                sendCommand = command +" "+ mVideoPath;
            }
            byte data[] = sendCommand.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, server_ipaddr, port);
            sendsocket.send(packet);
            sendsocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();
        if(myAsyncTask !=null){
            myAsyncTask.cancel(true);
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
        if(myAsyncTask !=null){
            myAsyncTask.cancel(true);
            myAsyncTask = null;
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle_ratio) {
            int aspectRatio = mVideoView.toggleAspectRatio();
            String aspectRatioText = MeasureHelper.getAspectRatioText(this, aspectRatio);
            mToastTextView.setText(aspectRatioText);
            mMediaController.showOnce(mToastTextView);
            return true;
        } else if (id == R.id.action_toggle_player) {
            int player = mVideoView.togglePlayer();
            String playerText = IjkVideoView.getPlayerText(this, player);
            mToastTextView.setText(playerText);
            mMediaController.showOnce(mToastTextView);
            return true;
        } else if (id == R.id.action_toggle_render) {
            int render = mVideoView.toggleRender();
            String renderText = IjkVideoView.getRenderText(this, render);
            mToastTextView.setText(renderText);
            mMediaController.showOnce(mToastTextView);
            return true;
        } else if (id == R.id.action_show_info) {
            mVideoView.showMediaInfo();
        } else if (id == R.id.action_show_tracks) {
            if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
                if (f != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.remove(f);
                    transaction.commit();
                }
                mDrawerLayout.closeDrawer(mRightDrawer);
            } else {
                Fragment f = TracksFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.right_drawer, f);
                transaction.commit();
                mDrawerLayout.openDrawer(mRightDrawer);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        if (mVideoView == null)
            return null;

        return mVideoView.getTrackInfo();
    }

    @Override
    public void selectTrack(int stream) {
        mVideoView.selectTrack(stream);
    }

    @Override
    public void deselectTrack(int stream) {
        mVideoView.deselectTrack(stream);
    }

    @Override
    public int getSelectedTrack(int trackType) {
        if (mVideoView == null)
            return -1;

        return mVideoView.getSelectedTrack(trackType);
    }

}
