package com.liuh.learn.imitateele;

public class MessageEvent {

    /**
     * 我的设备的添加的位置选择了
     */
    public static final int ACTION_SITE_CHOOSED = 1;

    /**
     * 我的设备的搜索的有关键字输入,然后点击了软键盘上的搜索
     */
    public static final int ACTION_SITE_SEARCH_KEYWORD = 2;


    private int action;
    private Object message;

    public MessageEvent(int action) {
        this.action = action;
    }

    public MessageEvent(Object message) {
        this.message = message;
    }

    public MessageEvent(int action, Object message) {
        this.action = action;
        this.message = message;
    }

    public int getAction() {
        return action;
    }

    public Object getMessage() {
        return message;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
