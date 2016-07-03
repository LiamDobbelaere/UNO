package digaly.uno.tests;

import digaly.uno.Card;
import digaly.uno.CardColor;
import digaly.uno.CardValue;
import digaly.uno.Deck;
import junit.framework.TestCase;
import org.junit.Before;

import static org.junit.Assert.*;

/**
 * Created by Tom Dobbelaere on 2/07/2016.
 */
public class DeckTest extends TestCase
{
    private Deck testDeck;

    @Before
    public void setUp()
    {
        testDeck = new Deck();

        testDeck.addCardToTop(new Card(CardColor.BLUE, CardValue.ZERO));
        testDeck.addCardToTop(new Card(CardColor.BLUE, CardValue.ONE));
        testDeck.addCardToTop(new Card(CardColor.BLUE, CardValue.TWO));
        testDeck.addCardToTop(new Card(CardColor.BLUE, CardValue.THREE));
    }

    public void testDrawTopCard()
    {
        Card card = testDeck.drawTopCard();

        assertTrue("Didn't draw expected blue card with value three",
                card.getCardColor() == CardColor.BLUE
                        && card.getCardValue() == CardValue.THREE);

        assertTrue("Didn't peek expected blue card with value two",
                testDeck.peekTopCard().getCardColor() == CardColor.BLUE
                        && testDeck.peekTopCard().getCardValue() == CardValue.TWO);
    }

    public void testAddCardToTop()
    {
        Card newCard = new Card(CardColor.RED, CardValue.SEVEN);
        testDeck.addCardToTop(newCard);

        assertTrue("Didn't peek expected red card with value seven",
                testDeck.peekTopCard().getCardColor() == CardColor.RED
                        && testDeck.peekTopCard().getCardValue() == CardValue.SEVEN);
    }

    public void testAddCardToBottom()
    {
        Card newCard = new Card(CardColor.RED, CardValue.SEVEN);
        testDeck.addCardToBottom(newCard);

        assertTrue("Didn't peek expected red card with value seven",
                testDeck.getCards().get(0).getCardColor() == CardColor.RED
                        && testDeck.getCards().get(0).getCardValue() == CardValue.SEVEN);
    }
}