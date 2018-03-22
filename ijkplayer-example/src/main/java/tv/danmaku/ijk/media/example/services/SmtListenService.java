package tv.danmaku.ijk.media.example.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import tv.danmaku.ijk.media.example.activities.VideoActivity;
import tv.danmaku.ijk.media.example.activities.VideoSwitchActivity;

public class SmtListenService extends Service {

    private DatagramSocket receiveSocket;
    private boolean listenStatus = true;

<<<<<<< HEAD
=======
    private int IMAGINPORT = 9430;
>>>>>>> cb4030de57c8d91b670a24b20a747c2488c4835d
    private int RECVPORT = 8080;
    private int IMAGINPORT = 9430;


    @Override
    public void onCreate() {
        Log.i("SmtListenService","Service Create!");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SmtListenService","Service onBing");
        new UdpReceiveThread().start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        listenStatus = false;
        receiveSocket.close();
    }



    public class UdpReceiveThread extends Thread {

        String[] ss;
        String lasturl = "";
        String nowurl = "";

        @Override
        public void run() {

            Intent intent = new Intent(SmtListenService.this, VideoActivity.class);

            try{
                //receiveSocket = new DatagramSocket(RECVPORT);

                if (receiveSocket == null) {
                    receiveSocket = new DatagramSocket(null);
                    receiveSocket.setReuseAddress(true);
                    receiveSocket.bind(new InetSocketAddress(RECVPORT));
                }

                byte[] inBuf = new byte[1024];

                while (listenStatus){
                    DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
                    receiveSocket.receive(inPacket);

                    String recvInfo = new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength());
                    if(recvInfo.indexOf("render") !=-1){
                        recvInfo="render-render" ;
                    }
                    ss = recvInfo.split("-");
                    Log.i("ss0@@@@@@@@@@@@@@@@", ss[0]);

<<<<<<< HEAD
=======
/*
                    if (ss[0].equals("cal") || ss[0].equals("add")){
                        Log.i("SmtListenService", "--" + ss[0]);
                        if (!playstatus) {
                            smtplay(ss[1], "");
                            playstatus = true;
                            last_rul = ss[1];
                            Log.i("SmtListenService","cal:"+ ss[1]);
                        }else {
                            smtplay(ss[1], "");
                            Log.i("SmtListenService","cal:"+ ss[1]);
                        }

                    }else if(ss[0].equals("del")){
                        smtplay(last_rul, "");
                        Log.i("SmtListenService","del:"+ ss[1]);
                    }else
                        Log.i("SmtListenService", "--" + ss[0] +"--");
                }
*/
>>>>>>> cb4030de57c8d91b670a24b20a747c2488c4835d
                if (ss[0].equals("cal")){
                    Log.i("SmtListenService", "--" + ss[0]);
                    if (nowurl.equals("")){
                        nowurl = ss[1];
                        if (ss[2].equals("broadcast"))
                            smtplay(ss[1],"", "broadcast");
                        else if(ss[2].equals("broadband"))
                            smtplay(ss[1],"", "broadband");
                        else
                            smtplay(ss[1], "", "");
                    }else {
                        VideoActivity.videoA.finish();
<<<<<<< HEAD
                     //   switchVideo();
                        Thread.sleep(1000);

=======
                        Thread.sleep(500);
>>>>>>> cb4030de57c8d91b670a24b20a747c2488c4835d
                        //lasturl = nowurl;
                        nowurl = ss[1];
                        if (ss[2].equals("broadcast"))
                            smtplay(ss[1],"", "broadcast");
                        else if(ss[2].equals("broadband"))
                            smtplay(ss[1],"", "broadband");
                        else
                            smtplay(ss[1], "", "");
<<<<<<< HEAD

/*
                        DatagramSocket typeSocket = new DatagramSocket(IMAGINPORT);
                        byte[] typeBuf = "";
                        if (ss[2].equals("broadcast"))
                            typeBuf = "broadcast".getBytes();
                        else if (ss[2].equals("broadband"))
                            typeBuf = "broadband".getBytes();
                        DatagramPacket typePacket = new DatagramPacket(typeBuf, typeBuf.length, InetAddress.getByName("lo"))
*/                    }
=======
                    }
>>>>>>> cb4030de57c8d91b670a24b20a747c2488c4835d
                    Log.i("SmtListenService","cal:"+ ss[1]);
                }else if(ss[0].equals("del")){
                    if (ss[1].equals(nowurl)) {
                        VideoActivity.videoA.finish();
                        //smtplay(lasturl,"");
                        //nowurl = lasturl;
<<<<<<< HEAD
                        //smtplay(mainviewurl, "");
                        //nowurl = mainviewurl;
=======
>>>>>>> cb4030de57c8d91b670a24b20a747c2488c4835d
                        Log.i("SmtListenService","del:"+ ss[1]);
                    }
                }else if(ss[0].equals("render")){
                    VideoActivity.videoA.stopRender();
                    Log.i("SmtListenService", "render");
                } else
                    Log.i("SmtListenService", "--" + ss[0] +"--");
            }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void smtplay(String url,String ipaddr, String videotype){
<<<<<<< HEAD

        VideoActivity.intentTo(this, url,ipaddr, videotype);
    }
    public void switchVideo()
    {
        Intent intente =new Intent(this,VideoSwitchActivity.class);
        startActivity(intente);

=======

        VideoActivity.intentTo(this, url,ipaddr, videotype);
>>>>>>> cb4030de57c8d91b670a24b20a747c2488c4835d
    }

}
