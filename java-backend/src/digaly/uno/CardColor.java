package digaly.uno;

/**
 * Created by Tom Dobbelaere on 2/07/2016.
 */
public enum CardColor
{
    BLUE("blue"),
    GREEN("green"),
    RED("red"),
    YELLOW("yellow"),
    JOKER("special");

    private String colorName;

    private CardColor(final String colorName)
    {
        this.colorName = colorName;
    }

    public String getColorName()
    {
        return colorName;
    }
}
