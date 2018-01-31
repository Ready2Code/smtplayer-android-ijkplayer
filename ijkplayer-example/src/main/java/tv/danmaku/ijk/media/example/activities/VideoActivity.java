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
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;


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

    private Settings mSettings;
    private boolean mBackPressed;
    public static VideoActivity videoA;
    private  final static String ACTION="com.sjtu.wefirechat.ACTION_GATT_MESSAGE";
    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }



    public static void intentTo(Context context, String videoPath, String videoTitle) {
        Intent intent = newIntent(context, videoPath, videoTitle);
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
    }
    public void switchVideo()
    {
        Intent intente =new Intent(this,VideoSwitchActivity.class);
        startActivity(intente);
        finish();

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
