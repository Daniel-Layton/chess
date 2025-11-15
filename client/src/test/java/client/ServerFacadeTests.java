package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import ui.models.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade sf;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8765);
        System.out.println("Started test HTTP server on " + port);
        sf = new ServerFacade("http://localhost:8765");
    }

    @AfterAll
    static void cleanupServer() {

        server.stop();
    }

    @BeforeEach
    public void clearDatabase() {
        try {
            sf.clear();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear database!");
        }
    }

    @Test
    public void validRegisterRequestReceivesCorrectResult() {
        // given - state, create serverfacade, make request
        RegisterRequest request = new RegisterRequest("bob", "bob1234", "bob@gmail.com");
        // when - call serverfade
        try {
            RegisterResult result = sf.register(request);
            // then - results
            Assertions.assertEquals("bob", result.username());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void invalidRegisterRequestReceivesCorrectResult() {
        // given - state, create serverfacade, make request
        RegisterRequest request = new RegisterRequest("bob", "bob1234", "");
        // when - call serverfade
        try {
            RegisterResult result = sf.register(request);
            // then - results
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void validLoginRequestReceivesCorrectResult() {
        // given - state, create serverfacade, make request
        try {
            RegisterResult result1 = sf.register(new RegisterRequest("bob", "bob1234", "bob@gmail.com"));
            sf.logout(new LogoutRequest(result1.authToken()));
            LoginRequest request = new LoginRequest("bob1234", "bob");
            // when - call serverfade
            LoginResult result = sf.login(request);
            // then - results
            Assertions.assertEquals(result.username(), result1.username());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void invalidLoginRequestReceivesCorrectResult() {
        try {
            RegisterResult result1 = sf.register(new RegisterRequest("bob", "bob1234", "bob@gmail.com"));
            sf.logout(new LogoutRequest(result1.authToken()));
            LoginRequest request = new LoginRequest("bob123", "bob");
            // when - call serverfade
            LoginResult result = sf.login(request);
            // then - results
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    public void validLogoutRequestReceivesCorrectResult() {}

    public void invalidLogoutRequestReceivesCorrectResult() {}

    public void validCreateRequestReceivesCorrectResult() {}

    public void invalidCreateRequestReceivesCorrectResult() {}

    public void validListRequestReceivesCorrectResult() {}

    public void invalidListRequestReceivesCorrectResult() {}

    public void validJoinRequestReceivesCorrectResult() {}

    public void invalidJoinRequestReceivesCorrectResult() {}

}
