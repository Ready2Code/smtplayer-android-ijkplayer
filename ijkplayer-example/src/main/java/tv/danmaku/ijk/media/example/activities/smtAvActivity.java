package tv.danmaku.ijk.media.example.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.net.Uri;
import android.widget.EditText;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.application.Settings;

public class smtAvActivity extends AppCompatActivity {
    private boolean  clickflagfly;
    private boolean  clickflagcoca;
    private boolean  clickflagmac;
    private String addcommand;
    private int resid;
    private int resid_fly;
    private int resid_coca;
    private int resid_mac;

    private Settings mSettings;

    private EditText editText0 ;
    private EditText  editText1 ;
    private EditText  editText2 ;
    private EditText  editText3 ;
    private EditText  editText4 ;
    private EditText  editText5 ;
    private EditText  editText6 ;
    private EditText  editText7 ;
    private EditText  editText8 ;



    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, smtAvActivity.class);
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
        setContentView(R.layout.activity_smt_av);
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

        final Button smt_fly = (Button) findViewById(R.id.avfly);
        final Button smt_coca = (Button) findViewById(R.id.avcoca);
        final Button smt_mac = (Button) findViewById(R.id.avmac);
        final Button smt_mainav = (Button) findViewById(R.id.broadcastav);
        final Button smt_ad_setbutton = (Button) findViewById(R.id.ad_button);
        clickflagfly = true;
        clickflagcoca  = true;
        clickflagmac   = true;
        resid = R.drawable.coffe;
        resid_fly = R.drawable.fly;
        resid_coca = R.drawable.coca;
        resid_mac = R.drawable.mac;

        smt_fly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(clickflagfly==true) {
                    getAddCommand(resid_fly);
                    sendCommand(addcommand);
                    smt_fly.setBackgroundResource(resid);
                    smt_mainav.setBackgroundResource(resid_fly);
                    int tmpresid = resid;
                    resid = resid_fly;
                    resid_fly = tmpresid;
                    clickflagfly= false;
                }
                else {
                   // String addcommand ="{\"type\": \"add\",\"server\":\"202.120.39.169:11071\",\"format\":{\"name\":\"smt://127.0.0.1:21451\",\"posx\":0,\"posy\":0,\"width\":1920,\"height\":1080}}";
                    getAddCommand(resid_fly);
                    sendCommand(addcommand);

                    smt_fly.setBackgroundResource(resid);
                    smt_mainav.setBackgroundResource(resid_fly);
                    int tmpresid = resid;
                    resid = resid_fly;
                    resid_fly = tmpresid;
                    clickflagfly= true;
                }
            }
        });
        smt_mainav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URL="";
                SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset1", Activity.MODE_PRIVATE);

                if(resid == R.drawable.fly)
                    URL = smtgetSharedPreferences.getString("smturl4","");//"http://172.16.5.19/AD2.html";
                else if (resid == R.drawable.coca)
                    URL = smtgetSharedPreferences.getString("smturl5","");//"http://172.16.5.19/AD4.html";
                else if (resid == R.drawable.mac)
                    URL = smtgetSharedPreferences.getString("smturl6","");//"http://172.16.5.19/AD3.html";
                else if (resid == R.drawable.coffe)
                    URL = smtgetSharedPreferences.getString("smturl7","");;//"http://172.16.5.19/AD1.html";
                openurl(URL);
            }
        });
        smt_coca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickflagcoca==true) {
//                    String addcommand ="{\"type\": \"add\",\"server\":\"202.120.39.169:11073\",\"format\":{\"name\":\"smt://127.0.0.1:21453\",\"posx\":0,\"posy\":0,\"width\":1920,\"height\":1080}}";
//                    sendCommand(addcommand);
                    getAddCommand(resid_coca);
                    sendCommand(addcommand);
                    smt_coca.setBackgroundResource(resid);
                    smt_mainav.setBackgroundResource(resid_coca);
                    int tmpresid = resid;
                    resid = resid_coca;
                    resid_coca = tmpresid;
                    clickflagcoca= false;
                }
                else {
                    //String addcommand ="{\"type\": \"add\",\"server\":\"202.120.39.169:11073\",\"format\":{\"name\":\"smt://127.0.0.1:5678\",\"posx\":0,\"posy\":0,\"width\":1920,\"height\":1080}}";
                    //sendCommand(addcommand);
                    getAddCommand(resid_coca);
                    sendCommand(addcommand);
                    smt_coca.setBackgroundResource(resid);
                    smt_mainav.setBackgroundResource(resid_coca);
                    int tmpresid = resid;
                    resid = resid_coca;
                    resid_coca = tmpresid;
                    clickflagcoca= true;
                }
            }
        });
        smt_mac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickflagmac==true) {
//                    String addcommand ="{\"type\": \"add\",\"server\":\"202.120.39.169:11072\",\"format\":{\"name\":\"smt://127.0.0.1:21452\",\"posx\":0,\"posy\":0,\"width\":1920,\"height\":1080}}";//"add 202.120.39.169:23458 smt://127.0.0.1:8001,2650,100,1080,608";
//                    sendCommand(addcommand);
                    getAddCommand(resid_mac);
                    sendCommand(addcommand);
                    smt_mac.setBackgroundResource(resid);
                    smt_mainav.setBackgroundResource(resid_mac);
                    int tmpresid = resid;
                    resid = resid_mac;
                    resid_mac = tmpresid;
                    clickflagmac= false;
                }
                else {
                   // String addcommand ="{\"type\": \"add\",\"server\":\"202.120.39.169:23458\",\"format\":{\"name\":\"smt://127.0.0.1:5678\",\"posx\":0,\"posy\":0,\"width\":1920,\"height\":1080}}";
                    //sendCommand(addcommand);
                    getAddCommand(resid_mac);
                    sendCommand(addcommand);

                    smt_mac.setBackgroundResource(resid);
                    smt_mainav.setBackgroundResource(resid_mac);
                    int tmpresid = resid;
                    resid = resid_mac;
                    resid_mac = tmpresid;
                    clickflagmac= true;
                }
            }
        });
        smt_ad_setbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatSetDialog();
            }
        });
    }
    public void sendCommand(String command){
        String sendCommand = command;
        SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset1", Activity.MODE_PRIVATE);
        String ffplayclient = smtgetSharedPreferences.getString("smturl8","");
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
    public String getAddCommand(int resid)
    {
        String type;
        String server="";
        String name="";
        int posx;
        int posy;
        int width;
        int height;
        String url;
        SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset1", Activity.MODE_PRIVATE);
        posx = 0;
        posy = 0;
        width = 1920;
        height = 1080;
        type = "add";
        if(resid ==  R.drawable.fly) {
            url = smtgetSharedPreferences.getString("smturl3","");
            String[] ss = url.split("\\@");
            server = ss[1];
            name = ss[0];
           // splitSmtUrl(url);
            //addcommand = "{\"type\": \"add\",\"server\":\"202.120.39.169:11074\",\"format\":{\"name\":\"smt://127.0.0.1:21354\",\"posx\":0,\"posy\":0,\"width\":1920,\"height\":1080}}";//"add 202.120.39.169:23458 smt://127.0.0.1:8001,2650,100,1080,608";
        }
        else if(resid ==  R.drawable.coffe) {
            url = smtgetSharedPreferences.getString("smturl0","");
            String[] ss = url.split("\\@");
            server = ss[1];
            name = ss[0];
           // addcommand = "{\"type\": \"add\",\"server\":\"202.120.39.169:11071\",\"format\":{\"name\":\"smt://127.0.0.1:21351\",\"posx\":0,\"posy\":0,\"width\":1920,\"height\":1080}}";//"add 202.120.39.169:23458 smt://127.0.0.1:8001,2650,100,1080,608";
        }
        else if (resid == R.drawable.mac){
            url = smtgetSharedPreferences.getString("smturl1","");
            String[] ss = url.split("\\@");
            server = ss[1];
            name = ss[0];
               // addcommand = "{\"type\": \"add\",\"server\":\"202.120.39.169:11072\",\"format\":{\"name\":\"smt://127.0.0.1:21352\",\"posx\":0,\"posy\":0,\"width\":1920,\"height\":1080}}";//"add 202.120.39.169:23458 smt://127.0.0.1:8001,2650,100,1080,608";
        }
        else if(resid ==  R.drawable.coca) {
            url = smtgetSharedPreferences.getString("smturl2","");
            String[] ss = url.split("\\@");
            server = ss[1];
            name = ss[0];
           // addcommand = "{\"type\": \"add\",\"server\":\"202.120.39.169:11073\",\"format\":{\"name\":\"smt://127.0.0.1:21353\",\"posx\":0,\"posy\":0,\"width\":1920,\"height\":1080}}";//"add 202.120.39.169:23458 smt://127.0.0.1:8001,2650,100,1080,608";
        }
        addcommand = mSettings.creadJsonCommand(type,server,name,posx,posy,width,height);
        return addcommand;
    }
    public void openurl(String uri){
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(it);
    }
    public  void creatSetDialog()
    {
        final AlertDialog dialog;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View configdialogview = layoutInflater.inflate(R.layout.smt_configdialog,null);
        editText0 = (EditText)configdialogview.findViewById(R.id.smtText0);
        editText1 = (EditText)configdialogview.findViewById(R.id.smtText1);
        editText2 = (EditText)configdialogview.findViewById(R.id.smtText2);
        editText3 = (EditText)configdialogview.findViewById(R.id.smtText3);
        editText4 = (EditText)configdialogview.findViewById(R.id.smtText4);
        editText5 = (EditText)configdialogview.findViewById(R.id.smtText5);
        editText6 = (EditText)configdialogview.findViewById(R.id.smtText6);
        editText7 = (EditText)configdialogview.findViewById(R.id.smtText7);
        editText8 = (EditText)configdialogview.findViewById(R.id.smtText8);

        AlertDialog.Builder configdialog = new AlertDialog.Builder(this);
        configdialog.setTitle("smtconfig:");
        configdialog.setView(configdialogview);
        Button button_ok = (Button)configdialogview.findViewById(R.id.ok);
        Button button_cancel = (Button)configdialogview.findViewById(R.id.cancel);

        SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset1", Activity.MODE_PRIVATE);
        editText0.setText(smtgetSharedPreferences.getString("smturl0",""));
        editText1.setText(smtgetSharedPreferences.getString("smturl1",""));
        editText2.setText(smtgetSharedPreferences.getString("smturl2",""));
        editText3.setText(smtgetSharedPreferences.getString("smturl3",""));
        editText4.setText(smtgetSharedPreferences.getString("smturl4",""));
        editText5.setText(smtgetSharedPreferences.getString("smturl5",""));
        editText6.setText(smtgetSharedPreferences.getString("smturl6",""));
        editText7.setText(smtgetSharedPreferences.getString("smturl7",""));
        editText8.setText(smtgetSharedPreferences.getString("smturl8",""));
        dialog = configdialog.show();

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save config data
                String smturl0 = editText0.getText().toString();
                String smturl1 = editText1.getText().toString();
                String smturl2 = editText2.getText().toString();
                String smturl3 = editText3.getText().toString();
                String smturl4 = editText4.getText().toString();
                String smturl5 = editText5.getText().toString();
                String smturl6 = editText6.getText().toString();
                String smturl7 = editText7.getText().toString();
                String smturl8 = editText8.getText().toString();

                SharedPreferences smtSharedPreferences = getSharedPreferences("smtset1", Activity.MODE_PRIVATE);
                SharedPreferences.Editor smteditor = smtSharedPreferences.edit();
                smteditor.putString("smturl0",smturl0);
                smteditor.putString("smturl1",smturl1);
                smteditor.putString("smturl2",smturl2);
                smteditor.putString("smturl3",smturl3);
                smteditor.putString("smturl4",smturl4);
                smteditor.putString("smturl5",smturl5);
                smteditor.putString("smturl6",smturl6);
                smteditor.putString("smturl7",smturl7);
                smteditor.putString("smturl8",smturl8);
                smteditor.commit();
                dialog.dismiss();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
