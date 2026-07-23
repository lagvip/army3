package com.chicken.dulieu;

import com.chicken.dulieu.ChickenAnhBoPhan;

public class ChickenBoPhan {
    public ChickenAnhBoPhan[] pi;
    public int loai;

    public ChickenBoPhan(int loai) {
        this.loai = loai;
        if (loai == 0) {
            this.pi = new ChickenAnhBoPhan[4];
        }
        if (loai == 1) {
            this.pi = new ChickenAnhBoPhan[10];
        }
        if (loai == 2) {
            this.pi = new ChickenAnhBoPhan[10];
        }
        if (loai == 3) {
            this.pi = new ChickenAnhBoPhan[7];
        }
        if (loai == 4) {
            this.pi = new ChickenAnhBoPhan[2];
        }
        if (loai == 5) {
            this.pi = new ChickenAnhBoPhan[1];
        }
    }
}

