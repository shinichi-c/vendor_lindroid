package org.lindroid.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;

public class Constants {
    public static final String PERSPECTIVE_SERVICE_NAME = "perspective";
    public static final String SOCKET_PATH = "/data/lindroid/mnt/audio_socket";
    public static final String NOTIFICATION_CHANNEL_ID = "service";
    public static final int NOTIFICATION_ID = 1;

    @SuppressLint({ "PrivateApi", "BlockedPrivateApi" })
    public static SocketAddress createUnixSocketAddressObj(String path) {
        try {
            Class<?> myClass = Class.forName("android.system.UnixSocketAddress");
            Method method = myClass.getDeclaredMethod("createFileSystem", String.class);
            return (SocketAddress) method.invoke(null, path);
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Uri getDownloadUriForCurrentArch() {
	    return switch (Build.SUPPORTED_ABIS[0]) {
		    case "arm64-v8a" ->
				    Uri.parse("https://github.com/Linux-on-droid/lindroid-rootfs/releases/download/nightly/lindroid-rootfs-arm64-plasma.zip.tar.gz");
		    case "x86_64" ->
				    Uri.parse("https://github.com/Linux-on-droid/lindroid-rootfs/releases/download/nightly/lindroid-rootfs-amd64-plasma.zip.tar.gz");
		    case "armeabi-v7a" ->
				    Uri.parse("https://github.com/Linux-on-droid/lindroid-rootfs/releases/download/nightly/lindroid-rootfs-armhf-plasma.zip.tar.gz");
		    default ->
				    throw new UnsupportedOperationException("Lindroid does not seem to support this architecture: " + Build.SUPPORTED_ABIS[0]);
	    };
    }
}
