package tv.danmaku.ijk.media.example.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.application.AppActivity;
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


public class SmtActivity extends AppCompatActivity {
    private EditText smtURL;
    private WebView myWebView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smt);

        Intent serviceIntent = new Intent(this, SmtListenService.class);
        startService(serviceIntent);

        Intent starcoreIntent = new Intent(this, StarcoreService.class);
        startService(starcoreIntent);

        myWebView = (WebView) findViewById(R.id.myWebView);
        smtURL = (EditText)findViewById(R.id.smtText);
        Button smtplay_button = (Button)findViewById(R.id.smtplay);
        Button play_button = (Button)findViewById(playfile);
        Button smtset_button = (Button)findViewById(R.id.smtset);
        Button smttv_button = (Button)findViewById(R.id.smttv);
        Button smtav_button = (Button)findViewById(R.id.smtav);
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
            smtplay(smturl,"");
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
        smtset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smtset_button();
            }
        });
        smttv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smturl = smtURL.getText().toString();
                SharedPreferences smtSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
                SharedPreferences.Editor smteditor = smtSharedPreferences.edit();
                smteditor.putString("smturl",smturl);
                smteditor.commit();
                if(smturl.isEmpty())
                    smturl = "127.0.0.1:1234";
                smttvcontrol(smturl,"");
            }
        });
        smtav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smturl = smtURL.getText().toString();
                SharedPreferences smtSharedPreferences = getSharedPreferences("smtset", Activity.MODE_PRIVATE);
                SharedPreferences.Editor smteditor = smtSharedPreferences.edit();
                smteditor.putString("smturl",smturl);
                smteditor.commit();
                if(smturl.isEmpty())
                    smturl = "127.0.0.1:1234";
                smtavcontrol(smturl,"");
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
    public void smtplay(String url,String ipaddr){

        VideoActivity.intentTo(this, url,ipaddr);
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
}
