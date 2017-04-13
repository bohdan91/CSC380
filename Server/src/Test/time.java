package Test;

import java.time.*;

/**
 * Created by darki on 4/13/2017.
 */
public class time {

    public static void main(String[] args){

        Clock c = new Clock() {
            @Override
            public ZoneId getZone() {
                return null;
            }

            @Override
            public Clock withZone(ZoneId zone) {
                return null;
            }

            @Override
            public Instant instant() {
                return null;
            }
        };
    }

}
