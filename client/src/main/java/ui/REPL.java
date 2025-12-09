package ui;

import java.util.*;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;
import ui.models.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class REPL {
    ServerFacade server;
    WebSocketFacade ws;
    int status;
    String auth;
    Map<Integer, GameData> gameList = new HashMap<>();
    int joinedGamePsudoID = 0;
    int joinedGameRole = 0;


    public REPL(String serverUrl) throws Exception {
        this.server = new ServerFacade(serverUrl);
        this.status = 0;
        this.auth = null;
        this.ws = new WebSocketFacade(serverUrl, this::handleServerMessage);
    }

    private void handleServerMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                System.out.println("\n[GAME UPDATE]");
                System.out.println(new DrawBoard(message.getGame()).draw(joinedGameRole==2));
            }
            case ERROR -> System.out.println("\n[ERROR] " + message.getErrorMessage());
            case NOTIFICATION -> System.out.println("\n[NOTIFICATION] " + message.getMessage());
        }
        printPrompt();
    }

    public void run() {
        System.out.println(" Welcome to 240 chess. Type help to get started.");
//        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                if (status == 0) {
                    result = eval0(line);
                    System.out.print(result);
                } else if (status == 1) {
                    result = eval1(line);
                    System.out.print(result);
                }
                else {
                    result = eval2(line);
                    if (Objects.equals(result, "resign")) {
                        System.out.println("Are you sure you want to resign? ('yes/no')");
                        String conf = scanner.nextLine();
                        if (Objects.equals(conf, "yes")) {
                            result = resign();
                        }
                        else result = " ";
                    }
                    System.out.print(result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        if (this.status == 0) {
            System.out.print("\n" + "[LOGGED_OUT] >>> ");
        }
        else if (this.status == 1) {
            System.out.print("\n" + "[LOGGED_IN] >>> ");
        }
        else {
            System.out.print("\n" + "[IN_GAME] >>> ");
        }
    }

    public String eval0(String input) {

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help0();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String eval1(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "help" -> help1();
                case "quit" -> "quit";
                default -> help1();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String eval2(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help2();
                case "quit" -> "quit";
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "valid" -> valid(params);
                case "resign" -> "resign";
                default -> help2();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help0() {
        String l1 = "  register <USERNAME> <PASSWORD> <EMAIL> - to create an account\n";
        String l2 = "  login <USERNAME> <PASSWORD> - to log in\n";
        String l3 = "  quit - to exit the program\n";
        String l4 = "  help - to see the help menu\n";
        return l1 + l2 + l3 + l4;
    }

    public String help1() {
        String l1 = "  create <NAME> - create a new chess game\n";
        String l2 = "  list - to see all games\n";
        String l3 = "  join <ID> [WHITE|BLACK] - join a game\n";
        String l4 = "  observe <ID> - spectate\n";
        String l5 = "  logout - logs out the user\n";
        String l6 = "  quit - to exit the program\n";
        String l7 = "  help - to see the help menu\n";
        return l1 + l2 + l3 + l4 + l5 + l6 + l7;
    }

    public String help2() {
        String l1 = "quit - to exit the program\n";
        String l2 = "redraw - to redraw the chessboard\n";
        String l3 = "leave - to exit the current lobby\n";
        String l4 = "move <b1> <c3> <promotion> - to move\n";
        String l5 = "valid <b1> - to see valid moves for square\n";
        String l6 = "resign - to give up\n";
        String l7 = "help - to see the help menu\n";
        return l1 + l2 + l3 + l4 + l5 + l6 + l7;
    }

    public String register(String[] params) {
        if (params.length != 3) {
            return "Registration failed. Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        RegisterResult result;
        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
        try {
            result = server.register(request);
            status = 1;
            auth = result.authToken();
            return "Signed in as new user - " + result.username();
        }
        catch(Exception e) {
            return "Registration failed. User may already exist";
        }
    }

    public String login(String[] params) {
        if (params.length != 2) {
            return "Login failed. Usage: login <USERNAME> <PASSWORD>";
        }
        LoginResult result;
        LoginRequest request = new LoginRequest(params[0], params[1]);
        try {
            result = server.login(request);
            status = 1;
            auth = result.authToken();
            return "Signed in as user - " + result.username();
        }
        catch(Exception e) {
            return "Login failed: Incorrect username or password";
        }
    }

    public String logout() {
        LogoutResult result;
        LogoutRequest request = new LogoutRequest(auth);
        try {
            result = server.logout(request);
            status = 0;
            auth = null;
            return "Logged out of user";
        }
        catch(Exception e) {
            return "Logout failed: Auth Token Error";
        }
    }

    public String create(String[] params) {
        if (params.length != 1) {
            return "Create game failed. Usage: create <GAME NAME>";
        }
        CreateRequest request = new CreateRequest(auth, params[0]);
        try {
            server.create(request);
            return "Successfully created game " + params[0];
        }
        catch(Exception e) {
            return "Create failed: Bad Auth Token";
        }
    }

    public String list() {
        ListResult result;
        ListRequest request = new ListRequest(auth);
        try {
            result = server.list(request);
            if (result.games() == null || result.games().isEmpty()) {
                return "No games found on the server.";
            }
            int pseudoID = 0;
            StringJoiner joiner = new StringJoiner("\n");
            gameList.clear();

            for (GameData game : result.games()) {
                pseudoID++;
                String name = game.gameName();
                String white = game.whiteUsername() != null ? game.whiteUsername() : "empty";
                String black = game.blackUsername() != null ? game.blackUsername() : "empty";
                String gameString = String.format(
                        " %d. Game name: %s\tWhite: %s\tBlack: %s",
                        pseudoID,
                        name,
                        white,
                        black
                );
                joiner.add(gameString);
                gameList.put(pseudoID, game);
            }
            return joiner.toString();
        }
        catch(Exception e) {
            return "List failed: Bad auth token";
        }
    }

    public String join(String[] params) {
        if (params.length != 2) {
            return "join game failed. Usage: join <GAME ID> <COLOR>";
        }
        try {
        ChessGame.TeamColor joinColor;
        if (Objects.equals(params[1], "white")) {
            joinColor = ChessGame.TeamColor.WHITE;
        }
        else if (Objects.equals(params[1], "black")) {
            joinColor = ChessGame.TeamColor.BLACK;
        }
        else {
            return "join game failed. Color value must be 'white' or 'black'";
        }
        int pseudoId = Integer.parseInt(params[0]);
        GameData gameData = gameList.get(pseudoId);
        JoinRequest request = new JoinRequest(auth, joinColor, gameData.gameID());

            server.join(new JoinRequest(auth, joinColor, gameData.gameID()));

            list();
            gameData = gameList.get(pseudoId);

            if (ws != null) ws.connect(auth, gameData.gameID());

            status = 2;
            if (joinColor == ChessGame.TeamColor.WHITE) joinedGameRole = 1;
            else joinedGameRole = 2;
            joinedGamePsudoID = pseudoId;
            return new DrawBoard(gameData.game()).draw(joinColor != ChessGame.TeamColor.WHITE);
        }
        catch(Exception e) {
            return "Join failed: No Board with ID " + params[0] + " or " + params[1] + " already taken";
        }
    }

    public String observe(String[] params) {
        if (params.length != 1) {
            return "observe game failed. Usage: observe <GAME ID>";
        }
        try {
            joinedGameRole = 3;
            status = 2;
            joinedGamePsudoID = Integer.parseInt(params[0]);
            if (ws != null) ws.connect(auth, gameList.get(joinedGamePsudoID).gameID());
            return new DrawBoard(gameList.get(joinedGamePsudoID).game()).draw(false);
        } catch (Exception e) {
            return "Observe failed: No Board with that ID";
        }
    }

    public String redraw() {
        list();
        return new DrawBoard(gameList.get(joinedGamePsudoID).game()).draw(joinedGameRole == 2);
    }

    public String leave() throws Exception {
        if (ws != null) ws.leave(auth, gameList.get(joinedGamePsudoID).gameID());
        status = 1;
        joinedGameRole = 0;
        joinedGamePsudoID = 0;
        return " ";
    }

    public String move(String[] params) throws Exception {
        if (params.length != 2 && params.length != 3) {
            return "move piece failed. Usage: move <b1> <c3> <promotion>";
        }

        if (!isColInRange(params[0].charAt(0))) {
            return "move piece failed. Usage: move <b1> <c3> <promotion>";
        }
        if (!isRowInRange(params[0].charAt(1))) {
            return "move piece failed. Usage: move <b1> <c3> <promotion>";
        }
        if (!isColInRange(params[1].charAt(0))) {
            return "move piece failed. Usage: move <b1> <c3> <promotion>";
        }
        if (!isRowInRange(params[1].charAt(1))) {
            return "move piece failed. Usage: move <b1> <c3> <promotion>";
        }

        // e2 e4 -> params[0] params[1]

        ChessPosition start = new ChessPosition(params[0].charAt(1)-48, rowLetterParser(params[0].charAt(0)));
        ChessPosition end = new ChessPosition(params[1].charAt(1)-48, rowLetterParser(params[1].charAt(0)));

        ChessPiece.PieceType promotionPiece;
        if (params.length == 3) promotionPiece = PromotionParser(params[2]);
        else promotionPiece = null;
        ChessMove move = new ChessMove(start, end, promotionPiece);
        if (ws != null) ws.makeMove(auth, gameList.get(joinedGamePsudoID).gameID(), move);
        return " ";
    }

    private int rowLetterParser(char letter) {
        if (letter == 'a') return 1;
        if (letter == 'b') return 2;
        if (letter == 'c') return 3;
        if (letter == 'd') return 4;
        if (letter == 'e') return 5;
        if (letter == 'f') return 6;
        if (letter == 'g') return 7;
        if (letter == 'h') return 8;
        return 0;
    }

    private boolean isRowInRange(char number) {
        if (number == '1') return true;
        if (number == '2') return true;
        if (number == '3') return true;
        if (number == '4') return true;
        if (number == '5') return true;
        if (number == '6') return true;
        if (number == '7') return true;
        if (number == '8') return true;
        return false;
    }

    private boolean isColInRange(char letter) {
        if (letter == 'a') return true;
        if (letter == 'b') return true;
        if (letter == 'c') return true;
        if (letter == 'd') return true;
        if (letter == 'e') return true;
        if (letter == 'f') return true;
        if (letter == 'g') return true;
        if (letter == 'h') return true;
        return false;
    }

    private ChessPiece.PieceType PromotionParser(String piece) {
        if (Objects.equals(piece, "rook")) return ChessPiece.PieceType.ROOK;
        if (Objects.equals(piece, "knight")) return ChessPiece.PieceType.KNIGHT;
        if (Objects.equals(piece, "bishop")) return ChessPiece.PieceType.BISHOP;
        if (Objects.equals(piece, "queen")) return ChessPiece.PieceType.QUEEN;
        return null;
    }

    public String valid(String[] params) throws Exception {
        if (params.length != 1) {
            return "valid check failed. Usage: valid <b1>";
        }
        list();
        ChessPosition start = new ChessPosition(params[0].charAt(1)-48, rowLetterParser(params[0].charAt(0)));
        new DrawValid(gameList.get(joinedGamePsudoID).game(), start).draw(joinedGameRole == 2);
        return " ";
    }

    public String resign() throws Exception {
        if (ws != null) ws.resign(auth, gameList.get(joinedGamePsudoID).gameID());
        return " ";
    }
}
