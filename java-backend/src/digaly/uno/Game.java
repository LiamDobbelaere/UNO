package digaly.uno;

import digaly.uno.exceptions.PlayerExistsException;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tom Dobbelaere on 3/07/2016.
 */
public class Game
{
    private ArrayList<Player> players;
    private int currentPlayer;
    private Deck drawPile;
    private Deck discardPile;
    private boolean started;
    private boolean reversed;

    public Game()
    {
        this.players = new ArrayList<>();
        this.currentPlayer = 0;

        DeckBuilder deckBuilder = new DeckBuilder();

        this.drawPile = deckBuilder.createStartingDeck();
        this.drawPile.shuffle();
        this.discardPile = new Deck();

        this.started = false;
        this.reversed = false;
    }

    public void addPlayer(String name)
    {
        if (!started)
        {
            this.players.add(new Player(name));
        }
    }

    public void kickPlayer(String name)
    {
        Player playerToRemove = null;

        for (Player player : this.players)
        {
            if (player.getName().equals(name))
            {
                playerToRemove = player;
            }
        }

        if (playerToRemove != null)
        {
            players.remove(playerToRemove);
        }
    }

    public ArrayList<Player> getPlayers()
    {
        return players;
    }

    public void advancePlayer()
    {
        currentPlayer = getNextPlayerID();

        if (!canPlayCards())
        {
            getCurrentPlayer().getHand().addCardToTop(drawPile.drawTopCard());
        }
    }

    private int getNextPlayerID()
    {
        int nextPlayer = 0;

        if (reversed)
        {
            if (currentPlayer - 1 < 0)
            {
                nextPlayer = players.size() - 1;
            }
            else
            {
                nextPlayer = currentPlayer - 1;
            }
        }
        else
        {
            if (currentPlayer + 1 == players.size())
            {
                nextPlayer = 0;
            }
            else
            {
                nextPlayer = currentPlayer + 1;
            }
        }

        return nextPlayer;
    }

    public Player getCurrentPlayer()
    {
        return players.get(currentPlayer);
    }

    public Player getNextPlayer()
    {
        return players.get(getNextPlayerID());
    }

    public Deck getDrawPile()
    {
        return drawPile;
    }

    public Deck getDiscardPile()
    {
        return discardPile;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void start()
    {
        DeckBuilder builder = new DeckBuilder();
        this.discardPile = builder.createDiscardpileFromDeck(this.drawPile);

        Random random = new Random();
        this.currentPlayer = random.nextInt(this.players.size());

        for (int i = 0; i < players.size(); i++)
        {
            Player thisPlayer = players.get(i);

            for (int j = 0; j < 7; j++)
            {
                thisPlayer.getHand().addCardToTop(this.drawPile.drawTopCard());
            }
        }

        started = true;
    }

    public boolean canPlayCards() {
        boolean canPlay = false;

        Card topDiscardCard = discardPile.peekTopCard();

        for (Card c : getCurrentPlayer().getHand().getCards())
        {
            if (topDiscardCard.getCardColor() == c.getCardColor()
                    || topDiscardCard.getCardValue() == c.getCardValue())
            {
                canPlay = true;
            }
        }

        return canPlay;
    }

    private void nextPlayerDrawCards(int amount)
    {
        for (int i = 0; i < amount; i++)
        {
            players.get(getNextPlayerID()).getHand().addCardToTop(drawPile.drawTopCard());
        }
    }

    public void playCard(Card card)
    {
        boolean playedSkip = false;
        boolean playedReverse = false;

        Card topDiscardCard = discardPile.peekTopCard();

        if (topDiscardCard.getCardColor() == card.getCardColor()
                || topDiscardCard.getCardValue() == card.getCardValue()
                || card.getCardColor() == CardColor.JOKER)
        {
            getCurrentPlayer().getHand().removeCard(card);
            discardPile.addCardToTop(card);

            if (card.getCardValue().isSpecialCard())
            {
                switch (card.getCardValue())
                {
                    case ADDFOUR:
                        nextPlayerDrawCards(4);
                        break;
                    case ADDTWO:
                        nextPlayerDrawCards(2);
                        break;
                    case WILD:
                        break;
                    case REVERSE:
                        reversed = !reversed;
                        playedReverse = true;

                        if (players.size() > 2)
                        {
                            advancePlayer();
                        }
                        break;
                    case SKIP:
                        playedSkip = true;
                        advancePlayer();
                        advancePlayer();
                        break;
                }
            }

            if (!canPlayCards() && !playedSkip && !playedReverse) advancePlayer();
        }
    }
}
