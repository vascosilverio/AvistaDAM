package com.projetodam.avista.retrofit.service;
/*
* Classe usada para obter a resposta da API do ImgBB após o envio de uma imagem
 */
public interface EnvioFotografiaCallback {
    void onSucess(String url);
    void onError(String mensagemErro);
}