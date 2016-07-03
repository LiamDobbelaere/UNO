package digaly.uno;

/**
 * Created by Tom Dobbelaere on 2/07/2016.
 */
public class Card
{
    private CardColor cardColor;
    private CardValue cardValue;

    public Card(CardColor cardColor, CardValue cardValue)
    {
        this.cardColor = cardColor;
        this.cardValue = cardValue;
    }

    public CardColor getCardColor()
    {
        return cardColor;
    }

    public CardValue getCardValue()
    {
        return cardValue;
    }
}
