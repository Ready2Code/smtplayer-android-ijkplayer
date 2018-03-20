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

public class SmtListenService extends Service {

    private DatagramSocket receiveSocket;
    private boolean listenStatus = true;

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
                        Thread.sleep(500);
                        //lasturl = nowurl;
                        nowurl = ss[1];
                        if (ss[2].equals("broadcast"))
                            smtplay(ss[1],"", "broadcast");
                        else if(ss[2].equals("broadband"))
                            smtplay(ss[1],"", "broadband");
                        else
                            smtplay(ss[1], "", "");

/*
                        DatagramSocket typeSocket = new DatagramSocket(IMAGINPORT);
                        byte[] typeBuf = "";
                        if (ss[2].equals("broadcast"))
                            typeBuf = "broadcast".getBytes();
                        else if (ss[2].equals("broadband"))
                            typeBuf = "broadband".getBytes();
                        DatagramPacket typePacket = new DatagramPacket(typeBuf, typeBuf.length, InetAddress.getByName("lo"))
*/                    }
                    Log.i("SmtListenService","cal:"+ ss[1]);
                }else if(ss[0].equals("del")){
                    if (ss[1].equals(nowurl)) {
                        VideoActivity.videoA.finish();
                        //smtplay(lasturl,"");
                        //nowurl = lasturl;
                        //smtplay(mainviewurl, "");
                        //nowurl = mainviewurl;
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

        VideoActivity.intentTo(this, url,ipaddr, videotype);
    }

}
