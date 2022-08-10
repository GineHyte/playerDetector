package net.minebyte.playerdetector;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class playerDetectorMod implements ModInitializer{
	public static final Logger LOGGER = LoggerFactory.getLogger("playerdetector");
	List<String> playersDetected = new ArrayList<String>();
	public boolean isModWork = true;
	public boolean isRealTimeUpdater = false;
	public boolean isAutoLeave = false;
	public boolean isChatSpamming = true;
	private static KeyBinding resetList;
	private static KeyBinding Mod;
	private static KeyBinding realTimeUpdater;
	private static KeyBinding autoLeave;
	private static KeyBinding chatSpamming;
	@Override
	public void onInitialize() {
		resetList = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Reset memory list",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_F7,
				"Player detector"
		));

		Mod = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Turning on/off mod",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_F8,
				"Player detector"
		));

		realTimeUpdater = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Real time player list updater",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_F6,
				"Player detector"
		));

		chatSpamming = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Chat spamming",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_F9,
				"Player detector"
		));

		autoLeave = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Auto leave",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_F10,
				"Player detector"
		));

		ClientTickEvents.START_CLIENT_TICK.register((client) ->
		{
			if (client.player == null) {
				return;
			}
			else {
				if (Mod.wasPressed()) {
					if (isModWork) {
						client.inGameHud.setOverlayMessage(Text.of("Disabled"), true);
						isModWork = false;
					} else {
						client.inGameHud.setOverlayMessage(Text.of("Enabled"), true);
						isModWork = true;
					}
				}
				if (autoLeave.wasPressed()) {
					if (isAutoLeave) {
						client.inGameHud.setOverlayMessage(Text.of("Disabled - auto leave"), true);
						isAutoLeave = false;
					} else {
						client.inGameHud.setOverlayMessage(Text.of("Enabled - auto leave"), true);
						isAutoLeave = true;
					}
				}
				if (realTimeUpdater.wasPressed()){
					if (isRealTimeUpdater) {
						client.inGameHud.setOverlayMessage(Text.of("Disabled - real time"), true);
						isRealTimeUpdater = false;
					} else {
						client.inGameHud.setOverlayMessage(Text.of("Enabled - real time"), true);
						isRealTimeUpdater = true;
					}
				}
				if (chatSpamming.wasPressed()){
					if (isChatSpamming) {
						client.inGameHud.setOverlayMessage(Text.of("Disabled - chat spamming"), true);
						isChatSpamming = false;
					} else {
						client.inGameHud.setOverlayMessage(Text.of("Enabled - chat spamming"), true);
						isChatSpamming = true;
					}
				}
				if (resetList.wasPressed()) {
					playersDetected.clear();
					client.inGameHud.setOverlayMessage(Text.of("player memory was cleared!"), true);
				}
				if (isModWork) {
					for (PlayerEntity player : client.player.getWorld().getPlayers()) {
						if ((!player.getEntityName().equals(client.player.getEntityName())) &
								(!playersDetected.contains(player.getEntityName()))) {
							if (isAutoLeave){client.world.disconnect();}
							client.inGameHud.setOverlayMessage(Text.of("player detect: "+ player.getEntityName() + "(" + (playersDetected.toArray().length + 1) + ")"), false);
							if (isChatSpamming) {
								client.inGameHud.addChatMessage(MessageType.SYSTEM, Text.of(player.getEntityName() + "(" + (playersDetected.toArray().length + 1) + ")"), client.player.getUuid());
							}
							playersDetected.add(player.getEntityName());
						}
					}
					if (isRealTimeUpdater){
						playersDetected.clear();
					}
				}
			}
		});
	}
}
