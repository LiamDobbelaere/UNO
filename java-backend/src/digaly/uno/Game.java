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
    private boolean isOver;
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
        this.isOver = false;
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

    public Player getPlayer(String name)
    {
        Player returnPlayer = null;

        for (Player player : players)
        {
            if (player.getName().equals(name))
            {
                returnPlayer = player;
            }
        }

        return returnPlayer;
    }

    public void advancePlayer()
    {
        currentPlayer = getNextPlayerID();

        /*if (!canPlayCards())
        {
            getCurrentPlayer().getHand().addCardToTop(drawPile.drawTopCard());
            advancePlayer();
        }*/
    }

    private int getNextPlayerID()
    {
        if (isOver()) return 0;

        boolean first = true;
        int nextPlayer = 0;

        while (first || getPlayers().get(nextPlayer).isOut())
        {
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

            first = false;
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

    public boolean isOver() { return isOver; }

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

    public boolean canPlayCards()
    {
        boolean canPlay = false;

        Card topDiscardCard = discardPile.peekTopCard();

        for (Card c : getCurrentPlayer().getHand().getCards())
        {
            if (canCardBePlayed(c, topDiscardCard))
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

    public void drawCard()
    {
        if (isOver()) return;

        Card topCard = drawPile.drawTopCard();

        getCurrentPlayer().getHand().addCardToTop(topCard);

        if (canPlayCards())
        {
            playCard(topCard);
        }
        else
        {
            advancePlayer();
        }
    }

    public boolean canCardBePlayed(Card card, Card discardCard)
    {
        return (discardCard.getCardColor() == card.getCardColor()
                || discardCard.getCardValue() == card.getCardValue()
                || card.getCardColor() == CardColor.JOKER
                || discardCard.getCardColor() == CardColor.JOKER);
    }

    public void checkOver() {
        int playersOut = 0;

        for (Player player : players)
        {
            if (player.isOut()) playersOut += 1;
        }

        if (players.size() - playersOut <= 1)
        {
            isOver = true;
        }
    }

    public void playCard(Card card)
    {
        Player currentPlayer = getCurrentPlayer();
        boolean blockAdvance = false;

        Card topDiscardCard = discardPile.peekTopCard();

        if (canCardBePlayed(card, topDiscardCard) && !isOver())
        {
            currentPlayer.getHand().removeCard(card);
            discardPile.addCardToTop(card);

            if (card.getCardValue().isSpecialCard())
            {
                switch (card.getCardValue())
                {
                    case ADDFOUR:
                        nextPlayerDrawCards(4);

                        if (currentPlayer.getHand().getCards().size() > 0)
                        {
                            blockAdvance = true;
                        }
                        break;
                    case ADDTWO:
                        nextPlayerDrawCards(2);

                        break;
                    case WILD:
                        if (currentPlayer.getHand().getCards().size() > 0)
                        {
                            blockAdvance = true;
                        }
                        break;
                    case REVERSE:
                        reversed = !reversed;

                        if (players.size() == 2)
                        {
                            blockAdvance = true;
                        }
                        break;
                    case SKIP:
                        advancePlayer();
                        break;
                }
            }

            if (currentPlayer.getHand().getCards().size() == 0 && !currentPlayer.isOut())
            {
                currentPlayer.setOut(true);
                advancePlayer();
            }

            checkOver();

            if (!blockAdvance) advancePlayer();
        }
    }
}
