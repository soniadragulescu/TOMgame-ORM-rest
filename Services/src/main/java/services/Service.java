package services;

import model.Game;
import model.User;
import repos.GameRepo;
import repos.UserRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements IService {
    private UserRepo userRepo;
    private GameRepo gameRepo;
    private List<IObserver> observers;
    private static Integer participants;
    private static Integer responses;
    List<String> letters= Arrays.asList("A", "B", "C", "D","E","F", "G");
    List<String> countries=Arrays.asList("Albania", "Algeria", "Austria", "Bolivia", "Brazil","Bulgaria","China", "Chile", "Colombia", "Denmark", "Estonia", "Ethiopia", "France", "Fiji", "Greece", "Germany");
    List<String> cities=Arrays.asList("Bucharest", "Fagaras", "Brasov", "Cluj", "Constanta", "Galati", "Arad", "Bacau", "Deva", "Calarasi", "Drobeta", "Focsani", "Gherla", "Dorohoi", "Dej");
    List<String> seas=Arrays.asList("Black", "Caspian", "Caribbean", "Baltic", "Adriatic", "Bering", "EastChina", "Greenland", "Flores","Coral");
    List<User> users;
    private final int defaultThreadsNo=5;

    public Service(UserRepo userRepo, GameRepo gameRepo) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        observers=new ArrayList<>();
        users=new ArrayList<>();
        this.participants=0;
        this.responses=0;
    }

    @Override
    public User login(IObserver client, String username, String password) {
        if(participants>=3){
            return null;
        }
        else{
            User user=userRepo.findOne(username, password);
            if(user!=null){
                observers.add(client);
                users.add(user);
                participants+=1;
                if(participants==3){
                    List<String> randomCategories=createRandomLetters();
                    Game game=new Game(randomCategories, this.users);
                    gameRepo.save(game);
                    startGame();
                }
            }
            return user;
        }
    }

    List<String> createRandomLetters(){
//        Random random = new Random();
//        List<String> randomCategories=new ArrayList<>();
//        for(int i=0; i<5; i++){
//            Integer category=random.nextInt()%3-1;
//            randomCategories.add(this.letters.get(category));
//        }
        List<String> randomCategories=Arrays.asList("A", "C", "F");
        return randomCategories;
    }

    @Override
    public void logout(IObserver client, String username) {
        observers.remove(client);
    }

    @Override
    public void updateGame(String user, String country, String city, String sea) {
        Game game=gameRepo.getLast();
        List<User> players=game.getUsers();
        Integer poz=-1;
        for(User u:players){
            poz+=1;
            if(u.getUsername().equals(user))
                break;
        }

        //update the answers sent for this player
        if(poz.equals(0)){
            List<String> words=new ArrayList<>();
            for(String w:game.getUser1())
                words.add(w);
            words.add(country);
            words.add(city);
            words.add(sea);
            game.setUser1(words);
        }

        if(poz.equals(1)){
            List<String> words=new ArrayList<>();
            for(String w:game.getUser2())
                words.add(w);
            words.add(country);
            words.add(city);
            words.add(sea);
            game.setUser2(words);
        }

        if(poz.equals(2)){
            List<String> words=new ArrayList<>();
            for(String w:game.getUser3())
                words.add(w);
            words.add(country);
            words.add(city);
            words.add(sea);
            game.setUser3(words);
        }

        this.responses+=1;
        if(this.responses==3){
            Integer round=game.getRound();

            //get the score for each user
            List<String> answers1=new ArrayList<>();
            List<String> answers2=new ArrayList<>();
            List<String> answers3=new ArrayList<>();

            Integer start=round*3;
            Integer finish=round*3+3;

            List<String> user1=new ArrayList<>();
            for(String ans:game.getUser1()){
                user1.add(ans);
            }

            List<String> user2=new ArrayList<>();
            for(String ans:game.getUser2()){
                user2.add(ans);
            }

            List<String> user3=new ArrayList<>();
            for(String ans:game.getUser1()){
                user3.add(ans);
            }

            answers1.add(user1.get(start));
            answers1.add(user2.get(start));
            answers1.add(user3.get(start));

            answers2.add(user1.get(start+1));
            answers2.add(user2.get(start+1));
            answers2.add(user3.get(start+1));

            answers3.add(user1.get(start+2));
            answers3.add(user2.get(start+2));
            answers3.add(user3.get(start+2));

            List<Integer> scores=getScore(answers1, answers2, answers3);
            int i=0;
            for(User u: game.getUsers()){
                Integer initialscore=u.getScore();
                u.setScore(initialscore+scores.get(i));
                i++;
                userRepo.update(u);
            }
            this.responses=0;

            round+=1;
            game.setRound(round);
        }


        gameRepo.update(game);
        if(this.responses==0){
            notifyUsers();
        }
    }

    List<Integer> getScore(List<String> answers1, List<String> answers2, List<String> answers3){
        Game lastGame=gameRepo.getLast();
        char letter=lastGame.getLetters().get(lastGame.getRound()).charAt(0);
        Integer score1=0;
        Integer score2=0;
        Integer score3=0;

        List<Integer> scores=new ArrayList<>();

        List<String> tari=new ArrayList<>();
        tari.addAll(answers1);

        List<String> orase=new ArrayList<>();
        orase.addAll(answers2);

        List<String> mari=new ArrayList<>();
        mari.addAll(answers3);

        //for user 1
        if(countries.contains(answers1.get(0))&&answers1.get(0).charAt(0)==letter){
            score1+=3;
            if(Collections.frequency(tari,answers1.get(0))==1){
                score1+=7;
            }
        }

        if(cities.contains(answers2.get(0))&&answers2.get(0).charAt(0)==letter){
            score1+=3;
            if(Collections.frequency(orase,answers2.get(0))==1){
                score1+=7;
            }
        }

        if(seas.contains(answers3.get(0))&&answers3.get(0).charAt(0)==letter){
            score1+=3;
            if(Collections.frequency(mari,answers3.get(0))==1){
                score1+=7;
            }
        }

        //for user 2
        if(countries.contains(answers1.get(1))&&answers1.get(1).charAt(0)==letter){
            score2+=3;
            if(Collections.frequency(tari,answers1.get(1))==1){
                score2+=7;
            }
        }

        if(cities.contains(answers2.get(1))&&answers2.get(1).charAt(0)==letter){
            score2+=3;
            if(Collections.frequency(orase,answers2.get(1))==1){
                score2+=7;
            }
        }

        if(seas.contains(answers2.get(1))&&answers3.get(1).charAt(0)==letter){
            score2+=3;
            if(Collections.frequency(mari,answers3.get(1))==1){
                score2+=7;
            }
        }

        //for user 3
        if(countries.contains(answers1.get(2))&&answers1.get(2).charAt(0)==letter){
            score3+=3;
            if(Collections.frequency(tari,answers1.get(2))==1){
                score3+=7;
            }
        }

        if(cities.contains(answers2.get(2))&&answers2.get(2).charAt(0)==letter){
            score3+=3;
            if(Collections.frequency(orase,answers2.get(2))==1){
                score3+=7;
            }
        }

        if(seas.contains(answers3.get(2))&&answers3.get(2).charAt(0)==letter){
            score3+=3;
            if(Collections.frequency(mari,answers3.get(2))==1){
                score3+=7;
            }
        }

        scores.add(score1);
        scores.add(score2);
        scores.add(score3);
        return scores;


    }

    @Override
    public void startGame() {
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer:observers) {
            executor.execute(() -> {
                try {
                    System.out.println("notifying users there are 3 players...");
                    observer.gameStart();
                } catch (Exception e) {
                    System.out.println("error notifying users...");
                }
            });
        }
        executor.shutdown();
    }

    @Override
    public void actualGameStarted() {
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer:observers) {
            executor.execute(() -> {
                try {
                    System.out.println("notifying users button START was pressed...");
                    observer.actualGameStarted();
                } catch (Exception e) {
                    System.out.println("error notifying users...");
                }
            });
        }
        executor.shutdown();

        notifyUsers();
    }

    private void notifyUsers(){
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer:observers) {
            executor.execute(() -> {
                try {
                    System.out.println("notifying users for the next round...");
                    List<User> players=new ArrayList<>();
                    Game game=gameRepo.getLast();
                    for(User u: game.getUsers()) {
                        players.add(u);
                    }
                    game.setUsers(players);
                    List<String> letters=new ArrayList<>();
                    Integer round=game.getRound();
                    game.setRound(round);
                    for(String c:game.getLetters()){
                        letters.add(c);
                    }
                    game.setLetters(letters);
                    observer.nextRound(game);
                } catch (Exception e) {
                    System.out.println("error notifying users...");
                }
            });
        }
        executor.shutdown();
    }
}
