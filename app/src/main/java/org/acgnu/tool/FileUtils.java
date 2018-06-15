package org.acgnu.tool;

import android.os.Environment;

import java.io.*;

public class FileUtils {
    private static final int SD_UNREADABLE = -1;    //SD卡不存在或者不可读写
    private static final int SD_WRITE_SUCCESS = 0;
    public static final String EXPORT_DATA_NAME = "acgnu_settings.txt";

    /**
     * 写入文件到SD
     * @param filename 文件名
     * @param filecontent 内容
     * @throws Exception
     */
    public static int savaFileToSD(String filename, String filecontent) {
        //如果手机已插入sd卡,且app具有读写sd卡的权限
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileOutputStream output = null;
            try {
                filename = Environment.getExternalStorageDirectory().getCanonicalPath() + File.separator + filename;
                output = new FileOutputStream(filename);
                //将String字符串以字节流的形式写入到输出流中
                output.write(filecontent.getBytes());
                return SD_WRITE_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(output);
            }
        }
        return SD_UNREADABLE;
    }

    /**
     读取SD卡中文件的方法
     */
    public static String readFromSD(String filename){
        StringBuilder sb = new StringBuilder("");
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileInputStream input = null;
            try {
                filename = Environment.getExternalStorageDirectory().getCanonicalPath() + File.separator + filename;
                //打开文件输入流
                input = new FileInputStream(filename);
                byte[] temp = new byte[1024];
                int len = 0;
                //读取文件内容:
                while ((len = input.read(temp)) > 0) {
                    sb.append(new String(temp, 0, len));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //关闭输入流
                close(input);
            }
        }
        return sb.toString();
    }

    private static void close(Closeable closeable){
        try {
            if (null != closeable) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
