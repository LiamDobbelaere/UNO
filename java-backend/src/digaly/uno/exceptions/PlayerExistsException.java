package digaly.uno.exceptions;

/**
 * Created by Tom Dobbelaere on 3/07/2016.
 */
public class PlayerExistsException extends Exception
{
    public PlayerExistsException()
    {
        super();
    }

    public PlayerExistsException(String message)
    {
        super(message);
    }
}
