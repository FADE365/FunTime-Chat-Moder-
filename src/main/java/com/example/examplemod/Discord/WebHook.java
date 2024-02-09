package com.example.examplemod.Discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.awt.*;

public class WebHook {
    private String URL;
    public WebHook(String WebHookURL) {
        this.URL = WebHookURL;
    }

    public void DefaultText(String message) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setHeader("Content-Type", "application/json");

            JsonObject json = new JsonObject();
            json.addProperty("content", message);

            StringEntity entity = new StringEntity(json.toString(), "UTF-8");
            httpPost.setEntity(entity);

            client.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Embed(String message) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

            JsonObject embed = new JsonObject();
            embed.addProperty("title", "Мут игрока");
            embed.addProperty("description", message);
            embed.addProperty("color", 0xFF00FF); // Цвет полосы слева от сообщения в десятичном формате


            // Добавляем встроенное сообщение к основному объекту JSON
            JsonArray embeds = new JsonArray();
            embeds.add(embed);
            JsonObject payload = new JsonObject();
            payload.add("embeds", embeds);

            StringEntity entity = new StringEntity(payload.toString(), "UTF-8");
            httpPost.setEntity(entity);

            client.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
