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
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import java.util.concurrent.TimeUnit;

public class ChickenKhoiTaoKenhGame
extends ChannelInitializer<SocketChannel> {
    private final SslContext sslContext;

    public ChickenKhoiTaoKenhGame(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    protected void initChannel(SocketChannel ch) {
        int ma = ChickenQuanLyMayChu.nextClientId();
        ChickenPhien phien = new ChickenPhien(ch, ma);
        if (this.sslContext != null) {
            SslHandler ssl = this.sslContext.newHandler(ch.alloc());
            ssl.setHandshakeTimeout(10, TimeUnit.SECONDS);
            ch.pipeline().addLast("tls", ssl);
        }
        ch.pipeline().addLast("decoder", new ChickenGiaiMaTinGame(phien))
                .addLast("encoder", new ChickenMaHoaTinGame(phien))
                .addLast("handler", new ChickenXuLyPhienGame(phien));
    }
}
