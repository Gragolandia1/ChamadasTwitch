package com.chamada.live.domain.twitchbot;

import com.chamada.live.controllers.UsuarioController;
import com.chamada.live.domain.service.TwitchBotService;
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

    @Autowired
    private TwitchBotService twitchBotService;

    @Override
    public void run(String[] args) {
        System.out.println("Iniciando o bot...");

        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(oAuth2Credential())
                .build();

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, this::responserUsuarioNoChat);

        twitchClient.getChat().joinChannel("Gragolandia1");
        System.out.println("Bot conectado ao canal da Twitch!");
    }


    private OAuth2Credential oAuth2Credential() {
        return new OAuth2Credential("twitch", twitchApiKey);
    }

    private void responserUsuarioNoChat(ChannelMessageEvent event) {
        String message = event.getMessage().toLowerCase();
        String username = event.getUser().getName();

        if (message.equals("!presente") || message.equals("!presunto")) {
            try {
                Long usuarioId = twitchBotService.buscarUsuarioPorNome(username);
                if (usuarioId == null) {
                    event.getTwitchChat().sendMessage(event.getChannel().getName(),
                            "@" + username + ", você ainda não está registrado. Digite !criar e depois !presente");
                } else {
                    boolean chamadaAtiva = twitchBotService.buscarChamadaAtiva();
                    if (chamadaAtiva) {
                        Long chamadaId = twitchBotService.buscarChamadaAtivaId();
                        if (twitchBotService.usuarioJaEstaPresente(chamadaId, usuarioId)) {
                            event.getTwitchChat().sendMessage(event.getChannel().getName(),
                                    "@" + username + ", você já está presente nesta chamada");
                        } else {
                            twitchBotService.registrarPresenca(username, chamadaId);
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
                Long usuarioId = twitchBotService.buscarUsuarioPorNome(username);
                if (usuarioId != null) {
                    event.getTwitchChat().sendMessage(event.getChannel().getName(),
                            "@" + username + ", você já está registrado.");
                } else {
                    event.getTwitchChat().sendMessage(event.getChannel().getName(),
                            "@" + username + ", você foi registrado com sucesso.");
                    usuarioId = twitchBotService.criarNovoUsuario(username);
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
