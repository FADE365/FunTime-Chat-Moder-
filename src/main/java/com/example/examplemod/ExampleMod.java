package com.example.examplemod;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.text.ChatType.CHAT;

@Mod("examplemod")
public class ExampleMod
{
    public String webhookUrl = "https://discord.com/api/webhooks/1203458825676652554/6jhlKV6wFmS0mlS9E39qmeIMuTZv5B8uXoZFDy12nd2JoW8-OVLBu_Sz_Zy_NyvcqXiL";
    public List<List<String>> ListReactions = new ArrayList();

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);

        ListReactions.add(Arrays.asList( //Слова для мута на 60 сек
                        "test1", "test2"
                )
        );
        ListReactions.add(Arrays.asList( //Слова для мута на 15сек
                        "test3", "test4"
                )
        );
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.getType() != CHAT) return;
        String message = event.getMessage().getString();

        String Nick = ExtractNick(message);
        Detected(message, Nick, 0, 60);
        Detected(message, Nick, 1, 15);

    }

    public void Detected(String MSG,String NICK, int INDEX, int TIME) {
        for (String react : ListReactions.get(INDEX)) {
            if ((MSG.toLowerCase().contains(react.toLowerCase()) && (NICK != null && !NICK.isEmpty()))) {
                Mute(NICK, TIME, "Mute na " + TIME + "s");
            }
        }
    }

    public void Mute(String Nick, int Time, String Arg) {
        String Command = "/tempmute " + Nick + " " + Time + "m " + Arg + " -s";
        Minecraft.getInstance().player.sendMessage
        (
                new StringTextComponent("Player -> " + Nick + " mute for a reason : " + Arg), null
        );
        Minecraft.getInstance().player.chat(Command);

        String discordMessage = "Player " + Nick + " was muted for " + Time + " seconds. Reason: " + Arg;
        sendDiscordWebhook(discordMessage, webhookUrl);

        Command = "";
    }

    private String ExtractNick(String message) {
        int bracketIndex = message.indexOf(']');
        int indx = 0;
        // Убедитесь, что скобка найдена и это не последний символ сообщения
        if (bracketIndex != -1 && message.length() > bracketIndex + 1) {
            // Проверяем, стоит ли двоеточие перед ']'
            if (message.charAt(bracketIndex + 1) == ':') {
                // Если да, то используем '[' как опорный индекс
                int openBracketIndex = message.lastIndexOf('[', bracketIndex);
                int FirstSpace = message.indexOf(' ');
                for (int i = openBracketIndex - 2; message.charAt(i) != ' '; i--){
                    indx = i;
                }
                if (openBracketIndex != -1) {
                    return message.substring(indx, openBracketIndex - 1).trim();
                }
            } else {
                // Если нет, то берем слово после ']'
                String afterBracket = message.substring(bracketIndex + 1).trim();
                String[] words = afterBracket.split(" ");
                if (words.length > 0) {
                    return words[0].trim();
                }
            }
        }
        return null;
    }

    public void sendDiscordWebhook(String message, String webhookUrl) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(webhookUrl);
            httpPost.setHeader("Content-Type", "application/json");

            JsonObject json = new JsonObject();
            json.addProperty("content", message);

            StringEntity entity = new StringEntity(json.toString());
            httpPost.setEntity(entity);

            client.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
