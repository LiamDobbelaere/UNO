package digaly.uno;

/**
 * Created by Tom Dobbelaere on 3/07/2016.
 */
public class Player
{
    private String name;
    private Deck hand;
    private boolean isOut;

    public Player(String name) {
        this.name = name;
        this.hand = new Deck();
        this.isOut = false;
    }

    public String getName()
    {
        return name;
    }

    public Deck getHand()
    {
        return hand;
    }

    public boolean isOut()
    {
        return isOut;
    }

    public void setOut(boolean value)
    {
        isOut = value;
    }
}
