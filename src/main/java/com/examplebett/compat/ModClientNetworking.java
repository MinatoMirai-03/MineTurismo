//? if >=1.18 {
//?} else {
/*
package com.examplebett.compat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ModClientNetworking {
	private ModClientNetworking() {
	}
*/
//?}
//? if <1.16 {
/*	public static <T> void sendToServer(ResourceLocation id, T payload, BiConsumer<T, FriendlyByteBuf> encoder) {
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

//? if >=1.16 && <1.16.2 {
/*	public static <T> void sendToServer(ResourceLocation id, T payload, BiConsumer<T, FriendlyByteBuf> encoder) {
		FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
		encoder.accept(payload, buf);
		net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE.sendToServer(id, buf);
	}

	public static <T> void registerS2C(ResourceLocation id, Function<FriendlyByteBuf, T> decoder, Consumer<T> handler) {
		net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE.register(id,
				(context, buf) -> {
					T payload = decoder.apply(buf);
					context.getTaskQueue().execute(() -> handler.accept(payload));
				});
	}
*///?}
//? if >=1.16.2 && <1.18 {
/*	public static <T> void sendToServer(ResourceLocation id, T payload, BiConsumer<T, FriendlyByteBuf> encoder) {
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
//? if >=1.18 {
//?} else {
/*
}
*/
//?}
