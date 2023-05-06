package com.wp.bluetooth;


/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 11/22/21
 * 看了我的代码，感动了吗?
 */
public class Sign {

    public static byte vDwordRollRrf(byte pdwDat) {
        boolean blLsb = false;
        if ((pdwDat & 0x01) != 0) {
            blLsb = true;
        }
        pdwDat = (byte) (pdwDat >> 1);
        if (blLsb) {
            pdwDat = (byte) (pdwDat | 0x80);
        } else {
            pdwDat = (byte) (pdwDat & ~0x80);
        }
        return pdwDat;
    }

    public static byte vDwordRollRlf(byte pdwDat) {
        boolean blMsb = false;
        if ((pdwDat & 0x80) != 0) {
            blMsb = true;
        }
        pdwDat <<= 1;

        if (blMsb) {
            pdwDat |= 0x1;
        } else {
            pdwDat &= ~0x1;
        }
        return pdwDat;
    }

    public static byte vDwordRrf(byte pdwDat, int lShiftsNum) {
        for (int j = 0; j < lShiftsNum; j++) {
            pdwDat = vDwordRollRrf(pdwDat);
        }
        return pdwDat;
    }

    public static byte vDwordRlf(byte pdwDat, int lShiftsNum) {

        for (int j = 0; j < lShiftsNum; j++) {
            pdwDat = vDwordRollRlf(pdwDat);
        }
        return pdwDat;
    }


    public static byte[] vBOOTMODE_API_DeCode(byte[] pdwDat,byte key) {
        for (int i = 0; i < pdwDat.length; i++) {
            pdwDat[i] ^= key;
            pdwDat[i] = vDwordRrf(pdwDat[i], 5);
        }
        return pdwDat;
    }

    public static byte[] vBOOTMODE_API_EnCode(byte[] pdwDat,byte key) {
        for (int i = 0; i < pdwDat.length; i++) {
            pdwDat[i] = vDwordRlf(pdwDat[i], 5);
            pdwDat[i] ^= key;
        }
        return pdwDat;
    }

}
