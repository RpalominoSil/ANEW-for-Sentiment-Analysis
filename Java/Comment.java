package mulan.ASBCT;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    public String comment;
    public Integer unpleasant;
    public Integer pleasant;
    public Integer calm;
    public Integer excited;
    public Integer outOfControl;
    public Integer inControl;
    public List<Integer> wordIdList;

    public Comment(String comment) {
        this.comment = comment;
        this.unpleasant = 0;
        this.pleasant = 0;
        this.calm = 0;
        this.excited = 0;
        this.outOfControl = 0;
        this.inControl = 0;
        this.wordIdList = new ArrayList<>();
    }

    public Comment(String comment, Integer unpleasant, Integer pleasant, Integer calm,
                   Integer excited, Integer outOfControl, Integer inControl) {
        this.comment = comment;
        this.unpleasant = unpleasant;
        this.pleasant = pleasant;
        this.calm = calm;
        this.excited = excited;
        this.outOfControl = outOfControl;
        this.inControl = inControl;
        this.wordIdList = new ArrayList<>();
    }
}
