package service;
import dataaccess.DataBank;
import service.models.ClearResult;

public class ClearService {
    public ClearResult clear() {
        System.out.println("Hit Clear Service!!!");
        DataBank.getInstance().users.clear();
        DataBank.getInstance().auths.clear();
        DataBank.getInstance().games.clear();
        return new ClearResult();
    }
}
