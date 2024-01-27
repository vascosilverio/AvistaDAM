package com.projetodam.avista.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.projetodam.avista.model.ImgBBPOST;
import com.projetodam.avista.retrofit.service.EnvioFotografiaCallback;
import com.projetodam.avista.retrofit.service.ServicoFotografia;

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

/*
* Classe que recebe a imagem, converte para array de bytes, e a prepara para enviar para a API do ImgBB.
* Se a resposta da API for com o código de sucesso, recebe o URL de acesso directo ao JPG da imagem.
* código com recurso ao artigo do StackOverflow: https://stackoverflow.com/questions/59252255/how-to-upload-photo-via-retrofit-from-android-device
 */

public class EnvioFotografia {
    static String urlImgBB = "";
    public static String enviarFoto(Bitmap bitmap, Context context, EnvioFotografiaCallback callback) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // taxa de compressão - 100 com pouca compressão, 0 com compressão máxima
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        // converter bitmap para byte array
        byte[] bitmapData = bos.toByteArray();

        // recebe a data atual com hora, minutos e segundos, para usar sempre um nome diferente para cada ficheiro carregado para a API
        String currentDate = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        File f = new File(context.getCacheDir(), "temp_"+currentDate );
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        // instancia o retrofit
        Retrofit retrofit = RetrofitImgBB.getRetrofitClient(context);
        ServicoFotografia uploadAPIs = retrofit.create(ServicoFotografia.class);

        // declara o tipo de dados que vão ser enviados no Body
        RequestBody requestFile =  RequestBody.create(MediaType.parse("multipart/form-data"), f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), requestFile);

        // envia a imagem para a API do ImgBB
        Call call = uploadAPIs.uploadImage(RetrofitImgBB.KEY_API,body);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    ImgBBPOST imgBBResponse = (ImgBBPOST) response.body();
                    urlImgBB = imgBBResponse.getData().getDisplayUrl();
                    Log.d("EnvioFotografia", "Resposta do servidor: " + urlImgBB);
                    callback.onSucess(urlImgBB);
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onError("Erro: " + t.getMessage());
            }
        });
        return urlImgBB;
    }
}
