package Main;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Alex Voytovich on 2/21/2017
 */
public class AccountTable
{
    HashMap<String, Account> acountTable;

    public AccountTable()
    {
        accountTable = new HashMap<>();
    }

    public void put(String name, Account user)
    {
        accountTable.put(name, user);
    }

    public boolean contains(String name)
    {
        return accountTable.contatins(name);
    }

    public void remove(String name)
    {
        accountTable.remove(name);
    }

    public void clear()
    {
        accountTable.clear();
    }

    public Set alphaSet()
    {
        TreeSet<String> set = new TreeSet<>();
        for (String s : accountTable.keySet)
        {
            tree.add(s);
        }
        return set;
    }

    public Set typeSet(String type)
    {
        TreeSet<String> set = new TreeSet<>();
        for (String s : accountTable.keySet)
        {
            if (accountTable.get(s).getType().equals(type));
                set.add(s);
        }
    }
}
