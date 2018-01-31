/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.example.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import tv.danmaku.ijk.media.example.R;

public class Settings {
    private Context mAppContext;
    private SharedPreferences mSharedPreferences;

    public static final int PV_PLAYER__Auto = 0;
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;
    public static final int PV_PLAYER__IjkExoMediaPlayer = 3;

    public Settings(Context context) {
        mAppContext = context.getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public boolean getEnableBackgroundPlay() {
        String key = mAppContext.getString(R.string.pref_key_enable_background_play);
        return mSharedPreferences.getBoolean(key, false);
    }

    public int getPlayer() {
        String key = mAppContext.getString(R.string.pref_key_player);
        String value = mSharedPreferences.getString(key, "");
        try {
            return Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean getUsingMediaCodec() {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getUsingMediaCodecAutoRotate() {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec_auto_rotate);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getUsingOpenSLES() {
        String key = mAppContext.getString(R.string.pref_key_using_opensl_es);
        return mSharedPreferences.getBoolean(key, false);
    }

    public String getPixelFormat() {
        String key = mAppContext.getString(R.string.pref_key_pixel_format);
        return mSharedPreferences.getString(key, "");
    }

    public boolean getEnableNoView() {
        String key = mAppContext.getString(R.string.pref_key_enable_no_view);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getEnableSurfaceView() {
        String key = mAppContext.getString(R.string.pref_key_enable_surface_view);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getEnableTextureView() {
        String key = mAppContext.getString(R.string.pref_key_enable_texture_view);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getEnableDetachedSurfaceTextureView() {
        String key = mAppContext.getString(R.string.pref_key_enable_detached_surface_texture);
        return mSharedPreferences.getBoolean(key, false);
    }

    public String getLastDirectory() {
        String key = mAppContext.getString(R.string.pref_key_last_directory);
        return mSharedPreferences.getString(key, "/");
    }

    public void setLastDirectory(String path) {
        String key = mAppContext.getString(R.string.pref_key_last_directory);
        mSharedPreferences.edit().putString(key, path).apply();
    }
    public String getNtpServerIp() {
        String key = mAppContext.getString(R.string.pref_key_ntp_server_ip);
        return mSharedPreferences.getString(key,"");
    }
    public String getSocketClientIp() {
        String key = mAppContext.getString(R.string.pref_key_socket_client_ip);
        return mSharedPreferences.getString(key,"");
    }
    public boolean getEnableBluetoothSendData() {
        String key = mAppContext.getString(R.string.pref_key_enable_bluetooth);
        return mSharedPreferences.getBoolean(key, true);
    }
    public boolean getEnablePlayFile() {
        String key = mAppContext.getString(R.string.pref_key_enable_playfile);
        return mSharedPreferences.getBoolean(key, true);
    }
    public String getLogServerIp() {
        String key = mAppContext.getString(R.string.pref_key_log_server_ip);
        return mSharedPreferences.getString(key,"");
    }
    public String getFfplayClientIp() {
        String key = mAppContext.getString(R.string.pref_key_ffplay_client_ip);
        return mSharedPreferences.getString(key,"");
    }
    public  String creadJsonCommand(String type,String server,String name,int posx,int posy,int width,int height)
    {
        JSONObject smtcommand = new JSONObject();
        JSONObject format = new JSONObject();
        try {
            smtcommand.put("type", type);
            smtcommand.put("server", server);
            format.put("name", name);
            format.put("posx", posx);
            format.put("posy", posy);
            format.put("width", width);
            format.put("height", height);
            smtcommand.put("format",format);
            return smtcommand.toString();
        }catch (JSONException e){
            return null;
        }
    }

}
