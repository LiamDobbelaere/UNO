package digaly.uno;

/**
 * Created by Tom Dobbelaere on 3/07/2016.
 */
public class Game
{
    private PlayerList players;
    private int currentPlayer;
    private Deck drawPile;
    private Deck discardPile;

    public Game()
    {
        this.players = new PlayerList();
        this.currentPlayer = 0;

        DeckBuilder deckBuilder = new DeckBuilder();

        this.drawPile = deckBuilder.createStartingDeck();
        this.drawPile.shuffle();
        this.discardPile = deckBuilder.createDiscardpileFromDeck(this.drawPile);
    }

    public void advancePlayer()
    {
        currentPlayer += 1;

        if (currentPlayer == players.size())
        {
            currentPlayer = 0;
        }
    }

    public String getCurrentPlayer()
    {
        return players.get(currentPlayer);
    }

    public Deck getDrawPile()
    {
        return drawPile;
    }

    public Deck getDiscardPile()
    {
        return discardPile;
    }
}
