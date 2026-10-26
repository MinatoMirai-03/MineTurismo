package com.examplebett.compat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ModNetworking {
	private ModNetworking() {
	}

	//? if >=1.20.5 && <26.1 {
	public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void registerC2S(
			net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type,
			net.minecraft.network.codec.StreamCodec<? super FriendlyByteBuf, T> codec,
			BiConsumer<T, ServerPlayer> handler) {
		net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S().register(type, codec);
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(type,
				(payload, context) -> handler.accept(payload, context.player()));
	}

	public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void registerS2C(
			net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type,
			net.minecraft.network.codec.StreamCodec<? super FriendlyByteBuf, T> codec) {
		net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(type, codec);
	}

	public static void sendToClient(ServerPlayer player, net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, payload);
	}
	//?}
	//? if >=26.1 {
	/*public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void registerC2S(
			net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type,
			net.minecraft.network.codec.StreamCodec<? super FriendlyByteBuf, T> codec,
			BiConsumer<T, ServerPlayer> handler) {
		net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(type, codec);
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(type,
				(payload, context) -> handler.accept(payload, context.player()));
	}

	public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void registerS2C(
			net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type,
			net.minecraft.network.codec.StreamCodec<? super FriendlyByteBuf, T> codec) {
		net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.clientboundPlay().register(type, codec);
	}

	public static void sendToClient(ServerPlayer player, net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, payload);
	}*/
	//?}
	//? if <1.16 {
	/*public static <T> void registerC2S(ResourceLocation id, Function<FriendlyByteBuf, T> decoder,
			BiConsumer<T, ServerPlayer> handler) {
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(id,
				(server, player, netHandler, buf, sender) -> {
					T payload = decoder.apply(buf);
					server.execute(() -> handler.accept(payload, player));
				});
	}

	public static <T> void registerS2C(ResourceLocation id, Function<FriendlyByteBuf, T> decoder) {
	}

	public static <T> void sendToClient(ServerPlayer player, ResourceLocation id, T payload, BiConsumer<T, FriendlyByteBuf> encoder) {
		FriendlyByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
		encoder.accept(payload, buf);
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, id, buf);
	}
	*///?}

	//? if >=1.16 && <1.16.2 {
	/*public static <T> void registerC2S(ResourceLocation id, Function<FriendlyByteBuf, T> decoder,
			BiConsumer<T, ServerPlayer> handler) {
		net.fabricmc.fabric.api.network.ServerSidePacketRegistry.INSTANCE.register(id,
				(context, buf) -> {
					T payload = decoder.apply(buf);
					ServerPlayer player = (ServerPlayer) context.getPlayer();
					context.getTaskQueue().execute(() -> handler.accept(payload, player));
				});
	}

	public static <T> void registerS2C(ResourceLocation id, Function<FriendlyByteBuf, T> decoder) {
	}

	public static <T> void sendToClient(ServerPlayer player, ResourceLocation id, T payload, BiConsumer<T, FriendlyByteBuf> encoder) {
		FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
		encoder.accept(payload, buf);
		net.fabricmc.fabric.api.network.ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, id, buf);
	}
	*///?}
	//? if >=1.16.2 && <1.20.5 {
	/*public static <T> void registerC2S(ResourceLocation id, Function<FriendlyByteBuf, T> decoder,
			BiConsumer<T, ServerPlayer> handler) {
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(id,
				(server, player, netHandler, buf, sender) -> {
					T payload = decoder.apply(buf);
					server.execute(() -> handler.accept(payload, player));
				});
	}

	public static <T> void registerS2C(ResourceLocation id, Function<FriendlyByteBuf, T> decoder) {
	}

	public static <T> void sendToClient(ServerPlayer player, ResourceLocation id, T payload, BiConsumer<T, FriendlyByteBuf> encoder) {
		FriendlyByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
		encoder.accept(payload, buf);
		net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, id, buf);
	}
	*///?}

	//? if >=1.16 && <1.16.2 {
	/*public static Iterable<ServerPlayer> trackingPlayers(net.minecraft.world.entity.Entity entity) {
		java.util.List<ServerPlayer> list = new java.util.ArrayList<>();
		net.fabricmc.fabric.api.server.PlayerStream.watching(entity).forEach(p -> list.add((ServerPlayer) p));
		return list;
	}

	public static Iterable<ServerPlayer> worldPlayers(net.minecraft.server.level.ServerLevel level) {
		java.util.List<ServerPlayer> list = new java.util.ArrayList<>();
		net.fabricmc.fabric.api.server.PlayerStream.world(level).forEach(p -> list.add((ServerPlayer) p));
		return list;
	}
	*///?}
	//? if <1.16 {
	/*public static Iterable<ServerPlayer> trackingPlayers(net.minecraft.world.entity.Entity entity) {
		return net.fabricmc.fabric.api.networking.v1.PlayerLookup.tracking(entity);
	}

	public static Iterable<ServerPlayer> worldPlayers(net.minecraft.server.level.ServerLevel level) {
		return net.fabricmc.fabric.api.networking.v1.PlayerLookup.world(level);
	}
	*///?}
	//? if >=1.16.2 {
	public static Iterable<ServerPlayer> trackingPlayers(net.minecraft.world.entity.Entity entity) {
		return net.fabricmc.fabric.api.networking.v1.PlayerLookup.tracking(entity);
	}
	//?}

	//? if >=1.16.2 && <26.1 {
	public static Iterable<ServerPlayer> worldPlayers(net.minecraft.server.level.ServerLevel level) {
		return net.fabricmc.fabric.api.networking.v1.PlayerLookup.world(level);
	}
	//?}
	//? if >=26.1 {
	/*public static Iterable<ServerPlayer> worldPlayers(net.minecraft.server.level.ServerLevel level) {
		return net.fabricmc.fabric.api.networking.v1.PlayerLookup.level(level);
	}
	*///?}
}
