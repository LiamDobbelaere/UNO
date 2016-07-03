package digaly.uno.tests;

import digaly.uno.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Tom Dobbelaere on 2/07/2016.
 */
public class DeckBuilderTest
{
    private DeckBuilder deckBuilder;
    private Deck sampleDeck;

    @Before
    public void setUp()
    {
        deckBuilder = new DeckBuilder();

        sampleDeck = new Deck();

        sampleDeck.addCardToTop(new Card(CardColor.BLUE, CardValue.ZERO));
        sampleDeck.addCardToTop(new Card(CardColor.BLUE, CardValue.ONE));
        sampleDeck.addCardToTop(new Card(CardColor.BLUE, CardValue.TWO));
    }

    @Test
    public void testCreateStartingDeck()
    {
        Deck newDeck = deckBuilder.createStartingDeck();

        assertTrue("Deck size is incorrect", newDeck.getCards().size() == 108);
    }

    @Test
    public void testCreateDiscardpileFromDeck()
    {
        Deck discardPile = deckBuilder.createDiscardpileFromDeck(sampleDeck);

        assertTrue("Discardpile size is incorrect", discardPile.getCards().size() == 1);
        assertTrue("Discardpile incorrect top card: expected blue two",
                discardPile.peekTopCard().getCardColor() == CardColor.BLUE
                        && discardPile.peekTopCard().getCardValue() == CardValue.TWO);
    }

    @Test
    public void testCreateDiscardpileFromDeckSpecialTopcard()
    {
        sampleDeck.addCardToTop(new Card(CardColor.JOKER, CardValue.ADDFOUR));

        Deck discardPile = deckBuilder.createDiscardpileFromDeck(sampleDeck);

        assertTrue("Discardpile size is incorrect", discardPile.getCards().size() == 1);
        assertTrue("Discardpile incorrect top card: did not expect joker addfour",
                discardPile.peekTopCard().getCardColor() != CardColor.JOKER
                        && discardPile.peekTopCard().getCardValue() != CardValue.ADDFOUR);
    }

}