package com.chicken.mang.kenh;

import com.chicken.mang.ChickenTinNhan;
import com.chicken.mang.ChickenPhien;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class ChickenGiaiMaTinGame
extends ByteToMessageDecoder {
    private final ChickenPhien phien;

    public ChickenGiaiMaTinGame(ChickenPhien phien) {
        this.phien = phien;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (true) {
            int readerIndex = in.readerIndex();
            ChickenTinNhan tin = this.phien.thuGiaiMaTin(in);
            if (tin == null) {
                in.readerIndex(readerIndex);
                return;
            }
            out.add(tin);
        }
    }
}

