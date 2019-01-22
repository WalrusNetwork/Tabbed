package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Reflection;
import com.keenant.tabbed.util.Skin;
import com.keenant.tabbed.util.Skins;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

/**
 * A tab item that represents a player.
 */
@ToString
public class PlayerTabItem implements TabItem {
    @Getter private final Player player;
    @Getter private final PlayerProvider<String> textProvider;
    @Getter private final PlayerProvider<Skin> skinProvider;
    @Getter private String text;
    @Getter private int ping;
    @Getter private Skin skin;

    public PlayerTabItem(Player player, PlayerProvider<String> textProvider, PlayerProvider<Skin> skinProvider) {
        this.player = player;
        this.textProvider = textProvider;
        this.skinProvider = skinProvider;
        this.text = textProvider.get(player);
        this.ping = getNewPing();
        this.skin = skinProvider.get(player);
        updateText();
        updatePing();
        updateSkin();
    }

    public PlayerTabItem(Player player, PlayerProvider<String> textProvider) {
        this(player, textProvider, skinProvier(player));
    }

    public PlayerTabItem(Player player) {
        this(player, listNameProvider(player));
    }

    @Override
    public boolean updateText() {
        if (!this.player.isOnline() || !this.player.isValid())
            return false;

        String newText = this.textProvider.get(this.player);
        boolean update = this.text == null || !newText.equals(this.text);
        this.text = newText;
        return update;
    }

    @Override
    public boolean updatePing() {
        if (!this.player.isOnline() || !this.player.isValid())
            return false;

        int newPing = getNewPing();
        boolean update = newPing != ping;
        this.ping = newPing;
        return update;
    }

    @Override
    public boolean updateSkin() {
        if (!this.player.isOnline() || !this.player.isValid())
            return false;

        Skin newSkin = this.skinProvider.get(this.player);
        boolean update = this.skin == null || !newSkin.equals(this.skin);
        this.skin = newSkin;
        return update;
    }

    private int getNewPing() {
        try {
            Object craftPlayer = Reflection.getHandle(this.player);
            return craftPlayer.getClass().getDeclaredField("ping").getInt(craftPlayer);
        } catch (Exception e) {
            throw new RuntimeException("couldn't get player ping", e);
        }
    }

    private static PlayerProvider<String> nameProvider(Player viewer) {
        return player -> player.getName(viewer);
    }

    private static PlayerProvider<String> displayNameProvider(Player viewer) {
        return player -> player.getDisplayName(viewer);
    }

    private static PlayerProvider<String> listNameProvider(Player viewer) {
        return player -> player.getPlayerListName(viewer);
    }

    private static PlayerProvider<Skin> skinProvier(Player viewer) {
        return player -> Skins.getPlayer(player, viewer);
    }

    public interface PlayerProvider<T> {
        T get(Player player);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PlayerTabItem))
            return false;
        PlayerTabItem other = (PlayerTabItem) object;
        return this.text.equals(other.getText()) && this.skin.equals(other.getSkin()) && this.ping == other.getPing();
    }
}
