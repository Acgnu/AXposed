package org.acgnu.third.oplus;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import org.acgnu.xposed.R;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * O+/卓品 门禁二维码生成工具
 */
public class OPlusPassCodeGenerator {
    //参数:E39F7D8C39404E2780B8F0E7BD5286A3, 00000084, A, 6.5.0
    //返回:Y1fOX1NQt4MhX1yhnFJv9mIjvtRVuXmULc/1wkc00WAIJxk42QAcjINAGLoXcAptarqh2U0pClKo8cP4K1tP8Q==
    private String qrCodeKey = "ENhJELnO0u2ShC6i";
    private String userId = "E39F7D8C39404E2780B8F0E7BD5286A3";
    private String appVersion = "7.0.0";
    private ImageView imageView;
    private static OPlusPassCodeGenerator oPlusPassCodeGenerator;

    public synchronized static OPlusPassCodeGenerator instance(){
        if (null == oPlusPassCodeGenerator) {
            oPlusPassCodeGenerator = new OPlusPassCodeGenerator();
        }
        return oPlusPassCodeGenerator;
    }

    private OPlusPassCodeGenerator(){}

    public Bitmap generate(Context context){
        String format = String.format("%08d", new Object[]{Long.valueOf(getPassCodeNumber())});
        if (format.length() > 8) {
            format = format.substring(format.length() - 8);
        }
        String qrContent = encodeKey(userId, format, "A", appVersion);
        float dip = TypedValue.applyDimension(1, (float) 220, context.getResources().getDisplayMetrics());
        return OPlusEncodingHandler.createQRCode(qrContent, (int) dip);
    }

    public void generateAsync(ImageView imageView){
        this.imageView = imageView;
        new DrawPassCodeTask().execute();
    }

    private String encodeKey(String userId, String str2, String str3, String appVersion) {
        String md5_16 = OPlusMD5.getMD5_16(str2);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (i < 4) {
            int i4 = i2 + 4;
            try {
                sb.append(userId.substring(i2, i4));
                int i5 = i3 + 2;
                sb.append(str2.substring(i3, i5));
                i++;
                i3 = i5;
                i2 = i4;
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return null;
            }
        }
        sb.append(userId.substring(i2));
        sb.append(md5_16);
        sb.append(str3);
        sb.append(appVersion);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("encode:");
        sb2.append(sb.toString());
        try {
            return aesEncrypt(sb.toString(), qrCodeKey, qrCodeKey);
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private String aesEncrypt(String str, String str2, String str3) throws Exception {
        if (str2 == null || str2.length() != 16) {
            return null;
        }
        Cipher instance2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
        instance2.init(1, new SecretKeySpec(str2.getBytes(), "AES"), new IvParameterSpec(str3.getBytes()));
        String b64 = new String(Base64.encode(instance2.doFinal(str.getBytes("utf-8")), Base64.DEFAULT));
        return b64.replace("\n", "");
    }

    private class DrawPassCodeTask extends AsyncTask<String, Void, Bitmap> {
        private DrawPassCodeTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... strArr) {
            return generate(imageView.getContext());
        }

        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private long getPassCodeNumber(){
        String fileKey = "oplus_passcode_preference";
        String valueKey = "oplus_passcode";
        SharedPreferences sharedPreferences = imageView.getContext().getSharedPreferences(fileKey, Context.MODE_PRIVATE);
        long aLong = sharedPreferences.getLong(valueKey, 100L);
        sharedPreferences.edit().putLong(valueKey, aLong + 1).apply();
        return aLong;
    }
}
