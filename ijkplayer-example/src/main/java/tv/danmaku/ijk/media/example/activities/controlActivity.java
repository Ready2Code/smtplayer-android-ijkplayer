package tv.danmaku.ijk.media.example.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.application.Settings;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class controlActivity extends AppCompatActivity {
    private int recvport;
    private byte[]recvbuff;
    private  String recvdata;
    private Settings mSettings;
    private String smturl;
    private boolean switchflag;
    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, controlActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (mSettings == null) {
            mSettings = new Settings(this);
        }
        smturl = getIntent().getStringExtra("videoPath");
        recvport = 8080;
        recvbuff = new byte[32];
        recvdata="-1";
        switchflag = true;


        final Button smt_mubutton = (Button) findViewById(R.id.mubutton);
        final Button smt_avbutton = (Button) findViewById(R.id.avbutton);
        final Button smt_switch_control_button = (Button) findViewById(R.id.smt_switch_control_button);
        smt_avbutton.setEnabled(true);
        smt_mubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smttvcontrol(smturl,"");
            }
        });
        smt_avbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smtavcontrol(smturl,"");

            }
        });
        smt_switch_control_button.setText("Y");
        smt_switch_control_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchflag == false){
                    switchflag = true;
                    smt_switch_control_button.setText("Y");
                }else{
                    switchflag = false;
                    smt_switch_control_button.setText("N");
                }
            }
        });
//        new Thread(new Runnable() {//recv data thread
//            @Override
//            public void run() {
//                while (true){
//                    receivedata();
//                    if(Integer.parseInt(recvdata) == 1)
//                    {
//                        smt_avbutton.setEnabled(true);
//                    }
//                    else if(Integer.parseInt(recvdata) == 2)
//                    {
//                        smt_avbutton.setEnabled(false);
//                    }
//                }
//
//            }
//        }).start();

    }
    public void smtavguistate(String index){

    }
    public void smttvcontrol(String url,String ipaddr){
        String runcommand = "r1";
        if(switchflag)
          sendCommand(runcommand);
        //smtTvActivity.intentTo(this, url,ipaddr);
    }
    public void smtavcontrol(String url,String ipaddr){
        String runcommand = "r2";
        if(switchflag)
          sendCommand(runcommand);
        //smtAvActivity.intentTo(this, url,ipaddr);
    }
    public void receivedata(){
        try {
            Thread.sleep(1000);
            DatagramSocket recvsocket = new DatagramSocket(recvport);
            DatagramPacket packet = new DatagramPacket(recvbuff,recvbuff.length);
            recvsocket.receive(packet);
            recvdata = new String(packet.getData());
            recvsocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void sendCommand(String command){
        String sendCommand = command;
        String ffplayclient = mSettings.getFfplayClientIp();
        try {

            String[] ss = ffplayclient.split("\\:");
            int port = Integer.parseInt(ss[1]);
            InetAddress server_ipaddr = InetAddress.getByName(ss[0]);
            DatagramSocket sendsocket = new DatagramSocket(port);

            byte data[] = sendCommand.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, server_ipaddr, port);
            sendsocket.send(packet);
            sendsocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}
