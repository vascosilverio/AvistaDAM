package com.projetodam.avista.retrofit.service;

import com.projetodam.avista.model.ImgBBPOST;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/*
* Interface para o envio de imagens através da API do ImgBB
* código com recurso ao artigo do StackOverflow:
* https://stackoverflow.com/questions/59252255/how-to-upload-photo-via-retrofit-from-android-device
 */
public interface ServicoFotografia {
    @Multipart()
    @POST("/1/upload")
    Call<ImgBBPOST> uploadImage(@Query("key") String key, @Part() MultipartBody.Part file);
}