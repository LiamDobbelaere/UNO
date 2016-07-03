package digaly.uno;

/**
 * Created by Tom Dobbelaere on 2/07/2016.
 */
public class DeckBuilder
{
    public Deck createStartingDeck() {
        Deck newDeck = new Deck();

        for (CardColor cardColor : CardColor.values())
        {
            if (cardColor != CardColor.JOKER)
            {
                for (CardValue cardValue : CardValue.values())
                {
                    Card newCard;

                    if (cardValue.getNumericValue() < 0)
                    {
                        newCard = new Card(CardColor.JOKER, cardValue);
                    }
                    else
                    {
                        newCard = new Card(cardColor, cardValue);
                    }

                    if (cardValue.getNumericValue() <= 0)
                    {
                        newDeck.addCardToTop(newCard);
                    }
                    else
                    {
                        for (int i = 0; i < 2; i++)
                        {
                            newDeck.addCardToTop(newCard);
                        }
                    }
                }
            }
        }

        return newDeck;
    }

    public Deck createDiscardpileFromDeck(Deck deck)
    {
        Deck discardPile = new Deck();

        //Shuffle deck until top card is not a special one
        while (deck.peekTopCard().getCardValue().getNumericValue() < 0
                || deck.peekTopCard().getCardValue().getNumericValue() > 9)
        {
            deck.shuffle();
        }


        discardPile.addCardToTop(deck.drawTopCard());

        return discardPile;
    }
}
