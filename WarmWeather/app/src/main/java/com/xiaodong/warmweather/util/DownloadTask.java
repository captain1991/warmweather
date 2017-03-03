package com.xiaodong.warmweather.util;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yxd on 2017/3/1.
 */
public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAIL = 1;
    public static final int TYPE_PUSE = 2;
    public static final int TYPE_CANCLE = 3;
    private boolean isCancled;
    private boolean isPused;
    RandomAccessFile save_file;
    File file;
    private DownloadListener listener;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if(integer==TYPE_SUCCESS) {
            listener.onSuccess();
        }else if (integer==TYPE_CANCLE){
            listener.onCancle();
        }else if(integer==TYPE_PUSE){
            listener.onPuse();
        }else if (integer==TYPE_FAIL){
            listener.onFailed();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //具体业务
        listener.onProgress(values[0]);
    }

    public void setCancled() {
        this.isCancled = true;
    }

    public void setPused() {
        this.isPused = true;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        long downloadLength = 0;
        String url = params[0];
        String fileName = url.substring(url.lastIndexOf("/"));
        String downDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        LogUtil.d("file=============="+downDir+fileName);
        try {
            file = new File(downDir + fileName);
            if (file.exists()) {
                downloadLength = file.length();
            }

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("RANGE", "bytes=" + downloadLength + "-")
                    .build();

            Response response = okHttpClient.newCall(request).execute();


            if (response != null) {
                is = response.body().byteStream();
                long totalLength = response.body().contentLength();
                save_file = new RandomAccessFile(file, "rw");
                save_file.seek(downloadLength);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    if (isCancled) {
                        return TYPE_CANCLE;
                    }
                    if (isPused) {
                        return TYPE_PUSE;
                    }
                    save_file.write(bytes, 0, len);
                    downloadLength += len;
                    Integer progress = (int) (downloadLength * 100 / totalLength);
                    publishProgress(progress);
                }
                response.body().close();
                return TYPE_SUCCESS;
            } else {
                return TYPE_FAIL;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (save_file != null) {
                    save_file.close();
                }
                if (isCancelled() && file != null) {
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return TYPE_FAIL;
    }

    public interface DownloadListener {
        void onProgress(int progress);

        void onSuccess();

        void onPuse();

        void onCancle();

        void onFailed();
    }
}
