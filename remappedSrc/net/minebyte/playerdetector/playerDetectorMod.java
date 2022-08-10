package net.minebyte.playerdetector;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.render.entity.CodEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class playerDetectorMod implements ModInitializer{
	public static final Logger LOGGER = LoggerFactory.getLogger("playerdetector");
	List<String> playersDetected = new ArrayList<String>();
	public boolean isModWork = true;
	private static KeyBinding resetList;
	private static KeyBinding Mod;
	@Override
	public void onInitialize() {

//		int w = MinecraftClient.getInstance().getWindow().getFramebufferWidth();
//		int h = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
//		int posX = w / 2;
//		int posY = h / 2;
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


		ClientTickEvents.START_CLIENT_TICK.register((client) ->
		{
			if (client.player == null) {
				return;
			}
			else {
				if (Mod.wasPressed()){
					if (isModWork){
						client.inGameHud.setOverlayMessage(Text.of("Disabled"), true);
						isModWork = false;
					}
					else{
						client.inGameHud.setOverlayMessage(Text.of("Enabled"), true);
						isModWork = true;
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
							client.inGameHud.setOverlayMessage(Text.of("player detect: " + player.getEntityName()), true);
							playersDetected.add(player.getEntityName());
						}
					}
				}
			}
		});
	}
}
