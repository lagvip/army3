package com.chicken.mang.kenh;

import com.chicken.mang.ChickenTinNhan;
import com.chicken.mang.ChickenPhien;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ChickenMaHoaTinGame
extends MessageToByteEncoder<ChickenTinNhan> {
    private final ChickenPhien phien;

    public ChickenMaHoaTinGame(ChickenPhien phien) {
        this.phien = phien;
    }

    protected void encode(ChannelHandlerContext ctx, ChickenTinNhan msg, ByteBuf out) {
        this.phien.maHoaTin(msg, out);
    }
}

