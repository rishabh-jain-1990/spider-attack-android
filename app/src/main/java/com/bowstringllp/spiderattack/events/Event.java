package com.bowstringllp.spiderattack.events;

/**
 * Created by rishabhjain on 6/17/16.
 */
public abstract class Event {
   private String author;

    public Event(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }
}
