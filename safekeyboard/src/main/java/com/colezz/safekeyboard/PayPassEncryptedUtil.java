package com.colezz.safekeyboard;

//todo

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 支付密码内存加密工具类
 */
public class PayPassEncryptedUtil {

    private static final String TAG="PayPassEncryptedUtil";

    public int random;

    public Random randomGen;

    public volatile static PayPassEncryptedUtil instance;

    public static PayPassEncryptedUtil getInstance() {
        if (instance == null) {
            synchronized (PayPassEncryptedUtil.class) {
                if (instance == null) {
                    instance = new PayPassEncryptedUtil();
                }
            }
        }
        return instance;
    }

    private PayPassEncryptedUtil() {
        randomGen=new Random();
        random=randomGen.nextInt(1000)+944;
    }

    /**
     * 给输入的char增加随机偏移量,奇数+,偶数-
     * @param pwd
     * @return
     */
    public int encryptedPayPwd(String pwd){
        if(TextUtils.isEmpty(pwd)||pwd.length()>1){
            throw new RuntimeException("plaintext length must be 1");
        }
        return trans(pwd.charAt(0));
    }

    private int trans(char source){
        if(source%2==0){
            return source-random;
        }
        return source+random;
    }

    /**
     * 将身份证等转化为加密密文,用于做密码规则比较
     * @param source
     * @return
     */
    public int[] transToEncrypted(String source){
        int result[]=new int[source.length()];
        for(int i=0;i<source.length();i++){
            result[i]=trans(source.charAt(i));
        }
        return result;
    }

    public byte[] decryptedPayPwd(int[] pwd){
        byte[] result=new byte[pwd.length];
        for(int i=0;i<pwd.length;i++) {
            if (random % 2 == 0) {
               if(pwd[i]%2==0){
                   result[i]=(byte) (pwd[i]+random);
               }else {
                   result[i]=(byte) (pwd[i]-random);
               }
            }else {
                if(pwd[i]%2==0){
                    result[i]=(byte) (pwd[i]-random);
                }else {
                    result[i]=(byte) (pwd[i]+random);
                }
            }
        }
        return result;
    }

    public byte[] decryptedLoginPwd(ArrayList<Integer> pwd){
        byte[] result=new byte[pwd.size()];
        for(int i=0;i<pwd.size();i++) {
            if (random % 2 == 0) {
                if(pwd.get(i)%2==0){
                    result[i]=(byte) (pwd.get(i)+random);
                }else {
                    result[i]=(byte) (pwd.get(i)-random);
                }
            }else {
                if(pwd.get(i)%2==0){
                    result[i]=(byte) (pwd.get(i)-random);
                }else {
                    result[i]=(byte) (pwd.get(i)+random);
                }
            }
        }
        return result;
    }

    /**
     * 加密上传给后台的支付密码
     */
    public String[] encryptedUploadPayPwd(String payKey,int[] payPwd){

        try {
            byte[] realPayPwd=decryptedPayPwd(payPwd);
            String[] result=SecurityUtils.payEncrypt(payKey,realPayPwd);

            //加密完成后随机填充密码明文
            randomGen.nextBytes(realPayPwd);

            return result;
        }catch (Exception e){
            e.printStackTrace();
            return new String[2];
        }
    }

    public boolean isSimplePassword(int psw[]){
        //判断是否为重复数字
        boolean result=true;
        int first=psw[0];
        for(int i=1;i<psw.length;i++){
            if(first!=psw[i]){
                result=false;
                break;
            }
        }
        return result||isLoopNumber(psw);
    }

    /**
     * 是否为String中的连续数字
     * @param psw
     * @param phone
     * @return
     */
    public boolean isContainString(int[] psw,String phone){
        if(TextUtils.isEmpty(phone)||psw==null){
            return false;
        }
        return contains(transToEncrypted(phone),psw);
    }

    public boolean isContainStringList(int[] psw, List<String> list){
        if(list==null||psw==null){
            return false;
        }
        for(String item:list){
            if(contains(transToEncrypted(item),psw)){
                return true;
            }
        }
        return false;
    }

    private boolean isLoopNumber(int psw[]){
        if(psw==null){
            return false;
        }
        int a[]=transToEncrypted("012345678901234");
        int b[]=transToEncrypted("098765432109876");
        return contains(a,psw)||contains(b,psw);
    }

    public boolean equalsArray(int[] a,int[] b){
        if(a==null||b==null||a.length!=b.length){
            return false;
        }
        return contains(a,b);
    }

    //a是否包含b
    public boolean contains(int[] source,int[] target){
        return indexOf(source,target,0)>-1;
    }

    private int indexOf(int[] source, int[] target, int fromIndex) {
        final int sourceLength = source.length;
        final int targetLength = target.length;
        if (fromIndex >= sourceLength) {
            return (targetLength == 0 ? sourceLength : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetLength == 0) {
            return fromIndex;
        }

        int first = target[0];
        int max = (sourceLength - targetLength);

        for (int i = fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i]!= first) {
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetLength - 1;
                for (int k = 1; j < end && source[j]
                        == target[k]; j++, k++);

                if (j == end) {
                    /* Found whole string. */
                    return i;
                }
            }
        }
        return -1;
    }


}

