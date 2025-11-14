package ui;

import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import ui.models.RegisterRequest;
import ui.models.RegisterResult;

public class REPL {
    ServerFacade server;
    int status;

    public REPL(String serverUrl) throws Exception {
        this.server = new ServerFacade(serverUrl);
        this.status = 0;
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
        System.out.print("\n" + ">>> ");
    }

    private String test() {
        return "\nTested\n";
    }

    public String eval0(String input) {

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help0();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String eval1(String input) { return null; }

    public String eval2(String input) { return null; }

    public String help0() {
        String l1 = "register <USERNAME> <PASSWORD> <EMAIL> - to create an account\n";
        String l2 = "login <USERNAME> <PASSWORD> - to log in\n";
        String l3 = "quit - to exit the program\n";
        String l4 = "help - to see the help menu\n";
        return l1 + l2 + l3 + l4;
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
            return "Signed in as new user - " + result.username();
        }
        catch(Exception e) {
            System.out.println(e);
            return "Registration failed. User may already exist";
        }
    }
}
