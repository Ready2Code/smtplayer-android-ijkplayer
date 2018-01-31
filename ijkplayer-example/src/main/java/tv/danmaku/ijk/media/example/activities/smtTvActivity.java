package tv.danmaku.ijk.media.example.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.application.Settings;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class smtTvActivity extends AppCompatActivity {
    private boolean  clickflagpiano;
    private boolean  clickflagcello;
    private boolean  clickflagviolin;

    private boolean  clickflagpad_p;
    private boolean  clickflagpad_c;
    private boolean  clickflagpad_v;

    private String    smtServerIp;
    private int       smtServerPort;
    private String    smtLocalIp;
    private int       smtLocalPort;
    private String    smturl;

    private String type;
    private String server;
    private String name;
    private int posx;
    private int posy;
    private int width;
    private int height;
    private Settings mSettings;
    private EditText  editText0 ;
    private EditText  editText1 ;
    private EditText  editText2 ;
    private EditText  editText3 ;
    private EditText  editText4 ;
    private EditText  editText5 ;
    private EditText  editText6 ;
    private EditText  editText7 ;


    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, smtTvActivity.class);
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
        setContentView(R.layout.activity_smt_tv);
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
        final Button smt_piano = (Button) findViewById(R.id.smtplaypiano);
        final Button smt_cello = (Button) findViewById(R.id.smtplaycello);
        final Button smt_violin = (Button) findViewById(R.id.smtplayviolin);

        final Button pad_p = (Button) findViewById(R.id.button_p);
        final Button pad_c = (Button) findViewById(R.id.button_c);
        final Button pad_v = (Button) findViewById(R.id.button_v);
        final Button smtsetbutton = (Button) findViewById(R.id.set_button);

        clickflagpiano = true;
        clickflagcello = true;
        clickflagviolin = true;
        clickflagpad_p = false;
        clickflagpad_c = false;
        clickflagpad_v = false;
        smt_piano.getBackground().setAlpha(150);
        smt_cello.getBackground().setAlpha(150);
        smt_violin.getBackground().setAlpha(150);
       // smturl = getIntent().getStringExtra("videoPath");
       // splitSmtUrl(smturl);
        smt_piano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String command;
                String url;
                SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset0", Activity.MODE_PRIVATE);
                url = smtgetSharedPreferences.getString("smturl3","");
                splitSmtUrl(url);
                posx = 2650;
                posy = 100;
                width = 1080;
                height = 608;
                if(clickflagpiano==true) {
                    type = "add";
                    //String addcommand  ="{\"type\": \"add\",\"server\":\"202.120.39.169:23458\",\"format\":{\"name\":\"smt://127.0.0.1:8001\",\"posx\":2650,\"posy\":100,\"width\":1080,\"height\":608}}";//"add 202.120.39.169:23458 smt://127.0.0.1:8001,2650,100,1080,608";;//"add 202.120.39.169:23458 smt://127.0.0.1:8001,2650,100,1080,608";
                   // sendCommand(addcommand);
                    smt_piano.setBackgroundResource(R.drawable.gq);
                    clickflagpiano= false;
                }
                else {
                  //  String delcommand  ="{\"type\": \"delete\",\"server\":\"202.120.39.169:23458\",\"format\":{\"name\":\"smt://127.0.0.1:8001\",\"posx\":2650,\"posy\":100,\"width\":1080,\"height\":608}}";
                  //  sendCommand(delcommand);
                    type = "delete";
                    smt_piano.setBackgroundResource(R.drawable.gq1);
                    clickflagpiano= true;
                }
                command = mSettings.creadJsonCommand(type,server,name,posx,posy,width,height);
                sendCommand(command);
            }
        });

        smt_cello.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String command;
                String url;
                SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset0", Activity.MODE_PRIVATE);
                url = smtgetSharedPreferences.getString("smturl4","");
                splitSmtUrl(url);
                posx = 2650;
                posy = 800;
                width = 1080;
                height = 608;

                if(clickflagcello==true) {
                    type = "add";
                   // String addcommand  ="{\"type\": \"add\",\"server\":\"202.120.39.169:23457\",\"format\":{\"name\":\"smt://127.0.0.1:8002\",\"posx\":2650,\"posy\":800,\"width\":1080,\"height\":608}}";
                  //  String addcommand ="add 202.120.39.169:23457 smt://127.0.0.1:8002,2650,800,1080,608";
                  //  sendCommand(addcommand);
                    smt_cello.setBackgroundResource(R.drawable.dtq);
                    clickflagcello= false;
                }
                else {
                   // String delcommand ="del 202.120.39.169:23457 smt://127.0.0.1:8002";
                  //  String delcommand  ="{\"type\": \"delete\",\"server\":\"202.120.39.169:23457\",\"format\":{\"name\":\"smt://127.0.0.1:8002\",\"posx\":2650,\"posy\":800,\"width\":1080,\"height\":608}}";
                   // sendCommand(delcommand);
                    type = "delete";
                    smt_cello.setBackgroundResource(R.drawable.dtq1);
                    clickflagcello= true;
                }
                command = mSettings.creadJsonCommand(type,server,name,posx,posy,width,height);
                sendCommand(command);

            }
        });
        smt_violin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String command;
                String url;
                SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset0", Activity.MODE_PRIVATE);
                url = smtgetSharedPreferences.getString("smturl5","");
                splitSmtUrl(url);
                posx = 2650;
                posy = 1500;
                width = 1080;
                height = 608;

                if(clickflagviolin==true) {
                    type = "add";
                  //  String addcommand = creadJsonCommand(type,server,name,posx,posy,width,height);
                   // String addcommand  ="{\"type\": \"add\",\"server\":\"202.120.39.169:23459\",\"format\":{\"name\":\"smt://127.0.0.1:8003\",\"posx\":2650,\"posy\":1500,\"width\":1080,\"height\":608}}";
                 //   sendCommand(addcommand);
                    smt_violin.setBackgroundResource(R.drawable.xtq);
                    clickflagviolin= false;
                }
                else {
                    type = "delete";
                  //  String delcommand = creadJsonCommand(type,server,name,posx,posy,width,height);
                   // String delcommand  ="{\"type\": \"delete\",\"server\":\"202.120.39.169:23459\",\"format\":{\"name\":\"smt://127.0.0.1:8003\",\"posx\":2650,\"posy\":1500,\"width\":1080,\"height\":608}}";
                  //  sendCommand(delcommand);
                    smt_violin.setBackgroundResource(R.drawable.xtq1);
                    clickflagviolin= true;
                }
                command = mSettings.creadJsonCommand(type,server,name,posx,posy,width,height);
                sendCommand(command);
            }
        });
        pad_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int seq = 1;
                play_pad(seq);
            }
        });
        pad_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int seq = 0;
                play_pad(seq);
            }
        });
        pad_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int seq = 2;
                play_pad(seq);
            }
        });
        smtsetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              creatSetDialog();
            }
        });
    }
    public void sendCommand(String command){
        String sendCommand = command;
        SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset0", Activity.MODE_PRIVATE);
        String ffplayclient = smtgetSharedPreferences.getString("smturl6","");
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
    public void play_pad(int seq)
    {
        String videopath;
        SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset0", Activity.MODE_PRIVATE);
        if(seq==0) {
            videopath = smtgetSharedPreferences.getString("smturl1","");
            VideoActivity.intentTo(this, videopath, "");
        }
        if(seq==1) {
            videopath = smtgetSharedPreferences.getString("smturl0","");
            VideoActivity.intentTo(this, videopath, "");
        }
        if(seq==2) {
            videopath = smtgetSharedPreferences.getString("smturl2","");
            VideoActivity.intentTo(this, videopath, "");
        }
    }
    public void splitSmtUrl(String url)
    {
        String[] ss = url.split("\\@");
        server = ss[1];
        name = ss[0];
    }
//    public  String creadJsonCommand(String type,String server,String name,int posx,int posy,int width,int height)
//    {
//        JSONObject smtcommand = new JSONObject();
//        JSONObject format = new JSONObject();
//        try {
//            smtcommand.put("type", type);
//            smtcommand.put("server", server);
//            format.put("name", name);
//            format.put("posx", posx);
//            format.put("posy", posy);
//            format.put("width", width);
//            format.put("height", height);
//            smtcommand.put("format",format);
//            return smtcommand.toString();
//        }catch (JSONException e){
//            return null;
//        }
//    }
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

        AlertDialog.Builder configdialog = new AlertDialog.Builder(this);
        configdialog.setTitle("smtconfig:");
        configdialog.setView(configdialogview);
        Button button_ok = (Button)configdialogview.findViewById(R.id.ok);
        Button button_cancel = (Button)configdialogview.findViewById(R.id.cancel);

        SharedPreferences smtgetSharedPreferences = getSharedPreferences("smtset0", Activity.MODE_PRIVATE);
        editText0.setText(smtgetSharedPreferences.getString("smturl0",""));
        editText1.setText(smtgetSharedPreferences.getString("smturl1",""));
        editText2.setText(smtgetSharedPreferences.getString("smturl2",""));
        editText3.setText(smtgetSharedPreferences.getString("smturl3",""));
        editText4.setText(smtgetSharedPreferences.getString("smturl4",""));
        editText5.setText(smtgetSharedPreferences.getString("smturl5",""));
        editText6.setText(smtgetSharedPreferences.getString("smturl6",""));
        editText7.setText(smtgetSharedPreferences.getString("smturl7",""));
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

                SharedPreferences smtSharedPreferences = getSharedPreferences("smtset0", Activity.MODE_PRIVATE);
                SharedPreferences.Editor smteditor = smtSharedPreferences.edit();
                smteditor.putString("smturl0",smturl0);
                smteditor.putString("smturl1",smturl1);
                smteditor.putString("smturl2",smturl2);
                smteditor.putString("smturl3",smturl3);
                smteditor.putString("smturl4",smturl4);
                smteditor.putString("smturl5",smturl5);
                smteditor.putString("smturl6",smturl6);
                smteditor.putString("smturl7",smturl7);
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
