import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;
import digaly.uno.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Tom Dobbelaere on 22/06/2016.
 */
public class Program
{
    private static final int port = 3005;
    private static final int[][] positions = {{1, 3, 2}, {0, 2, 3}, {3, 0, 1}, {2, 1, 0}}; //Top, right, left in that order

    public static void main(String[] args)
    {
        HashMap<String, Game> lobbies = new HashMap<>();
        SessionIdentifierGenerator identifierGenerator = new SessionIdentifierGenerator();

        Configuration config = new Configuration();
        //Testing locally:
        //config.setHostname("localhost");
        config.setPort(port);

        SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener()
        {
            @Override
            public void onConnect(SocketIOClient socketIOClient)
            {
                System.out.println(socketIOClient.getRemoteAddress());
            }
        });

        server.addEventListener("create game", String.class, new DataListener<String>()
        {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception
            {
                JSONObject request = new JSONObject(s);

                String sessionID = identifierGenerator.nextSessionId();

                socketIOClient.sendEvent("create response", sessionID);

                socketIOClient.set("username", request.getString("username"));
                socketIOClient.set("lobbyid", sessionID);
                socketIOClient.joinRoom(sessionID);

                System.out.println(socketIOClient.get("username") + " is creating a new game");

                Game newGame = new Game();
                newGame.addPlayer(request.getString("username"));

                lobbies.put(sessionID, newGame);
            }
        });

        server.addEventListener("join game", String.class, new DataListener<String>()
        {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception
            {
                JSONObject request = new JSONObject(s);

                String sessionID = request.getString("lobbyid");

                if (lobbies.get(sessionID).getPlayers().size() < 4 && !lobbies.get(sessionID).isStarted())
                {
                    socketIOClient.set("username", request.get("username").toString());
                    socketIOClient.set("lobbyid", sessionID);
                    socketIOClient.joinRoom(sessionID);

                    System.out.println(socketIOClient.get("username") + " is joining " + sessionID);

                    lobbies.get(sessionID).addPlayer(request.get("username").toString());

                    server.getRoomOperations(socketIOClient.get("lobbyid")).sendEvent("receive notification", request.getString("username") + " joined the game!");
                }
                else
                {
                    socketIOClient.sendEvent("game nojoin", "");
                }
            }
        });

        server.addEventListener("ready game", String.class, new DataListener<String>()
        {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception
            {
                String sessionID = socketIOClient.get("lobbyid");

                if (lobbies.get(sessionID).getPlayers().get(0).getName().equals(socketIOClient.get("username")) && lobbies.get(sessionID).getPlayers().size() > 1)
                {
                    lobbies.get(sessionID).start();
                    System.out.println(socketIOClient.get("username") + " started " + sessionID);
                    server.getRoomOperations(sessionID).sendEvent("ready game ok", "");
                    sendGameUpdate(server, sessionID, lobbies);
                }

            }
        });

        server.addEventListener("send chat", String.class, new DataListener<String>()
        {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception
            {
                server.getRoomOperations(socketIOClient.get("lobbyid")).sendEvent("receive chat", s);
            }
        });

        server.addEventListener("play card", String.class, new DataListener<String>()
        {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception
            {
                JSONObject request = new JSONObject(s);
                String cardColor = request.getString("color");
                String cardValue = request.getString("value");

                String sessionID = socketIOClient.get("lobbyid");

                if (lobbies.get(sessionID).getCurrentPlayer() == lobbies.get(sessionID).getPlayer(socketIOClient.get("username")))
                {
                    Player thisPlayer = lobbies.get(sessionID).getPlayer(socketIOClient.get("username"));
                    Card cardToPlay = null;

                    boolean hasPlayed = false;
                    for (Card card : thisPlayer.getHand().getCards())
                    {
                        if (cardToPlay == null && card.getCardColor().getColorName().equals(cardColor) && card.getCardValue().getValueName().equals(cardValue))
                        {
                            cardToPlay = card;
                        }
                    }

                    if (cardToPlay != null)
                    {
                        lobbies.get(sessionID).playCard(cardToPlay);
                        sendGameUpdate(server, sessionID, lobbies);

                        if (lobbies.get(sessionID).isOver())
                        {
                            server.getRoomOperations(socketIOClient.get("lobbyid")).sendEvent("receive notification", "The game is over!");
                        }
                    }
                }
            }
        });

        server.addEventListener("draw card", String.class, new DataListener<String>()
        {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception
            {
                String sessionID = socketIOClient.get("lobbyid");

                if (lobbies.get(sessionID).getCurrentPlayer() == lobbies.get(sessionID).getPlayer(socketIOClient.get("username")) && !lobbies.get(sessionID).isOver())
                {
                    Card discardCard = lobbies.get(sessionID).getDiscardPile().peekTopCard();

                    lobbies.get(sessionID).drawCard();

                    if (discardCard != lobbies.get(sessionID).getDiscardPile().peekTopCard())
                    {
                        server.getRoomOperations(socketIOClient.get("lobbyid")).sendEvent("receive notification", socketIOClient.get("username") + " drew a card and played it.");
                    }
                    else
                    {
                        server.getRoomOperations(socketIOClient.get("lobbyid")).sendEvent("receive notification", socketIOClient.get("username") + " drew a card.");
                    }

                    sendGameUpdate(server, sessionID, lobbies);
                }
            }
        });

        System.out.println("Starting UNO server on " + port + ", enter stop to close");
        server.start();

        Scanner s = new Scanner(System.in);
        while (!s.nextLine().equals("stop"))
        {

        }

        System.out.println("Please wait while the server is being closed...");
        server.stop();
        System.out.println("Done");
    }

    private static String getGameStateInfo(Game game, Player thisPlayer, int[] configuration)
    {
        JSONObject response = new JSONObject();
        response.put("hand", thisPlayer.getHand().getCards());
        response.put("top", game.getPlayers().get(configuration[0]).getHand().getCards().size());

        if (game.getPlayers().size() - 1 >= configuration[1])
            response.put("right", game.getPlayers().get(configuration[1]).getHand().getCards().size());

        if (game.getPlayers().size() - 1 >= configuration[2])
            response.put("left", game.getPlayers().get(configuration[2]).getHand().getCards().size());

        response.put("discard", new JSONObject(game.getDiscardPile().peekTopCard()));
        response.put("currentplayer", game.getCurrentPlayer().getName());

        return response.toString();
    }

    private static void sendGameUpdate(SocketIOServer server, String sessionID, HashMap<String, Game> lobbies)
    {
        for (SocketIOClient roomClient : server.getRoomOperations(sessionID).getClients())
        {
            Player thisPlayer = lobbies.get(sessionID).getPlayer(roomClient.get("username"));
            int thisPlayerIndex = lobbies.get(sessionID).getPlayers().indexOf(thisPlayer);
            int[] configuration = positions[thisPlayerIndex];

            roomClient.sendEvent("update gamestate", getGameStateInfo(lobbies.get(sessionID), thisPlayer, configuration));
        }
    }

    private static String toJSON(Object o)
    {
        return new JSONObject(o).toString();
    }
}
