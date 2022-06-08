package com.colezz.safekeyboard;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;


public class LoginPassEditText extends AppCompatEditText {

    private ArrayList<Integer> password = new ArrayList();

    public LoginPassEditText(Context context) {
        super(context);
    }

    public LoginPassEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginPassEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void insetPass(String str){
        password.add(PayPassEncryptedUtil.getInstance().encryptedPayPwd(str));
    }

    public ArrayList<Integer> getPassword(){
        return password;
    }

    public void deletePass(int start,int end){
        if(start<0){
            return;
        }
        password.remove(start);
    }

    public void clearPass() {
        password.clear();
    }
}
