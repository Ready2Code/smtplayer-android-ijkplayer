package tv.danmaku.ijk.media.example.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import tv.danmaku.ijk.media.example.R;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Button;
import android.view.View;
import android.os.Message;
import android.widget.Toast;

public class SmtWebActivity extends SmtActivity {
    private WebView myWebView;
    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, SmtWebActivity.class);
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
        setContentView(R.layout.activity_smt_web);

        myWebView = (WebView) findViewById(R.id.myWebView);
        WebSettings mysettings = myWebView.getSettings();
        mysettings.setSupportZoom(true);
        mysettings.setBuiltInZoomControls(false);
        mysettings.setJavaScriptEnabled(true);
        mysettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mysettings.setUseWideViewPort(true);
        mysettings.setSupportMultipleWindows(true);
        mysettings.setLoadWithOverviewMode(true);
        String smturl =  getIntent().getStringExtra("videoPath");
        myWebView.loadUrl(smturl);
        Button refresh_button = (Button)findViewById(R.id.refresh);
        Button goback_button = (Button)findViewById(R.id.back);
        Button goforward_button = (Button)findViewById(R.id.forward);
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myWebView.reload();
            }
        });
        goback_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 myWebView.goBack();
            }
        });
        goforward_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 myWebView.goForward();
            }
        });
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String loadingUrl) {
                String  url = loadingUrl;
                if (!TextUtils.isEmpty(url) ) {
                    if( url.startsWith("smt://")) {
                        smtplay(loadingUrl, "");
                    } else {
//                        int  ret=url.indexOf("&ad");
//                        if(ret>-1){
//                            String ss[]=url.split("&ad");
//                            Uri uri = Uri.parse(ss[0]);
//                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                            startActivity(intent);
//                            return true;
//                        }
                        view.loadUrl(url);
                    }
                }
                return true;

            }
        });
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                //Toast.makeText(getApplicationContext(), "OnCreateWindow", Toast.LENGTH_LONG).show();
                WebView newWebView = new WebView(view.getContext());
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        String  weburl = url;
                        if (!TextUtils.isEmpty(url) ) {
                            if( weburl.startsWith("smt://")) {
                                smtplay(url, "");
                            } else {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                                return true;

                            }
                        }
                        return true;
                    }
                });
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
              //  return true;
                return true;
            }
        });
       // mWebView.loadUrl("http://www.google.com");
    //}

    }
}
