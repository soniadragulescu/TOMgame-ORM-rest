package games;

import model.Game;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repos.GameRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/games/")
public class GamesController {
    private static final String template="TemplatePaper";

    @Autowired
    private GameRepo gameRepo;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable Integer id){
        Game game=gameRepo.findOne(id);
        if(game == null){
            return new ResponseEntity<String>("Game not found!", HttpStatus.NOT_FOUND);
        }
        else{
            List<User> users=new ArrayList<>();
            for(User u:game.getUsers())
                users.add(u);
            Collections.sort(users, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.getScore().compareTo(o2.getScore());
                }
            });
            game.setUsers(users);
            return new ResponseEntity<>(game, HttpStatus.OK);
        }
    }

}

