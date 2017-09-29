package com.example.xiangyunlin.myasynctask;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private Button mButton;
    private ImageView mImageView;
    private final String IMAGE_PATH = "http://cdn.clickme.net/Gallery/2016/01/14/49e8ef43cf9bc876d37b2b526852bf30.jpg";
//    private final String IMAGE_PATH = "https://ppt.cc/futZzx@.jpg";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button);
        mImageView = (ImageView) findViewById(R.id.image_view);

        // 彈出要給 ProgressDialog
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("提示信息");
        mProgressDialog.setMessage("正在下載中，請稍後......");
        mProgressDialog.setMax(100);

        // 設置setCancelable(false); 表示我們不能取消這個彈出框，等下載完成之後再讓彈出框消失
        mProgressDialog.setCancelable(false);

        // 設置ProgressDialog樣式為水平的樣式
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAsyncTask(mImageView).execute(IMAGE_PATH);
            }
        });
    }

    public class MyAsyncTask extends AsyncTask<String, Integer, Bitmap> {
        ImageView bmImage;

        public MyAsyncTask(ImageView mImageView) {
            this.bmImage = mImageView;
        }

        @Override
        protected void onPreExecute() {
            Log.d("test", "onPreExecute !");
            super.onPreExecute();
            // 讓 ProgressDialog 顯示出來
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Log.d("test", "doInBackground !");
            String timeLogStart = Long.toString(System.currentTimeMillis());
            Log.d("test", timeLogStart);
            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            Response response = null;
            Bitmap mIcon11 = null;
            InputStream inputStream = null;
            byte[] image = new byte[]{};
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response.isSuccessful()) {
                try {
                    //mIcon11 = BitmapFactory.decodeStream(response.body().byteStream());
                    Log.d("test", "成功抓取圖片 !");
                    // 得到文件的總長度
                    long file_length = response.body().contentLength();
                    Log.d("test1111111111111", String.valueOf(file_length));
                    // 每次讀取後累加的長度
                    long total_length = 0;
                    int length = 0;
                    // 每次讀取1024個字節
                    byte[] data = new byte[1024];
                    inputStream = response.body().byteStream();
                    //Log.d("test2222222222222", String.valueOf(inputStream.read(data)));
                    while (-1 != (length = inputStream.read(data))) {
                        Log.d("test", "33333333333333333333333");
                        // 每讀一次，就將total_length累加起來
                        Log.d("test", "length = " + length);
                        total_length += length;
                        Log.d("test", "total_length = " + total_length);
                        // 邊讀邊寫到 ByteArrayOutputStream當中
                        byteArrayOutputStream.write(data, 0, length);
                        // 得到當前圖片下載的進度
                        int progress = (int)((total_length/(float)file_length) * 100);

                        Log.d("test", "progress = " + progress);
                        // 時刻將當前進度更新給onProgressUpdate方法
                        publishProgress(progress);
                    }
                    image = byteArrayOutputStream.toByteArray();
                    inputStream.close();
                    byteArrayOutputStream.close();
                    mIcon11 = BitmapFactory.decodeByteArray(image, 0, image.length);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

            }
            String timeLogEnd = Long.toString(System.currentTimeMillis());
            Log.d("test", timeLogEnd);
            Log.d("test", String.valueOf(Long.valueOf(timeLogEnd)-Long.valueOf(timeLogStart)));
            return mIcon11;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("test", "onProgressUpdate !");
            super.onProgressUpdate(values);
            // 更新 ProgressDialog 的進度條
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.d("test", "onPostExecute");
            super.onPostExecute(result);
            // 更新 ImageView
            bmImage.setImageBitmap(result);
            // 讓 ProgressDialog 框消失
            mProgressDialog.dismiss();
        }

    }

}
