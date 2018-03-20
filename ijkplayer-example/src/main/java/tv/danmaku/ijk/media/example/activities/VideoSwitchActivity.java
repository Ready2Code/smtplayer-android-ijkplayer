package tv.danmaku.ijk.media.example.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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

public class VideoSwitchActivity extends AppCompatActivity {
    private String mVideoPath;
    private String mNtpServerIp;
    private String bluetoothClientIp;
    private String mLogServerIp;
    private boolean playfile;
    private Uri    mVideoUri;
    private EditText bluetoothdata;
    private int    SendMode;
    private boolean mEnableBluetooth;

    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private TextView mToastTextView;
    private TableLayout mHudView;
    private DrawerLayout mDrawerLayout;
    private ViewGroup mRightDrawer;

    private Settings mSettings;
    private boolean mBackPressed;
    public static void intentTo(Context context, String videoPath, String videoTitle) {
       // context.startActivity(newIntent(context, videoPath, videoTitle));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
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
             //   sendCommand("del");
             //   SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
             //   String videopath = smtgetSharedPreferences.getString("smturl","");
            //    sendCommand("del");
              //  switchVideo();
                if(SendMode == 0)
                    SendMode = 1;// use socker send data
                else
                    SendMode = 0;
            }
        });
        bluetoothClientIp = mSettings.getSocketClientIp();

        blt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                switchVideo();
                String sendData = bluetoothdata.getText().toString();
                if(SendMode == 0){
                  //  Intent intent = new Intent();
                 //   intent.setAction(ACTION);
                 //   bluetoothdata.setText("");
                  //  intent.putExtra("data", sendData);
                  //  sendBroadcast(intent);
                }else{
//                    String videopath = getVideoPath();
                 //   SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
                 //   String videopath = smtgetSharedPreferences.getString("smturl","");
                   /* try {
                        int port = 8000;
                        InetAddress server_ipaddr = InetAddress.getByName(bluetoothClientIp);
                        DatagramSocket sendsocket = new DatagramSocket(port);
                        byte data[] = sendData.getBytes();
                        DatagramPacket packet = new DatagramPacket(data, data.length, server_ipaddr, port);
                        sendsocket.send(packet);
                        bluetoothdata.setText("");
                        sendsocket.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }


            }

        });
        // handle arguments
      //  mVideoPath = getIntent().getStringExtra("videoPath");
        mVideoPath = getVideoPath();
        //sendCommand("add");
        mNtpServerIp = mSettings.getNtpServerIp();
        mLogServerIp = mSettings.getLogServerIp();
        if(mNtpServerIp.isEmpty())
            mNtpServerIp = "127.0.0.1";
        if(bluetoothClientIp.isEmpty())
            bluetoothClientIp = "127.0.0.1:23458";
        if(mLogServerIp.isEmpty())
            mLogServerIp = "127.0.0.1:1235";
        String[] ss = bluetoothClientIp.split("\\:");
        int smtserver_port = Integer.parseInt(ss[1]);
        smtserver_port += 1;
        mNtpServerIp = mSettings.getNtpServerIp()+":"+ss[0]+":"+Integer.toString(smtserver_port)+":"+mLogServerIp;//":202.120.39.169:23458";
        if(mNtpServerIp.isEmpty())
            mNtpServerIp = "127.0.0.1";

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
                       // Log.e(TAG, "Null unknown scheme\n");
                        finish();
                        return;
                    }
                    if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                        mVideoPath = mVideoUri.getPath();
                    } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                      //  Log.e(TAG, "Can not resolve content below Android-ICS\n");
                        finish();
                        return;
                    } else {
                     //   Log.e(TAG, "Unknown scheme " + scheme + "\n");
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
          //  Log.e(TAG, "Null Data Source\n");
            finish();
            return;
        }
        mVideoView.start();
    }

    public void switchVideo()
    {
        Intent intente =new Intent(this,VideoSwitch2Activity.class);
        startActivity(intente);
        finish();

    }
    public void sendData()
    {
        String sendData = bluetoothdata.getText().toString();
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
        }

    }
    public void sendCommand(String command)
    {
        String sendCommand = null;
        try {
            String[] ss = bluetoothClientIp.split("\\:");
            int port = Integer.parseInt(ss[1]);
            port++;
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
    public String getVideoPath()
    {
        SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
        String videopath = smtgetSharedPreferences.getString("smturl","");
        String[] ss = videopath.split("\\:");
        if(ss.length <= 3){
            int port = Integer.parseInt(ss[2]);
            port++;
            videopath= ss[0]+":"+ss[1]+":"+Integer.toString(port);
            return videopath;
        }

        int port = Integer.parseInt(ss[4]);
        int server_port = Integer.parseInt(ss[3]);
        port++;
        server_port++;
        videopath = ss[0]+":"+ss[1]+":"+ss[2]+":"+Integer.toString(server_port)+":"+Integer.toString(port);
        return videopath;
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

}
