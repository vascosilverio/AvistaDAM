package com.example.avista.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.avista.retrofit.service.ServicoFotografia;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// código com recurso ao artigo do StackOverflow: https://stackoverflow.com/questions/59252255/how-to-upload-photo-via-retrofit-from-android-device
public class EnvioFotografia {
    public static void testExecute(Bitmap bitmap, Context context) throws IOException {



        // converter bitmap para byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // taxa de compressão - 100 com pouca compressão, 0 com compressão máxima
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapData = bos.toByteArray();

        String currentDate = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        File f = new File(context.getCacheDir(), "temp_"+currentDate );
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        Retrofit retrofit = RetrofitImgBB.getRetrofitClient(context);
        ServicoFotografia uploadAPIs = retrofit.create(ServicoFotografia.class);

        RequestBody requestFile =  RequestBody.create(MediaType.parse("multipart/form-data"), f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), requestFile);
        Call call = uploadAPIs.uploadImage(RetrofitImgBB.KEY_API,body);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    String responseBodyString = response.body().toString();

                    Log.d("EnvioFotografia", "Resposta do servidor: " + responseBodyString);
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context, "Erro: ", Toast.LENGTH_SHORT);
            }
        });
    }
}
