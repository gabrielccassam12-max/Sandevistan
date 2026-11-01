package com.sandevistan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Configuração salva em config/sandevistan.json (lida pelo SERVIDOR; o cliente recebe o necessário por pacote). */
public class SandevistanConfig {
    /** Duração do Sandevistan em segundos. */
    public double durationSeconds = 10.0;
    /** Recarga após o encerramento, em segundos. */
    public double cooldownSeconds = 20.0;
    /** Multiplicador de velocidade de movimento do jogador (3.0 = 3x). */
    public double speedMultiplier = 3.0;
    /** Multiplicador da força do pulo (1.8 = salto ~3x mais alto). */
    public double jumpMultiplier = 1.8;
    /** Blocos extras de queda sem dano. */
    public double extraSafeFallDistance = 12.0;
    /** Multiplicador da velocidade de ataque (recarga do golpe). */
    public double attackSpeedMultiplier = 3.0;
    /** Entidades afetadas só executam 1 tick a cada N ticks (8 = 12,5% da velocidade normal). */
    public int timeSlowdownFactor = 8;
    /** Raio (em blocos) em volta do jogador onde o tempo é alterado. 0 = dimensão inteira. */
    public int affectRadius = 128;
    /** Chance (0..1) de desviar automaticamente de um projétil que te atingiria. */
    public double autoDodgeChance = 0.6;
    /** Destaca (outline) mobs e projéteis lentos para quem ativou (client). */
    public boolean highlightSlowedEntities = true;
    /** Ids de entidades que NUNCA devem ser desaceleradas, ex.: "minecraft:ender_dragon". */
    public List<String> entityBlacklist = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static SandevistanConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("sandevistan.json");
        SandevistanConfig cfg = new SandevistanConfig();
        if (Files.exists(path)) {
            try (Reader r = Files.newBufferedReader(path)) {
                SandevistanConfig loaded = GSON.fromJson(r, SandevistanConfig.class);
                if (loaded != null) cfg = loaded;
            } catch (Exception e) {
                Sandevistan.LOGGER.error("Falha ao ler sandevistan.json, usando padrões", e);
            }
        }
        cfg.sanitize();
        try {
            Files.writeString(path, GSON.toJson(cfg));
        } catch (Exception e) {
            Sandevistan.LOGGER.error("Falha ao salvar sandevistan.json", e);
        }
        return cfg;
    }

    private void sanitize() {
        durationSeconds = Math.max(0.5, durationSeconds);
        cooldownSeconds = Math.max(0.0, cooldownSeconds);
        speedMultiplier = Math.max(1.0, Math.min(20.0, speedMultiplier));
        jumpMultiplier = Math.max(1.0, Math.min(6.0, jumpMultiplier));
        extraSafeFallDistance = Math.max(0.0, extraSafeFallDistance);
        attackSpeedMultiplier = Math.max(1.0, Math.min(20.0, attackSpeedMultiplier));
        timeSlowdownFactor = Math.max(1, Math.min(100, timeSlowdownFactor));
        affectRadius = Math.max(0, affectRadius);
        autoDodgeChance = Math.max(0.0, Math.min(1.0, autoDodgeChance));
        if (entityBlacklist == null) entityBlacklist = new ArrayList<>();
    }
}
