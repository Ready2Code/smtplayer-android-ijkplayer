package tv.danmaku.ijk.media.example.services;


import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.srplab.www.starcore.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class StarcoreService extends Service{

    public Handler handler;

    private StarCoreFactory starcore;
    private StarServiceClass starService;
    private StarSrvGroupClass SrvGroup;

    private void copyFile(Service c, String Name,String desPath) throws IOException {
        File outfile = null;
        if( desPath != null )
            outfile = new File("/data/data/"+getPackageName()+"/files/"+desPath+Name);
        else
            outfile = new File("/data/data/"+getPackageName()+"/files/"+Name);
        if (!outfile.exists()) {
            outfile.createNewFile();
            FileOutputStream out = new FileOutputStream(outfile);
            byte[] buffer = new byte[1024];
            InputStream in;
            int readLen = 0;
            in = c.getAssets().open(Name);
            while((readLen = in.read(buffer)) != -1){
                out.write(buffer, 0, readLen);
            }
            out.flush();
            in.close();
            out.close();
        }
    }

    private static boolean CreatePath(String Path){
        File destCardDir = new File(Path);
        if(!destCardDir.exists()){
            int Index = Path.lastIndexOf(File.separator.charAt(0));
            if( Index < 0 ){
                if( destCardDir.mkdirs() == false )
                    return false;
            }else{
                String ParentPath = Path.substring(0, Index);
                if( CreatePath(ParentPath) == false )
                    return false;
                if( destCardDir.mkdirs() == false )
                    return false;
            }
        }
        return true;
    }

    private static boolean unzip(InputStream zipFileName, String outputDirectory,Boolean OverWriteFlag ) {
        try {
            ZipInputStream in = new ZipInputStream(zipFileName);
            ZipEntry entry = in.getNextEntry();
            byte[] buffer = new byte[1024];
            while (entry != null) {
                File file = new File(outputDirectory);
                file.mkdir();
                if (entry.isDirectory()) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - 1);
                    if( CreatePath(outputDirectory + File.separator + name) == false )
                        return false;
                } else {
                    String name = outputDirectory + File.separator + entry.getName();
                    int Index = name.lastIndexOf(File.separator.charAt(0));
                    if( Index < 0 ){
                        file = new File(outputDirectory + File.separator + entry.getName());
                    }else{
                        String ParentPath = name.substring(0, Index);
                        if( CreatePath(ParentPath) == false )
                            return false;
                        file = new File(outputDirectory + File.separator + entry.getName());
                    }
                    if( !file.exists() || OverWriteFlag == true){
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        int readLen = 0;
                        while((readLen = in.read(buffer)) != -1){
                            out.write(buffer, 0, readLen);
                        }
                        out.close();
                    }
                }
                entry = in.getNextEntry();
            }
            in.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        File destDir = new File("/data/data/"+getPackageName()+"/files");
        if(!destDir.exists())
            destDir.mkdirs();
        java.io.File python2_7_libFile = new java.io.File("/data/data/"+getPackageName()+"/files/python2.7.zip" );
        if( !python2_7_libFile.exists() ){
            try{
                copyFile(this,"python2.7.zip",null);
            }
            catch(Exception e){
            }
        }
        try{
            AssetManager assetManager = getAssets();
            InputStream dataSource = assetManager.open("django.zip");
            unzip(dataSource, "/data/data/"+getPackageName()+"/files",false );
            dataSource.close();

            dataSource = assetManager.open("smt_system.zip");
            unzip(dataSource, "/data/data/"+getPackageName()+"/files/smt_system",false );
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        try{
            copyFile(this,"_ctypes.so",null);
            copyFile(this,"zlib.so",null);
            copyFile(this,"_json.so",null);
            copyFile(this,"_random.so",null);
            copyFile(this,"_socket.so",null);
            copyFile(this,"_sqlite3.so",null);
            copyFile(this,"select.so",null);
            copyFile(this,"time.so",null);
            copyFile(this,"unicodedata.so",null);
            copyFile(this,"datetime.so",null);
            copyFile(this,"fcntl.so",null);
            copyFile(this,"_struct.so",null);
            copyFile(this,"binascii.so",null);
            copyFile(this,"_collections.so",null);
            copyFile(this,"operator.so",null);
            copyFile(this,"itertools.so",null);
            copyFile(this,"_functools.so",null);
            copyFile(this,"math.so",null);
            copyFile(this,"_io.so",null);
            copyFile(this,"cStringIO.so",null);
            copyFile(this,"_hashlib.so",null);
            copyFile(this,"array.so",null);
            copyFile(this,"cPickle.so",null);
            copyFile(this,"manage.py","smt_system/client/");
            copyFile(this,"signal_client.py","smt_system/client/controller/");
        }
        catch(Exception e){
            System.out.println(e);
        }

        StarCoreFactoryPath.StarCoreCoreLibraryPath = this.getApplicationInfo().nativeLibraryDir;
        StarCoreFactoryPath.StarCoreShareLibraryPath = this.getApplicationInfo().nativeLibraryDir;
        StarCoreFactoryPath.StarCoreOperationPath = "/data/data/"+getPackageName()+"/files";

        final String LibPath = this.getApplicationInfo().nativeLibraryDir;
        final Service GService = this;

        new Thread(new Runnable(){
            @Override
            public void run() {
                starcore= StarCoreFactory.GetFactory();
                starService=starcore._InitSimple("test","123",0,0);


                SrvGroup = (StarSrvGroupClass)starService._Get("_ServiceGroup");
                starService._CheckPassword(false);

		        /*----run python code----*/
                SrvGroup._InitRaw("python",starService);
                StarObjectClass python = starService._ImportRawContext("python","",false,"");
                python._Call("import", "sys");

                StarObjectClass pythonSys = python._GetObject("sys");  // call sys object
                StarObjectClass pythonPath = (StarObjectClass)pythonSys._Get("path");
                pythonPath._Call("insert",0,"/data/data/"+getPackageName()+"/files/python2.7.zip");
                pythonPath._Call("insert",0,LibPath);
                pythonPath._Call("insert",0,"/data/data/"+getPackageName()+"/files");
                pythonPath._Call("insert",0,"/data/data/"+getPackageName()+"/files/django");

                pythonPath._Call("insert",0,"/data/data/"+getPackageName()+"/files/smt_system");
                pythonPath._Call("insert",0,"/data/data/"+getPackageName()+"/files/smt_system/client");
                pythonPath._Call("insert",0,"/data/data/"+getPackageName()+"/files/smt_system/common");
                pythonPath._Call("insert",0,"/data/data/"+getPackageName()+"/files/smt_system/related");

                python._Set("StarcoreService", GService);

                starService._DoFile("python", "/data/data/"+getPackageName()+"/files/smt_system/client/manage.py", "");
                Log.i("StarcoreService","django running...");
            }
        }).start();
        Log.i("StarcoreService","Service onBing");

    }

    public void OnDJangoInitFinish()
    {
        Message message = handler.obtainMessage();
        message.what = 0;
        message.obj = null;
        handler.sendMessage(message);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SrvGroup._ClearService();
        starcore._ModuleExit();

    }


}
