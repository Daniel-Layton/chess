package service;
import dataaccess.DataAccessException;
import dataaccess.DataBank;
import dataaccess.SQLClearDAO;
import service.models.ClearResult;

public class ClearService {

    SQLClearDAO clearDB = new SQLClearDAO();

    public ClearResult clear() throws DataAccessException {
        DataBank.getInstance().users.clear();
        DataBank.getInstance().auths.clear();
        DataBank.getInstance().games.clear();
        clearDB.clearTables();
        return new ClearResult();
    }
}
