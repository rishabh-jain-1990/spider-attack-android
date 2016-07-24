package com.bowstringllp.spiderattack.events;

/**
 * Created by rishabhjain on 2/13/16.
 */
public class ActionEvent extends Event{
    private EventType eventType;

    public ActionEvent(String author, EventType eventType) {
        super(author);
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public enum EventType {
        SHOW_GAME(0), START_GAME(1), SHOW_MAIN(2), SHOW_PHONE_VERIFICATION_1(3),
        SHOW_PHONE_VERIFICATION_2(4), SHOW_PHONE_VERIFICATION_3(5), SHOW_PHONE_VERIFICATION_4(6),
        SHOW_FAV_CATEGORY(7), SHOW_LOCATION(8), VIDEO_RECORDED(9), AD_POST_IN_PROGRESS(10), SHOW_COUNTRY_LIST(11), SHOW_CATEGORY_LIST(12)
        , SHOW_PRIVACY(13), SHOW_TERMS(14);

        private int value;

        EventType(int value) {
            this.value = value;
        }

        public static EventType getFromValue(int value) {
            switch (value) {
                case 0:
                    return SHOW_GAME;
                case 1:
                    return START_GAME;
                case 2:
                    return SHOW_MAIN;
                case 3:
                    return SHOW_PHONE_VERIFICATION_1;
                case 4:
                    return SHOW_PHONE_VERIFICATION_2;
                case 5:
                    return SHOW_PHONE_VERIFICATION_3;
                case 6:
                    return SHOW_PHONE_VERIFICATION_4;
                case 7:
                    return SHOW_FAV_CATEGORY;
                case 8:
                    return SHOW_LOCATION;
                case 9:
                    return VIDEO_RECORDED;
                case 10:
                    return AD_POST_IN_PROGRESS;
                case 11:
                    return SHOW_COUNTRY_LIST;
                case 12:
                    return SHOW_CATEGORY_LIST;
                case 13:
                    return SHOW_PRIVACY;
                case 14:
                    return SHOW_TERMS;
                default:
                    return SHOW_GAME;
            }
        }

        public int getValue() {
            return value;
        }
    }
}
