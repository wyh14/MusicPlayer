package com.wyhand.musicplayer;

public class Music {

    private long _id;
    private String uri;// 路径
    private String title;

    public Music(long _id, String uri, String title) {
        this._id = _id;
        this.uri = uri;
        this.title = title;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Music{" +
                "_id=" + _id +
                ", uri='" + uri + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}