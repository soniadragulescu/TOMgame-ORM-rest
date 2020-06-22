package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Game;
import model.User;
import services.IObserver;
import services.IService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainController extends UnicastRemoteObject implements IObserver, Serializable {
    private IService service;
    private User user;

    public MainController() throws RemoteException {
    }

    public void setUser(User user){
        this.user=user;
    }

    public void setService(IService service){
        this.service=service;
        init();
    }

    @FXML
    Label labelWait;

    @FXML
    Label labelLetter;

    @FXML
    Label labelScore;

    @FXML
    Label labelRound;

    @FXML
    TextField textboxCountry;

    @FXML
    TextField textboxCity;

    @FXML
    TextField textboxSea;


    @FXML
    Button buttonStart;

    @FXML
    Button buttonLogout;

    @FXML
    Button buttonSendAnswers;



    public void init(){
        buttonStart.setVisible(false);

        labelRound.setVisible(false);
        labelLetter.setVisible(false);
        labelScore.setVisible(false);

        textboxCountry.setVisible(false);
        textboxCity.setVisible(false);
        textboxSea.setVisible(false);

        buttonSendAnswers.setVisible(false);
    }

    @FXML
    public void logout(){
        this.service.logout(this,this.user.getUsername());
        Platform.exit();
    }


    @Override
    public void gameStart(){
        labelWait.setVisible(false);

        buttonStart.setVisible(true);
    }

    @FXML
    public void startGame(){
        buttonStart.setVisible(false);

        labelLetter.setVisible(true);
        labelScore.setVisible(true);
        labelRound.setVisible(true);

        textboxCountry.setVisible(true);
        textboxCity.setVisible(true);
        textboxSea.setVisible(true);

        buttonSendAnswers.setVisible(true);

        service.actualGameStarted();
    }

    @Override
    public void actualGameStarted() throws RemoteException {
        buttonStart.setVisible(false);

        labelLetter.setVisible(true);
        labelScore.setVisible(true);
        labelRound.setVisible(true);

        textboxCountry.setVisible(true);
        textboxCity.setVisible(true);
        textboxSea.setVisible(true);

        buttonSendAnswers.setVisible(true);
    }


    private static void showErrorMessage(String err){
        Alert message = new Alert(Alert.AlertType.ERROR);
        message.setTitle("Error message!");
        message.setContentText(err);
        message.showAndWait();
    }

    @FXML
    public void sendAnswers() {
        String country=textboxCountry.getText();
        String city=textboxCity.getText();
        String sea=textboxSea.getText();
        service.updateGame( this.user.getUsername(),country, city, sea);
        buttonSendAnswers.setVisible(false);
    }

    @Override
    public void nextRound(Game game) throws RemoteException {
        User newuser=this.user;
        List<User> users=new ArrayList<>();
        for(User u: game.getUsers())
            users.add(u);
        game.setUsers(users);
        String scores="";
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return -o1.getScore().compareTo(o2.getScore());
            }
        });
        for(User u: users){
            scores+=u.getUsername()+": "+u.getScore().toString()+" puncte\n";
        }
        if(game.getRound()<=2){
            String finalScores = scores;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Integer round=game.getRound();
                    String nextLetter=game.getLetters().get(round);
                    for(User u:users) {
                        if (u.getUsername().equals(newuser.getUsername()))
                            newuser.setScore(u.getScore());
                    }

                    labelRound.setText("Round: "+round.toString());
                    labelLetter.setText("Litera pt. care trebuie introduse \n Tara, Oras, Mare\n este : "+nextLetter);
                    labelScore.setText(finalScores);

                    buttonSendAnswers.setVisible(true);
                }
            });
        }
        this.user.setScore(newuser.getScore());
        if(game.getRound()>2){
            String finalScores1 = scores;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    showErrorMessage("S-au gatat cele 3 runde, ai obtinut "+user.getScore().toString()
                    +"\n Clasamentul: \n"+ finalScores1);
                }
            });
//            service.logout(this, newuser.getUsername());
//            Platform.exit();
        }

    }
}
