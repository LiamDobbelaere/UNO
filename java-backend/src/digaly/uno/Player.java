package digaly.uno;

/**
 * Created by Tom Dobbelaere on 3/07/2016.
 */
public class Player
{
    private String name;
    private Deck hand;

    public Player(String name) {
        this.name = name;
        this.hand = new Deck();
    }
}
