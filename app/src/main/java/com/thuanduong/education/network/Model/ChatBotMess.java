package com.thuanduong.education.network.Model;

import com.thuanduong.education.network.Ultil.dataTransfer.JsonSnapshot;

public class ChatBotMess {
    boolean isBot = true;
    String mess;

    public ChatBotMess(JsonSnapshot jsonSnapshot){
        this.isBot = true;
        this.mess = jsonSnapshot.child("answer").toString();
    }

    public ChatBotMess(String mess) {
        this.isBot = false;
        this.mess = mess;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }
}
