package com.kirbken;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FirebaseCharacterService {
    private static final String BASE_URL = "https://ecse-comp-2026-default-rtdb.firebaseio.com/characters/";

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static CharacterProfile fetchCharacter(String key) {
        try {
            String url = BASE_URL + key + ".json";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 || response.body().equals("null")) {
                return null;
            }

            FirebaseCharacterDTO dto = gson.fromJson(response.body(), FirebaseCharacterDTO.class);
            if (dto == null || dto.stats == null) return null;

            return new CharacterProfile(
                key,
                dto.name,
                "/images/characters/" + key + ".png",
                dto.stats.health_points,
                dto.stats.attack_power,
                dto.stats.defense_rating,
                dto.stats.speed_velocity,
                dto.ranking_tier,
                dto.combat_mechanics != null ? dto.combat_mechanics.primary_action : null,
                dto.combat_mechanics != null ? dto.combat_mechanics.secondary_action : null,
                dto.combat_mechanics != null ? dto.combat_mechanics.signature_burst : null,
                dto.visual_description
            );

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Matches Firebase's actual JSON structure for a single character entry. */
    private static class FirebaseCharacterDTO {
        String name;
        String ranking_tier;
        String visual_description;
        Stats stats;
        CombatMechanics combat_mechanics;
    }

    private static class Stats {
        int attack_power;
        int defense_rating;
        int health_points;
        int speed_velocity;
    }

    private static class CombatMechanics {
        String primary_action;
        String secondary_action;
        String signature_burst;
    }
}