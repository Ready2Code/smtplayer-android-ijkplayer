package tv.danmaku.ijk.media.player.MMTtool;

/**
 * Created by jxj on 1/26/18.
 */

public class MMTtoolApi {
    static {
        System.loadLibrary("MMTtool");
    }
    public static native void run_MMTtool(String args);
}
