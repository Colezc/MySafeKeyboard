package com.colezz.safekeyboard;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * @author Zengchi
 * @date 2022/5/20 13:58
 */
public class KeyboardEditTextTouchListener implements View.OnTouchListener {

    private int mKeyboardType;
    private SafeKeyboard mSafeKeyboard;

    public KeyboardEditTextTouchListener(SafeKeyboard safeKeyboard, @KeyboardType int keyboardType) {
        this.mSafeKeyboard = safeKeyboard;
        this.mKeyboardType = keyboardType;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mSafeKeyboard != null && event.getAction() == MotionEvent.ACTION_UP) {
            mSafeKeyboard.setKeyboardType(mKeyboardType);
            if (!mSafeKeyboard.isShowing()) {
                mSafeKeyboard.show((EditText) v);
            } else {
                mSafeKeyboard.setEditText((EditText) v);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // setInsertionDisabled when user touches the view
            setInsertionDisabled((EditText) v);
        }

        return false;
    }

    /**
     * 禁用选择和粘贴插入文本
     * @param editText
     */
    private void setInsertionDisabled(EditText editText) {
        try {
            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editorObject = editorField.get(editText);

            // if this view supports insertion handles
            Class editorClass = Class.forName("android.widget.Editor");
            Field mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled");
            mInsertionControllerEnabledField.setAccessible(true);
            mInsertionControllerEnabledField.set(editorObject, false);

            // if this view supports selection handles
            Field mSelectionControllerEnabledField = editorClass.getDeclaredField("mSelectionControllerEnabled");
            mSelectionControllerEnabledField.setAccessible(true);
            mSelectionControllerEnabledField.set(editorObject, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
