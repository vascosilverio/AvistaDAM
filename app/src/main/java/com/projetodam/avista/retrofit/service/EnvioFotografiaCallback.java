package com.projetodam.avista.retrofit.service;

public interface EnvioFotografiaCallback {
    void onSucess(String url);
    void onError(String mensagemErro);
}