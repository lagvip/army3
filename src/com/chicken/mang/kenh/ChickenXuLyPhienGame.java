package com.chicken.mang.kenh;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mang.ChickenPhien;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChickenXuLyPhienGame
extends SimpleChannelInboundHandler<ChickenTinNhan> {
    private final ChickenPhien phien;

    public ChickenXuLyPhienGame(ChickenPhien phien) {
        this.phien = phien;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        ChickenQuanLyMayChu.onClientConnected(this.phien);
    }

    protected void channelRead0(ChannelHandlerContext ctx, ChickenTinNhan tin) {
        this.phien.khiNhanTin(tin);
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        this.phien.khiKenhNgat();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ChickenQuanLyMayChu.logConnection("Loi kenh " + this.phien.moTa() + ": " + cause.getMessage());
        this.phien.dongTin();
    }
}

