package com.chamada.live.domain.twitchbot;

import com.chamada.live.controllers.UsuarioController;
import com.chamada.live.domain.usuario.UsuarioRepository;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TwitchBot  implements CommandLineRunner {

    @Value("${twitch.api.key}")
    private String twitchApiKey;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioController usuarioController;

    @Override
    public void run(String[] args) {
        System.out.println("Iniciando o bot...");

        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(oAuth2Credential())
                .build();

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, TwitchBot::responserUsuarioNoChat);

        twitchClient.getChat().joinChannel("Gragolandia1");
        System.out.println("Bot conectado ao canal da Twitch!");
    }


    private OAuth2Credential oAuth2Credential() {
        return new OAuth2Credential("twitch", twitchApiKey);
    }


    private static void registrarPresenca(String username, Long chamadaId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Long usuarioId = buscarUsuarioPorNome(username);
        if (usuarioId == null) {
            usuarioId = criarNovoUsuario(username);
        }

        if (usuarioJaEstaPresente(chamadaId, usuarioId)) {
            System.out.println("Usuário já está presente nesta chamada.");
            return;
        }
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

    private static Long buscarUsuarioPorNome(String username) throws IOException {
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

    private static Long criarNovoUsuario(String username) throws IOException {

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

    private static boolean buscarChamadaAtiva() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/chamadas/ativa")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return Boolean.parseBoolean(response.body().string());
            } else {
                return false;
            }
        }
    }

    private static Long buscarChamadaAtivaId() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/chamadas/id-ativa")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return Long.parseLong(responseBody);
            } else if (response.code() == 404) {
                System.out.println("Nenhuma chamada ativa no momento.");
                return null;
            } else {
                throw new IOException("Erro ao buscar ID da chamada ativa: " + response);
            }
        }
    }

    private static boolean usuarioJaEstaPresente(Long chamadaId, Long usuarioId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:8080/chamadas/" + chamadaId + "/usuarios/" + usuarioId)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    private static void responserUsuarioNoChat(ChannelMessageEvent event) {
        String message = event.getMessage().toLowerCase();
        String username = event.getUser().getName();

        if (message.equals("!presente") || message.equals("!presunto")) {
            try {
                Long usuarioId = buscarUsuarioPorNome(username);
                if (usuarioId == null) {
                    event.getTwitchChat().sendMessage(event.getChannel().getName(),
                            "@" + username + ", você ainda não está registrado. Digite !criar para se registrar.");
                } else {
                    boolean chamadaAtiva = buscarChamadaAtiva();
                    if (chamadaAtiva) {
                        Long chamadaId = buscarChamadaAtivaId();
                        if (usuarioJaEstaPresente(chamadaId, usuarioId)) {
                            event.getTwitchChat().sendMessage(event.getChannel().getName(),
                                    "@" + username + ", você já está presente nesta chamada.");
                        } else {
                            registrarPresenca(username, chamadaId);
                            event.getTwitchChat().sendMessage(event.getChannel().getName(),
                                    "@" + username + ", sua presença foi registrada!");
                        }
                    } else {
                        event.getTwitchChat().sendMessage(event.getChannel().getName(),
                                "@" + username + ", não há uma chamada ativa no momento.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                event.getTwitchChat().sendMessage(event.getChannel().getName(),
                        "@" + username + ", houve um erro ao registrar sua presença.");
            }
        } else if (message.equals("!criar")) {
            try {
                Long usuarioId = buscarUsuarioPorNome(username);
                if (usuarioId != null) {
                    event.getTwitchChat().sendMessage(event.getChannel().getName(),
                            "@" + username + ", você já está registrado.");
                } else {
                    usuarioId = criarNovoUsuario(username);
                    event.getTwitchChat().sendMessage(event.getChannel().getName(),
                            "@" + username + ", você foi registrado com sucesso.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                event.getTwitchChat().sendMessage(event.getChannel().getName(),
                        "@" + username + ", houve um erro ao tentar registrar seu nome.");
            }
        } else if (message.equals("!chamada")) {
            if (username.equals("gragolandia1")) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://localhost:8080/chamadas")
                            .post(RequestBody.create("", MediaType.parse("application/json")))
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            throw new IOException("Erro ao iniciar ou fechar a chamada: " + response);
                        }

                        String resposta = response.body().string();
                        event.getTwitchChat().sendMessage(event.getChannel().getName(), resposta);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    event.getTwitchChat().sendMessage(event.getChannel().getName(),
                            "Houve um erro ao iniciar ou fechar a chamada.");
                }
            } else {
                event.getTwitchChat().sendMessage(event.getChannel().getName(),
                        "@" + username + ", você não tem permissão para abrir uma chamada.");
            }
        }
    }
}