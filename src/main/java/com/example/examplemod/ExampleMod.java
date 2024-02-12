package com.example.examplemod;

import com.example.examplemod.Discord.WebHook;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.text.ChatType.CHAT;

@Mod("examplemod")
public class ExampleMod
{
    public String webhookUrl =
"https://discord.com/api/webhooks/1203458825676652554/6jhlKV6wFmS0mlS9E39qmeIMuTZv5B8uXoZFDy12nd2JoW8-OVLBu_Sz_Zy_NyvcqXiL";
    public List<List<String>> ListReactions = new ArrayList();

    public List<String> ListReactions2 = new ArrayList();
    public WebHook webHook = new WebHook(webhookUrl);

    public static Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onClientLoggedIn(ClientPlayerNetworkEvent.LoggedInEvent event) {
        webHook.Embed("Модератор активирован игроком " + getUserName());
    }

    public String getUserName() {
        return mc.player.getName().getString();
    }

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);

        ListReactions.add(Arrays.asList( //Слова для мута на 60 сек
                        "test1", "test2"
                )
        );
        ListReactions.add(Arrays.asList( //Слова для мута на 15сек
                        "test3", "test4", "Привет"
                )
        );
        ListReactions.add(Arrays.asList( //Слова для отчистки чата
                        "[chat]:"
                )
        );
        ListReactions.add(Arrays.asList( //93 дня
    "PIXELMINES!.!RU", "spiritworld.su", "timetime.pw", "aquakent.fun", "funtrainer.fun", "noobtime.fun",
    "piona.fun", "timefun.fun", "tpall.fun", "aqua103", "aqua160", "aqua162", "aqua161", "aqua16", "pioner22", "ds",
    "дискорд", "ютуб", "тг", "wild", "impact", "nursultan", "kdaqzdqla1wsasdok", "целка", "нурик", "экспа",
    "mc.space-times.ru", "skytime.su", "play.saturn-x.space", "mc.deathpvp.net", "mc.holyworld.ru", "mc.holyworld.me",
    "play.mixproject.net", "hardcube.net", "broland.net", "spookytime.net", "mineblaze.net", "masedworld.net", "dexland.org",
    "vimemc.net", "hypemc.pro", "musteryworld.net", "mcfunny.net", "go.infinityplay.ru", "mc.hypemc.pro", "mc.speedtime.su",
    "alltp.fun", "funfuntime.fun", "Fokus'fun", "aqua103", "Krysh'fun"
                )
        );

    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.getType() != CHAT) return;
        String message = event.getMessage().getString();

        String Nick = ExtractNick(message);
        //Detected(message, Nick, 0, 60);
        //Detected(message, Nick, 1, 15);
        Detected(message, Nick, 3, 15);
        Detected(message, 2);

    }

    public void Detected(String MSG,String NICK, int INDEX, int TIME) {
        if (NICK == null || NICK.isEmpty()) return;
        String MWN = MSG.replace(NICK, "").toLowerCase();
        for (String react : ListReactions.get(INDEX)) {
            if (MWN.contains(react.toLowerCase())) {
                Mute(NICK, TIME, react); break;
            }
        }
    }

    public void Detected(String MSG, int INDEX) {
        for (String react : ListReactions.get(INDEX)) {
            if ((MSG.toLowerCase().contains(react.toLowerCase()))) {
                ClearChat(); break;
            }
        }
    }

    public void ClearChat() {
        if (mc.player == null) {
            System.out.println("Mine.ist.player == null");
            return;
        }
        mc.player.chat("/clear");
    }

    public void Mute(String Nick, int Time, String Arg) {
        String Command = "/tempmute " + Nick + " " + Time + "m " + Arg + " -s";
        assert mc.player != null;
        mc.player.sendMessage
        (
                new StringTextComponent("Игрок -> " + Nick + " замьючен по причине : " + Arg), null
        );
        //Minecraft.getInstance().player.chat(Command);

        String discordMessage = "Игрок ```" + Nick + "``` Был замучен на " + Time + " секунд.\nПричина: " + Arg;
        webHook.Embed(discordMessage, getPlayerName());

        Command = "";
    }
    public static String getPlayerName() {
        assert mc.player != null;
        return mc.player.getName().getString();
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

}
