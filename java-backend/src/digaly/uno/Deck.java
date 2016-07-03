package digaly.uno;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Tom Dobbelaere on 2/07/2016.
 */
public class Deck
{
    private ArrayList<Card> cards;

    public Deck()
    {
        this.cards = new ArrayList<>();
    }

    public Card peekTopCard() {
        return cards.get(cards.size() - 1);
    }

    public Card drawTopCard() {
        Card topCard = peekTopCard();
        cards.remove(cards.size() - 1);

        return topCard;
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void addCardToTop(Card card) {
        cards.add(card);
    }

    public void addCardToBottom(Card card) {
        cards.add(0, card);
    }

    public ArrayList<Card> getCards()
    {
        return cards;
    }
}
