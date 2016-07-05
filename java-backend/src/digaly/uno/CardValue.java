package digaly.uno;

/**
 * Created by Tom Dobbelaere on 2/07/2016.
 */
public enum CardValue
{
    ZERO(0, "0"),
    ONE(1, "1"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    ADDTWO(10, "plustwo"),
    REVERSE(11, "reverse"),
    SKIP(12, "skip"),
    ADDFOUR(-1, "plusfour"),
    WILD(-2, "wild");

    private int numericValue;
    private String valueName;

    private CardValue(final int numericValue, final String valueName)
    {
        this.numericValue = numericValue;
        this.valueName = valueName;
    }

    public int getNumericValue()
    {
        return numericValue;
    }

    public boolean isSpecialCard() {
        return numericValue < 0 || numericValue > 9;
    }

    public String getValueName()
    {
        return valueName;
    }

}
