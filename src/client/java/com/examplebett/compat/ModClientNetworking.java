package com.examplebett.compat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ModClientNetworking {
	private ModClientNetworking() {
	}

	//? if >=1.20.5 {
	public static void sendToServer(net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
		net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(payload);
	}

	public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void registerS2C(
			net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type,
			Consumer<T> handler) {
		net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(type,
				(payload, context) -> handler.accept(payload));
	}
	//?} else {
	/*public static <T> void sendToServer(ResourceLocation id, T payload, BiConsumer<T, FriendlyByteBuf> encoder) {
		FriendlyByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
		encoder.accept(payload, buf);
		net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(id, buf);
	}

	public static <T> void registerS2C(ResourceLocation id, Function<FriendlyByteBuf, T> decoder, Consumer<T> handler) {
		net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(id,
				(client, netHandler, buf, sender) -> {
					T payload = decoder.apply(buf);
					client.execute(() -> handler.accept(payload));
				});
	}
	*///?}
}
