package digaly.uno;

import digaly.uno.exceptions.PlayerExistsException;

import java.util.ArrayList;

/**
 * Created by Tom Dobbelaere on 3/07/2016.
 */
public class PlayerList
{
    private ArrayList<String> players;

    public PlayerList()
    {
        this.players = new ArrayList<>();
    }

    public void addPlayer(String name) throws PlayerExistsException
    {
        if (players.contains(name))
        {
            throw new PlayerExistsException();
        }
        else
        {
            players.add(name);
        }
    }

    public void kickPlayer(String name)
    {
        players.remove(name);
    }

    public String get(int index)
    {
        return players.get(index);
    }

    public int size() {
        return players.size();
    }
}
