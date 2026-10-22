package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
import com.examplebett.item.SmartDispatchAppItem.SlotPrize;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

import java.util.ArrayList;
import java.util.List;

public record SlotSpinResultPayload(List<SlotPrize> prizes)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    private static void writePrizes(FriendlyByteBuf buf, List<SlotPrize> prizes) {
        buf.writeVarInt(prizes.size());
        for (SlotPrize prize : prizes) {
            buf.writeVarInt(prize.kind());
            buf.writeVarInt(prize.amount());
            buf.writeUtf(prize.modelId());
        }
    }

    private static SlotSpinResultPayload readPrizes(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<SlotPrize> prizes = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            prizes.add(new SlotPrize(buf.readVarInt(), buf.readVarInt(), buf.readUtf()));
        }
        return new SlotSpinResultPayload(prizes);
    }

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SlotSpinResultPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("slot_spin_result"));

    public static final StreamCodec<FriendlyByteBuf, SlotSpinResultPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> writePrizes(buf, value.prizes()),
            SlotSpinResultPayload::readPrizes
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("slot_spin_result");

    public void write(FriendlyByteBuf buf) {
        writePrizes(buf, prizes);
    }

    public static SlotSpinResultPayload read(FriendlyByteBuf buf) {
        return readPrizes(buf);
    }
    *///?}
}
