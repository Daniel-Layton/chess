package ui;

import java.util.Arrays;
import java.util.Scanner;
import java.util.StringJoiner;

import com.google.gson.Gson;
import model.GameData;
import ui.models.*;

public class REPL {
    ServerFacade server;
    int status;
    String auth;

    public REPL(String serverUrl) throws Exception {
        this.server = new ServerFacade(serverUrl);
        this.status = 0;
        this.auth = null;
        //ws = new WebSocketFacade(serverUrl, this);
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
        else {
            System.out.print("\n" + "[LOGGED_IN] >>> ");
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
        String l2 = "help - to see the help menu\n";
        return l1 + l2;
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
        LoginRequest request = new LoginRequest(params[1], params[0]);
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

            for (GameData game : result.games()) {
                pseudoID++;
                String name = game.gameName();
                String white = game.whiteUsername() != null ? game.whiteUsername() : "empty";
                String black = game.blackUsername() != null ? game.whiteUsername() : "empty";
                String gameString = String.format(
                        " %d. Game name: %s\tWhite: %s\tBlack: %s",
                        pseudoID,
                        name,
                        white,
                        black
                );
                joiner.add(gameString);
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
        JoinResult result;
        JoinRequest request = new JoinRequest(auth, params[0]);
        try {
            result = server.join(request);
            return drawBoard(result.)
        }
        catch(Exception e) {
            return "Join failed: No Board or No Room";
        }
    }
}
