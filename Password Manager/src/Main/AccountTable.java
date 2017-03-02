package Main;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Alex Voytovich on 2/21/2017
 */


public class AccountTable
{

    HashMap<String, Account> accountTable;

    public AccountTable(){
        accountTable = new HashMap<>();
    }

    public void put(String name, Account user){
        accountTable.put(name, user);
    }
    
    public Account get(String name){
    	return accountTable.get(name);

    }

    public boolean contains(String name){
        return accountTable.containsKey(name);
    }

    public int size(){
    	return accountTable.size();
    }
    
    public void remove(String name){
        accountTable.remove(name);
    }

    public void clear(){
        accountTable.clear();
    }

    public Set<String> alphaSet(){
        TreeSet<String> set = new TreeSet<>();
        for (String s : accountTable.keySet()){
            set.add(s);
        }
        return set;
    }

    public Set<String> typeSet(String type){
        TreeSet<String> set = new TreeSet<>();
        for (String s : accountTable.keySet()){
            if (accountTable.get(s).getType().equals(type));
                set.add(s);
        }
        return set;
    }
}
