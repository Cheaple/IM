package com.example.im.bean.chats;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.net.URI;

public class Msg {
    public final static int SPEAKER_TYPE_ME = 0;
    public final static int SPEAKER_TYPE_OTHER = 1;

    public final static int TYPE_MSG = 100;
    public final static int TYPE_PICTURE = 101;
    public final static int TYPE_VIDEO = 102;

    private String id;
    private String speaker;
    private int type;
    private String content;
    //private Bitmap picture;
    private Uri picture;
    private Uri video;

    private boolean isLocal = false;

    public Msg(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public Msg(String id, String speaker, int type, String content) {
        this.id = id;
        this.speaker = speaker;
        this.type = type;
        this.content = content;
    }

    public Msg(String speaker, int type, String content) {
        this.id = id;
        this.speaker = speaker;
        this.type = type;
        this.content = content;
    }

    public Msg(String speaker, int type, Uri video) {
        this.speaker = speaker;
        this.type = type;
        this.video = video;
    }

    public String getId() {
        return id;
    }

    public String getSpeaker() {
        return speaker;
    }

    public int getType() {
        return type;
    }

    public Uri getPicture() {
        return picture;
    }

    public Uri getVideo() {
        return video;
    }

    public String getContent() {
        return content;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }
}
