package Test;

import java.time.*;
import java.util.*;


/**
 * Created by darki on 4/13/2017.
 */
public class time {

    public static void main(String[] args){

        String thing = "1|ouisfoiu";
        String id = thing.substring(0, thing.indexOf("|"));
        String enc = thing.substring(thing.indexOf("|") + 1);
        System.out.println(enc + " " + id);
    }

}
