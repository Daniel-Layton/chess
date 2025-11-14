package ui;

import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import ui.models.RegisterRequest;
import ui.models.RegisterResult;

public class REPL {
    ServerFacade server;

    public REPL(String serverUrl) throws Exception {
        this.server = new ServerFacade(serverUrl);
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
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

    private String test() {
        return "\nTested\n";
    }

    public String eval(String input) {

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                /*
                case "signin" -> signIn(params);
                case "rescue" -> rescuePet(params);
                case "list" -> listPets();
                case "signout" -> signOut();
                case "adopt" -> adoptPet(params);
                case "adoptall" -> adoptAllPets();
                */
                case "reg" -> register(params);
                case "quit" -> "quit";
                default -> "help()";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String[] params) {
        if (params.length < 3) {
            return "Usage: reg <username> <password> <email>";
        }
        RegisterResult result;
        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
        try {
            result = server.register(request);
            return result.toString();
        }
        catch(Exception e) {
            return "Registration failed. Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
    }
}
