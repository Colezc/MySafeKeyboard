package com.colezz.safekeyboard;


import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils {
    private static final String TAG="SecurityUtils";


    private static char[] CS = "0123456789ABCDEF".toCharArray();

    private static Random random=new Random();

    public static String[] payEncrypt(String pkvalue, byte[] pwd) {
        byte[] pinBlock = getPinBlock(pwd);
        byte[] randByte = getRandomBytes(16);
        byte[] tmpByte = new byte[24];

        for (int i = 0; i < tmpByte.length; ++i) {
            if (i < 16) {
                tmpByte[i] = randByte[i];
            } else {
                tmpByte[i] = randByte[i - 16];
            }
        }

        oddCheck(tmpByte);
        String paypsw = bytesToHex(des3Encrypt(pinBlock, tmpByte));
        //随机打乱pinBlock
        random.nextBytes(pinBlock);
        paypsw = paypsw.substring(0, 16);
        byte[] bs = makeRsaData(tmpByte);
        String paykey = encrypt(pkvalue, bs);
        return new String[]{paypsw, paykey};
    }

    public static String encrypt(String pkvalue, byte[] tmpbyte) {
        byte[] bs = null;
        String key = "30819F300D06092A864886F70D010101050003818D00" + pkvalue;
        byte[] ks = hexToBytes(key);
        byte[] rs = rsaEncrypt(tmpbyte, ks);
        return bytesToHex(rs);
    }

    private static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] bs = new byte[len >> 1];
        int r = 0;

        for(int i = 0; i < len; ++i) {
            int n = s.charAt(i);
            if (n >= 48 && n <= 57) {
                n -= 48;
            } else if (n >= 65 && n <= 70) {
                n -= 55;
            } else if (n >= 97 && n <= 102) {
                n -= 87;
            }

            if ((i & 1) == 1) {
                bs[i >> 1] = (byte)(r | n);
            } else {
                r = n << 4;
            }
        }

        return bs;
    }

    public static byte[] rsaEncrypt(byte[] data, byte[] key) {
        Cipher cipher = null;

        try {
            KeySpec keySpec = new X509EncodedKeySpec(key);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey k = factory.generatePublic(keySpec);
            if (data.length >= 128) {
                cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            } else {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            }

            cipher.init(1, k);
            data = cipher.doFinal(data);
            return data;
        } catch (Throwable var6) {
            var6.printStackTrace();
            return null;
        }
    }



    public static byte[] makeRsaData(byte[] ran) {
        byte[] bs = getRandomBytes(128);

        for (int i = 0; i < 128; ++i) {
            if (bs[i] == 0) {
                bs[i] = 77;
            }
        }

        bs[0] = 0;
        bs[1] = 2;
        bs[97] = 0;
        bs[98] = 48;
        bs[99] = 28;
        bs[100] = 4;
        bs[101] = 16;
        System.arraycopy(ran, 0, bs, 102, 16);
        bs[118] = 4;
        bs[119] = 8;
        return bs;
    }

    public static final byte[] des3Encrypt(byte[] data, byte[] key) {
        try {
            SecretKey deskey = new SecretKeySpec(key, "DESede");
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(1, deskey);
            return c1.doFinal(data);
        } catch (Throwable var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String bytesToHex(byte[] bs) {
        try {
            int len = bs.length;
            char[] cs = CS;
            char[] rs = new char[bs.length << 1];

            for (int i = 0; i < len; ++i) {
                int b = bs[i] & 255;
                rs[i + i] = cs[b >> 4];
                rs[i + i + 1] = cs[b & 15];
            }

            return new String(rs) != null ? new String(rs) : "";
        } catch (Error var6) {
            return "";
        }
    }

    public static final void oddCheck(byte[] data) {
        int len = data.length;

        for (int i = 0; i < len; ++i) {
            int t = data[i] & 127;
            int p = Integer.bitCount(t);
            if ((p & 1) == 0) {
                t |= 128;
            }

            data[i] = (byte) t;
        }

    }

    public static final byte[] getRandomBytes(int n) {
        int tmp = n;
        SecureRandom s = new SecureRandom();
        byte[] r = new byte[tmp];

        for (int i = 0; i < tmp; ++i) {
            r[i] = (byte) s.nextInt();
        }

        return r;
    }

    public static byte[] getPinBlock(String s) {
        int len = s.length();
        byte[] bs = new byte[8];

        int ic;
        for (ic = 0; ic < bs.length; ++ic) {
            bs[ic] = -1;
        }

        ic = 0;

        for (int i = 0; i < bs.length && ic < len; ++i) {
            bs[i] = (byte) (bs[i] & (s.charAt(ic++) - 48 << 4 | 15));
            if (ic >= len) {
                break;
            }

            bs[i] = (byte) (bs[i] & (s.charAt(ic++) - 48 | 240));
        }

        return bs;
    }

    public static byte[] getPinBlock(byte[] s) {
        int len =s.length;
        byte[] bs = new byte[8];

        int ic;
        for (ic = 0; ic < bs.length; ++ic) {
            bs[ic] = -1;
        }
        ic = 0;
        for (int i = 0; i < bs.length && ic < len; ++i) {
            bs[i] = (byte) (bs[i] & (s[ic++] - 48 << 4 | 15));
            if (ic >= len) {
                break;
            }

            bs[i] = (byte) (bs[i] & (s[ic++] - 48 | 240));
        }
        //打乱密码明文
        random.nextBytes(s);
        return bs;
    }
}

