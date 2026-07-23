package com.chicken.mang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChickenTinNhan {
    private byte lenh;
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    public DataInputStream dis;
    private byte[] taiDuLieu;

    public ChickenTinNhan(int lenh) {
        this((byte)lenh);
    }

    public ChickenTinNhan(byte lenh) {
        this.lenh = lenh;
        this.os = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(this.os);
    }

    public ChickenTinNhan(byte lenh, byte[] duLieu) {
        this.lenh = lenh;
        this.taiDuLieu = duLieu;
        this.is = new ByteArrayInputStream(duLieu);
        this.dis = new DataInputStream(this.is);
    }

    public byte layLenh() {
        return this.lenh;
    }

    public void datLenh(int cmd) {
        this.datLenh((byte)cmd);
    }

    public void datLenh(byte cmd) {
        this.lenh = cmd;
    }

    public byte[] layDuLieu() {
        if (this.taiDuLieu != null) {
            return this.taiDuLieu;
        }
        return this.os.toByteArray();
    }

    public DataInputStream boDoc() {
        return this.dis;
    }

    public DataOutputStream boGhi() {
        return this.dos;
    }

    public void donDep() {
        try {
            if (this.dis != null) {
                this.dis.close();
            }
            if (this.dos != null) {
                this.dos.close();
            }
        }
        catch (IOException iOException) {
        }
    }
}

