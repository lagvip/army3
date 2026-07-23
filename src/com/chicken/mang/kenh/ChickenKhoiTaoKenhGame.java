package com.chicken.mang.kenh;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenPhien;
import com.chicken.mang.kenh.ChickenGiaiMaTinGame;
import com.chicken.mang.kenh.ChickenMaHoaTinGame;
import com.chicken.mang.kenh.ChickenXuLyPhienGame;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ChickenKhoiTaoKenhGame
extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel ch) {
        int ma = ChickenQuanLyMayChu.nextClientId();
        ChickenPhien phien = new ChickenPhien(ch, ma);
        ch.pipeline().addLast("decoder", new ChickenGiaiMaTinGame(phien)).addLast("encoder", new ChickenMaHoaTinGame(phien)).addLast("handler", new ChickenXuLyPhienGame(phien));
    }
}

