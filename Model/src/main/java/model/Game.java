package model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Game")
public class Game implements Serializable {

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="Round")
    private Integer round;

    @ElementCollection
    private List<String> letters;

    @ElementCollection
    private List<User> users;

    @ElementCollection
    private List<String> user1;

    @ElementCollection
    private List<String> user2;

    @ElementCollection
    private List<String> user3;

    public Game() {
        this.letters=new ArrayList<>();
        this.users=new ArrayList<>();
        this.user1=new ArrayList<>();
        this.user2=new ArrayList<>();
        this.user3=new ArrayList<>();
    }

    public Game(Integer id, Integer round, List<String> letters, List<User> users,List<String> user1, List<String> user2, List<String> user3) {
        this.id = id;
        this.round = round;
        this.letters = letters;
        this.users=users;
        this.user1 = user1;
        this.user2 = user2;
        this.user3 = user3;
    }

    public Game(List<String> letters, List<User> users) {
        this.round=0;
        this.letters = letters;
        this.users=users;
        this.user1=new ArrayList<>();
        this.user2=new ArrayList<>();
        this.user3=new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public List<String> getLetters() {
        return letters;
    }

    public void setLetters(List<String> letters) {
        this.letters = letters;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<String> getUser1() {
        return user1;
    }

    public void setUser1(List<String> user1) {
        this.user1 = user1;
    }

    public List<String> getUser2() {
        return user2;
    }

    public void setUser2(List<String> user2) {
        this.user2 = user2;
    }

    public List<String> getUser3() {
        return user3;
    }

    public void setUser3(List<String> user3) {
        this.user3 = user3;
    }
}
