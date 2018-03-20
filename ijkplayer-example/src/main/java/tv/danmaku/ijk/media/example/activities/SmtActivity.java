package tv.danmaku.ijk.media.example.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.application.AppActivity;
import tv.danmaku.ijk.media.example.application.Settings;
import tv.danmaku.ijk.media.player.MMTtool.MMTtoolApi;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import tv.danmaku.ijk.media.example.services.SmtListenService;
import tv.danmaku.ijk.media.example.services.StarcoreService;
import static tv.danmaku.ijk.media.example.R.id.playfile;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.SocketException;

public class SmtActivity extends AppCompatActivity {
    private EditText smtURL;
    private WebView myWebView;
    private Settings mSettings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smt);



        myWebView = (WebView) findViewById(R.id.myWebView);
        smtURL = (EditText)findViewById(R.id.smtText);
        Button smtplay_button = (Button)findViewById(R.id.smtplay);
        Button play_button = (Button)findViewById(playfile);
        Button smtset_button = (Button)findViewById(R.id.smtset);
        Button MMTtool_button = (Button)findViewById(R.id.MMTtool);
        Button Smt_system_button = (Button)findViewById(R.id.Smt_system);
        Button smtstart_button = (Button)findViewById(R.id.smtstart);
        Button fileexplorer_button = (Button)findViewById(R.id.fileexplorer);

        SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
        smtURL.setText(smtgetSharedPreferences.getString("smturl",""));
        smtplay_button.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View view) {
            String smturl = smtURL.getText().toString();
            SharedPreferences smtSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
            SharedPreferences.Editor smteditor = smtSharedPreferences.edit();
            smteditor.putString("smturl",smturl);
            smteditor.commit();
            if(smturl.isEmpty())
                smturl = "127.0.0.1:1234";
            Log.i("Smt", smturl);
            smtplay(smturl,"","");
            }
        });
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smturl = smtURL.getText().toString();
                SharedPreferences smtSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
                SharedPreferences.Editor smteditor = smtSharedPreferences.edit();
                smteditor.putString("smturl",smturl);
                smteditor.commit();
                if(smturl.isEmpty())
                    smturl = "127.0.0.1:1234";
                String[] ss = smturl.split(":");
//                if(ss[0].equals("lte")){
//                    smturl=ss[1]+":"+ss[2];
//                    smturl= "http://"+smturl+"/current_program/";
//                }else{
//                    smturl= "http://"+smturl+"/show_channels/related_operator/";
//                }
//
//               // smturl= "http://"+smturl+"/show_channels/related_operator/";
//              //  smturl= "http://"+smturl+"/current_program/";
                SmtWebplay(smturl,"");
            }
        });
        if (mSettings == null) {
            mSettings = new Settings(this);
        }
        smtset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smtset_button();
            }
        });
        MMTtool_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                class MyThread extends Thread {
                    public void run(){
                        String srcip=mSettings.getFfplayClientIp();
                        if(srcip.isEmpty())
                            return;
                        String [] ss = srcip.split(":");
                        srcip=ss[0];
                        String srcport=ss[1];
                        String localip=getLocalIp();//get wirenet ip
                        if(localip == ""){
                         localip=getLocalIpAddr(); //get wifi ip
                        }
                        MMTtoolApi mmttoolapi = new MMTtoolApi();
                        String cmd="--ts2ip --srcip "+srcip+"  --srcport "+srcport+" --dstip "+localip+"  --dstport 0";
                       // mmttoolapi.run_MMTtool("--ts2ip --srcip 192.168.100.233 --srcport 3006 --dstip 127.0.0.1 --dstport 0");
                         mmttoolapi.run_MMTtool(cmd);

                    }
                }
                new MyThread().start();
            }
        });
        Smt_system_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(SmtActivity.this, SmtListenService.class);
                startService(serviceIntent);

                Intent starcoreIntent = new Intent(SmtActivity.this, StarcoreService.class);
                startService(starcoreIntent);

            }
        });
        smtstart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smturl = smtURL.getText().toString();
                SharedPreferences smtSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
                SharedPreferences.Editor smteditor = smtSharedPreferences.edit();
                smteditor.putString("smturl",smturl);
                smteditor.commit();
                if(smturl.isEmpty())
                    smturl = "127.0.0.1:1234";
                smtopencontrolctivity(smturl,"");
            }
        });

        fileexplorer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(SmtActivity.this, FileExplorerActivity.class);
                SmtActivity.this.startActivity(it);
            }
    });

    }
    public void smtplay(String url,String ipaddr,String videotype){

        VideoActivity.intentTo(this, url,ipaddr,videotype);
    }
    public void smttvcontrol(String url,String ipaddr){

        //smtTvActivity.intentTo(this, url,ipaddr);
    }
    public void smtavcontrol(String url,String ipaddr){

        //smtAvActivity.intentTo(this, url,ipaddr);
    }
    public void SmtWebplay(String url,String ipaddr){

        SmtWebActivity.intentTo(this, url,ipaddr);
    }
    public void playfileactivity(){

        String smturl = smtURL.getText().toString();
        smtURL.setVisibility(View.GONE);
        SharedPreferences smtSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
        SharedPreferences.Editor smteditor = smtSharedPreferences.edit();
        smteditor.putString("smturl",smturl);
        smteditor.commit();
        smturl= "http://"+smturl+"/show_channels/related_operator/";
        myWebView.loadUrl(smturl);
    }
    public void smtset_button(){

        Intent intente =new Intent(this,SettingsActivity.class);
        startActivity(intente);
    }

    public void smtopencontrolctivity(String url,String ipaddr){
        controlActivity.intentTo(this, url,ipaddr);
    }
    //get wifi ip
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
    //get wirenet ip
    public String getLocalIp() {

        try {
            // 获取本地设备的所有网络接口
            Enumeration<NetworkInterface> enumerationNi = NetworkInterface.getNetworkInterfaces();
            while (enumerationNi.hasMoreElements()) {
                NetworkInterface networkInterface = enumerationNi.nextElement();
                String interfaceName = networkInterface.getDisplayName();
                Log.i("tag", "网络名字" + interfaceName);

                // 如果是有限网卡
                if (interfaceName.equals("eth0")) {
                    Enumeration<InetAddress> enumIpAddr = networkInterface
                            .getInetAddresses();

                    while (enumIpAddr.hasMoreElements()) {
                        // 返回枚举集合中的下一个IP地址信息
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        // 不是回环地址，并且是ipv4的地址
                        if (!inetAddress.isLoopbackAddress()
                                && inetAddress instanceof Inet4Address) {
                            Log.i("tag", inetAddress.getHostAddress() + "   ");

                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";

    }

}
