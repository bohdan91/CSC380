package Test;

/**
 * Created by darki on 4/18/2017.
 */
public class sqlSytaxCheck {
    public static void main(String[] args){
        String[] info = {"Darkking271", "abcdef", "123456"};
        String sql = "INSERT INTO users values(\"" + info[0] + "\", \"" + info[1] + "\", \"" + info[2] + "\")";
        System.out.println(sql);
    }
}
