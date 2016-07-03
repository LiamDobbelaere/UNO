package digaly.uno.tests;

import digaly.uno.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Tom Dobbelaere on 3/07/2016.
 */
public class GameTest
{
    private Game game;

    @Before
    public void setUp() throws Exception
    {
        game = new Game();

        game.addPlayer("playerOne");
        game.addPlayer("playerTwo");
    }

    @Test
    public void testKickPlayer()
    {
        game.kickPlayer("playerTwo");

        assertTrue("Player list did not go down to 1 after kicking player", game.getPlayers().size() == 1);
    }

    @Test
    public void testAddPlayerAfterStarting()
    {
        game.start();
        game.addPlayer("playerThree");

        assertTrue("Player list went up while game already started", game.getPlayers().size() == 2);
    }

    @Test
    public void testStartPiles()
    {
        game.start();

        assertTrue("Draw pile size was not 93 as expected", game.getDrawPile().getCards().size() == 93);
        assertTrue("Discard pile size was not 0 as expected", game.getDiscardPile().getCards().size() == 1);
    }

    @Test
    public void testStartHands()
    {
        game.start();

        assertTrue("Player one's hand size was not 7 as expected", game.getPlayers().get(0).getHand().getCards().size() == 7);
        assertTrue("Player two's hand size was not 7 as expected", game.getPlayers().get(1).getHand().getCards().size() == 7);
    }

    @Test
    public void testPlayCard()
    {
        game.start();
        game.getDiscardPile().addCardToTop(new Card(CardColor.YELLOW, CardValue.ZERO));

        Player currentPlayer = game.getCurrentPlayer();
        Card playedCard = new Card(CardColor.BLUE, CardValue.ZERO);

        currentPlayer.getHand().getCards().clear();
        currentPlayer.getHand().addCardToTop(playedCard);

        game.playCard(currentPlayer.getHand().getCards().get(0));

        assertFalse("Current player did not change automatically after playing", game.getCurrentPlayer().getName().equals(currentPlayer.getName()));
        assertTrue("The played card did not end up on the discardpile", game.getDiscardPile().peekTopCard() == playedCard);
    }

    @Test
    public void testPlayJokerAddFour()
    {
        game.start();

        Player currentPlayer = game.getCurrentPlayer();
        Card playedCard = new Card(CardColor.JOKER, CardValue.ADDFOUR);

        currentPlayer.getHand().getCards().clear();
        currentPlayer.getHand().addCardToTop(playedCard);

        game.playCard(currentPlayer.getHand().getCards().get(0));

        assertFalse("Current player did not change automatically after playing", game.getCurrentPlayer().getName().equals(currentPlayer.getName()));
        assertTrue("The played card did not end up on the discardpile", game.getDiscardPile().peekTopCard() == playedCard);
        assertTrue("Next player did not get four extra cards", game.getCurrentPlayer().getHand().getCards().size() == 11);
    }

    @Test
    public void testPlayCardAddTwo()
    {
        game.start();
        game.getDiscardPile().addCardToTop(new Card(CardColor.YELLOW, CardValue.ZERO));

        Player currentPlayer = game.getCurrentPlayer();
        Card playedCard = new Card(CardColor.YELLOW, CardValue.ADDTWO);

        currentPlayer.getHand().getCards().clear();
        currentPlayer.getHand().addCardToTop(playedCard);

        game.playCard(currentPlayer.getHand().getCards().get(0));

        assertFalse("Current player did not change automatically after playing", game.getCurrentPlayer().getName().equals(currentPlayer.getName()));
        assertTrue("The played card did not end up on the discardpile", game.getDiscardPile().peekTopCard() == playedCard);
        assertTrue("Next player did not get two extra cards", game.getCurrentPlayer().getHand().getCards().size() == 9);
    }

    @Test
    public void testPlayCardSkip()
    {
        game.start();
        game.getDiscardPile().addCardToTop(new Card(CardColor.YELLOW, CardValue.ZERO));

        Player currentPlayer = game.getCurrentPlayer();
        Card playedCard = new Card(CardColor.YELLOW, CardValue.SKIP);

        currentPlayer.getHand().getCards().clear();
        currentPlayer.getHand().addCardToTop(playedCard);

        game.playCard(currentPlayer.getHand().getCards().get(0));

        assertTrue("Current player is not the same player after skip", game.getCurrentPlayer().getName().equals(currentPlayer.getName()));
        assertTrue("The played card did not end up on the discardpile", game.getDiscardPile().peekTopCard() == playedCard);
    }

    @Test
    public void testPlayCardReverse()
    {
        game.start();
        game.getDiscardPile().addCardToTop(new Card(CardColor.YELLOW, CardValue.ZERO));

        Player currentPlayer = game.getCurrentPlayer();
        Card playedCard = new Card(CardColor.YELLOW, CardValue.REVERSE);

        currentPlayer.getHand().getCards().clear();
        currentPlayer.getHand().addCardToTop(playedCard);

        game.playCard(currentPlayer.getHand().getCards().get(0));

        assertTrue("Current player is not the same player after reverse", game.getCurrentPlayer().getName().equals(currentPlayer.getName()));
        assertTrue("The played card did not end up on the discardpile", game.getDiscardPile().peekTopCard() == playedCard);
    }

    @Test
    public void testPlayCardInvalid()
    {
        game.start();
        game.getDiscardPile().addCardToTop(new Card(CardColor.YELLOW, CardValue.ZERO));

        Player currentPlayer = game.getCurrentPlayer();
        Card playedCard = new Card(CardColor.BLUE, CardValue.ONE);

        currentPlayer.getHand().getCards().clear();
        currentPlayer.getHand().addCardToTop(playedCard);

        game.playCard(currentPlayer.getHand().getCards().get(0));

        assertTrue("Current player is not the same player after playing invalid card", game.getCurrentPlayer().getName().equals(currentPlayer.getName()));
        assertFalse("The invalid card ended up on the discardpile", game.getDiscardPile().peekTopCard() == playedCard);
    }

    @Test
    public void testDrawIfCannotPlay()
    {
        game.start();
        game.getDiscardPile().addCardToTop(new Card(CardColor.YELLOW, CardValue.ZERO));

        Player nextPlayer = game.getNextPlayer();
        Card playedCard = new Card(CardColor.BLUE, CardValue.ONE);

        nextPlayer.getHand().getCards().clear();
        nextPlayer.getHand().addCardToTop(playedCard);

        game.advancePlayer();

        assertTrue("Current player does not have two cards as expected", game.getCurrentPlayer().getHand().getCards().size() == 2);
    }
}