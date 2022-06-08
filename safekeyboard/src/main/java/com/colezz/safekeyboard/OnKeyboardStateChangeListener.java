package com.colezz.safekeyboard;

import android.widget.EditText;

/**
 * 键盘状态改变监听
 */
public interface OnKeyboardStateChangeListener {

    /**
     * 当键盘状态发生改变时回调改方法
     *
     * @param keyboardState 键盘状态
     * @param editText      {@link EditText}控件
     * @see KeyboardState
     */
    void onboardStateChange(@KeyboardState int keyboardState, EditText editText);
}
