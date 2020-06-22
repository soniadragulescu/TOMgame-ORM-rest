package services;

import model.Game;
import model.User;

public interface IService {
    User login(IObserver client, String username, String password);
    void logout(IObserver client, String username);
    void updateGame(String user, String country, String city, String sea);
    void startGame();
    void actualGameStarted();
}
