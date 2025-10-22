package service;
import dataaccess.AlreadyTakenException;
import dataaccess.MemoryUserDAO;
import service.models.*;

import java.util.UUID;

public class GameService {

    MemoryUserDAO database = new MemoryUserDAO();

    public ListResult list(ListRequest listRequest) throws AlreadyTakenException {
        return null;
    }
    public CreateResult create(CreateRequest createRequest) {
        return null;
    }
    public JoinResult join(JoinRequest joinRequest) {
        return null;
    }
}