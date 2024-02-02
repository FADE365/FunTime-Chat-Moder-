package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static net.minecraft.util.text.ChatType.CHAT;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("examplemod")
public class ExampleMod
{

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.getType() != CHAT) {
            return; // Игнорировать, если это не сообщение чата
        }

        String message = event.getMessage().getString();
        if (message.contains("test")) {
            String playerName = extractPlayerName(message);
            if (playerName != null && !playerName.isEmpty()) {
                // Отправляем команду на сервер
                Minecraft.getInstance().player.chat("/tempmute " + playerName + " 15m test -s");
            }
        }
    }

    private String extractPlayerName(String message) {
        int bracketIndex = message.indexOf(']');
        if (bracketIndex != -1 && message.length() > bracketIndex + 2) {
            String afterBracket = message.substring(bracketIndex + 2); // Извлекаем часть сообщения после скобки и пробела
            String[] words = afterBracket.split(" "); // Разделяем по пробелам
            if (words.length > 0) {
                return words[0]; // Первое слово будет именем игрока
            }
        }
        return "";
    }
}
