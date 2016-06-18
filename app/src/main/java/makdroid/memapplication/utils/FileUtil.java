package makdroid.memapplication.utils;

import android.content.Context;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Grzecho on 22.11.2015.
 */
public class FileUtil {

    private static final String[] MIME_TYPES = {"image/png", "image/jpeg", "audio/mpeg", "text/plain", "application/pdf"};

    public static File getAppDir(Context context) {
        final String appPath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalAppDir(context).getPath() :
                        context.getFilesDir().getPath();

        return new File(appPath);
    }

    public static File getDiskCacheDir(Context context) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath);
    }

    public static String getStringDiskCacheDir(Context context) {

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                context.getCacheDir().getPath();
    }


    private static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }

    private static File getExternalAppDir(Context context) {
        return context.getExternalFilesDir(null);
    }

    private static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    public static boolean isMimeTypeValid(String url) {
        boolean returnValue = false;
        String type = getMimeType(url);

        if (type != null) {
            for (String m : getListMimeTypes()) {
                if (m.equalsIgnoreCase(type)) {
                    returnValue = true;
                    break;
                }
            }
        }
        return returnValue;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = getExtension(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private static String getExtension(String url) {
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

    private static ArrayList<String> getListMimeTypes() {
        return new ArrayList<>(Arrays.asList(MIME_TYPES));
    }


    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
