package mulan.ASBCT;

public class Attribute {

    public Integer id;
    public String word;
    public Integer unpleasant;
    public Integer pleasant;
    public Integer calm;
    public Integer excited;
    public Integer inControl;
    public Integer outOfControl;

    public Attribute(Integer id, String wotd) {
        this.id = id;
        this.word = wotd;
        this.unpleasant = 0;
        this.pleasant = 0;
        this.calm = 0;
        this.excited = 0;
        this.outOfControl = 0;
        this.inControl = 0;
    }

    public Attribute(Integer id, String word, Integer unpleasant, Integer pleasant, Integer calm,
                     Integer excited, Integer outOfControl, Integer inControl) {
        this.id = id;
        this.word = word;
        this.unpleasant = unpleasant;
        this.pleasant = pleasant;
        this.calm = calm;
        this.excited = excited;
        this.outOfControl = outOfControl;
        this.inControl = inControl;
    }
}
