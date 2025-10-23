import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.DataBank;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.*;
import service.models.*;

import java.util.HashMap;


public class service {

    private static UserService userService;
    private static GameService gameService;
    private static ClearService clearService;

    @AfterEach
    void cleanUp() {
        clearService.clear();
    }

    @BeforeAll
    public static void init() {
        userService = new UserService();
        gameService = new GameService();
        clearService = new ClearService();
    }

    @Test
    @Order(1)
    @DisplayName("Register Pass")
    public void registerPass() throws AlreadyTakenException {
        RegisterResult result = userService.register(new RegisterRequest("user", "u0u0u0", "u@email.com"));
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(2)
    @DisplayName("Register Fail (duplicate username)")
    public void registerFail() throws AlreadyTakenException {
        RegisterResult result1 = userService.register(new RegisterRequest("user", "u0u0u0", "u@email.com"));
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(new RegisterRequest("user", "u0u0u0", "u@email.com")));
    }

    @Test
    @Order(3)
    @DisplayName("Login Pass")
    public void loginPass() throws DataAccessException, AlreadyTakenException {
        userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        LoginResult result = userService.login(new LoginRequest("u1u1u1", "user1"));
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(4)
    @DisplayName("Login Fail (incorrect password)")
    public void loginFail() throws AlreadyTakenException {
        userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        Assertions.assertThrows(Exception.class, () -> userService.login(new LoginRequest("u1u1u2", "user1")));
    }

    @Test
    @Order(5)
    @DisplayName("Logout Pass")
    public void logoutPass() throws AlreadyTakenException, DataAccessException {
        userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        LoginResult loginRes = userService.login(new LoginRequest("u1u1u1", "user1"));
        LogoutResult result = userService.logout(new LogoutRequest(loginRes.authToken()));
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(6)
    @DisplayName("Logout Fail (bad auth token)")
    public void logoutFail() throws AlreadyTakenException, DataAccessException {
        userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        LoginResult loginRes = userService.login(new LoginRequest("u1u1u1", "user1"));
        Assertions.assertThrows(Exception.class, () -> userService.logout(new LogoutRequest("yolo")));
    }

    @Test
    @Order(7)
    @DisplayName("Create Pass")
    public void createPass() throws AlreadyTakenException, DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        CreateResult result = gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(8)
    @DisplayName("Create Fail (bad auth token)")
    public void createFail() throws AlreadyTakenException, DataAccessException {
        userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        Assertions.assertThrows(Exception.class, () -> gameService.create(new CreateRequest("yolo", "game1")));
    }

    @Test
    @Order(9)
    @DisplayName("Join Pass")
    public void joinPass() throws Exception {
        RegisterResult registerResult = userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        CreateResult createResult = gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        JoinResult result = gameService.join(new JoinRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, createResult.gameID()));
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(10)
    @DisplayName("Join fail (bad auth token)")
    public void joinFail() throws DataAccessException, AlreadyTakenException {
        RegisterResult registerResult = userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        CreateResult createResult = gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        Assertions.assertThrows(Exception.class, () -> gameService.join(new JoinRequest("yolo", ChessGame.TeamColor.WHITE, createResult.gameID())));
    }

    @Test
    @Order(11)
    @DisplayName("List Pass")
    public void listPass() throws DataAccessException, AlreadyTakenException {
        RegisterResult registerResult = userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        ListResult result = gameService.list(new ListRequest(registerResult.authToken()));
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(12)
    @DisplayName("List fail (bad auth token)")
    public void listFail() throws DataAccessException, AlreadyTakenException {
        RegisterResult registerResult = userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        Assertions.assertThrows(Exception.class, () -> gameService.list(new ListRequest("yolo")));
    }

    @Test
    @Order(13)
    @DisplayName("Clear Pass")
    public void clearPass() throws DataAccessException, AlreadyTakenException {
        RegisterResult registerResult = userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        clearService.clear();
        HashMap<String, UserData> users = DataBank.getInstance().users;
        HashMap<String, String> auths = DataBank.getInstance().auths;
        HashMap<String, GameData> games = DataBank.getInstance().games;
        Assertions.assertTrue(users.isEmpty());
        Assertions.assertTrue(auths.isEmpty());
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    @Order(14)
    @DisplayName("Clear fail (tests the clear test by not clearing)")
    public void clearFail() throws DataAccessException, AlreadyTakenException {
        RegisterResult registerResult = userService.register(new RegisterRequest("user1", "u1u1u1", "u1@email.com"));
        gameService.create(new CreateRequest(registerResult.authToken(), "game1"));
        HashMap<String, UserData> users = DataBank.getInstance().users;
        HashMap<String, String> auths = DataBank.getInstance().auths;
        HashMap<String, GameData> games = DataBank.getInstance().games;
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertFalse(auths.isEmpty());
        Assertions.assertFalse(games.isEmpty());
    }
}

