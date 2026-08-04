package com.chicken.mang.kenh;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.kenh.ChickenKhoiTaoKenhGame;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.File;

public final class ChickenMayChuNetty {
    private EventLoopGroup nhomChu;
    private EventLoopGroup nhomTho;
    private Channel kenhMayChu;

    public void batDau(
            String mayChu,
            int cong,
            boolean dungTls,
            String duongDanChungChi,
            String duongDanKhoaRieng
    ) throws Exception {
        SslContext sslContext = null;
        if (dungTls) {
            File chungChi = new File(duongDanChungChi);
            File khoaRieng = new File(duongDanKhoaRieng);
            if (!chungChi.isFile() || !khoaRieng.isFile()) {
                throw new IllegalStateException(
                        "TLS da bat nhung thieu certificate/private key");
            }
            sslContext = SslContextBuilder
                    .forServer(chungChi, khoaRieng)
                    .protocols("TLSv1.3", "TLSv1.2")
                    .build();
        }
        this.nhomChu = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        this.nhomTho = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(nhomChu, nhomTho)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChickenKhoiTaoKenhGame(sslContext));
        ChannelFuture bindFuture = bootstrap.bind(mayChu, cong).sync();
        this.kenhMayChu = bindFuture.channel();
        ChickenQuanLyMayChu.log("Netty server listening on " + mayChu + ":" + cong
                + (dungTls ? " TLS" : " PLAINTEXT_LOCAL_ONLY"));
    }

    public void dung() throws InterruptedException {
        if (this.kenhMayChu != null) {
            this.kenhMayChu.close().sync();
            this.kenhMayChu = null;
        }
        if (this.nhomChu != null) {
            this.nhomChu.shutdownGracefully().sync();
            this.nhomChu = null;
        }
        if (this.nhomTho != null) {
            this.nhomTho.shutdownGracefully().sync();
            this.nhomTho = null;
        }
    }
}
