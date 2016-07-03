package digaly.uno;

/**
 * Created by Tom Dobbelaere on 2/07/2016.
 */
public enum CardValue
{
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    ADDTWO(10),
    REVERSE(11),
    SKIP(12),
    ADDFOUR(-1),
    WILD(-2);

    private int numericValue;

    private CardValue(final int numericValue)
    {
        this.numericValue = numericValue;
    }

    public int getNumericValue()
    {
        return numericValue;
    }
}
