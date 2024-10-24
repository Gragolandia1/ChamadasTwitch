package com.chamada.live.domain.service;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TwitchBotService {

    public Long buscarUsuarioPorNome(String username) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/usuarios/buscar?nome=" + username)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return Long.parseLong(response.body().string());
            } else {
                return null;
            }
        }
    }

    public Long criarNovoUsuario(String username) throws IOException {
        String json = "{\"nome\": \"" + username + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://localhost:8080/usuarios")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return Long.parseLong(response.body().string());
            } else {
                throw new IOException("Erro ao criar novo usuário: " + response);
            }
        }
    }

    public boolean buscarChamadaAtiva() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/chamadas/ativa")
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful() && response.body() != null && Boolean.parseBoolean(response.body().string());
        }
    }

    public Long buscarChamadaAtivaId() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/chamadas/id-ativa")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return Long.parseLong(response.body().string());
            } else if (response.code() == 404) {
                System.out.println("Nenhuma chamada ativa no momento.");
                return null;
            } else {
                throw new IOException("Erro ao buscar ID da chamada ativa: " + response);
            }
        }
    }

    public boolean usuarioJaEstaPresente(Long chamadaId, Long usuarioId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/chamadas/" + chamadaId + "/usuarios/" + usuarioId)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public void registrarPresenca(String username, Long chamadaId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Long usuarioId = buscarUsuarioPorNome(username);
        String json = "{\"usuarioId\": \"" + usuarioId + "\", \"chamadaId\": \"" + chamadaId + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://localhost:8080/presencas")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao registrar presença: " + response);
            }
        }
    }
}
