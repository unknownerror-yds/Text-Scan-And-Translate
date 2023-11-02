package com.example.textscanandtranslate.feature.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextPojo {
    private long id;
    private String text;

    private String timestamp;
    private Date timestampDate;


    public TextPojo(long id, String text, String timestamp){
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        try {
            this.timestampDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getTimestampDate() {
        return timestampDate;
    }
}
