package mulan.ASBCT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Tagging {

    public static List<Comment> tagInstances(List<Comment> commentList, List<Attribute> attributeList){
        List<Comment> sentiCommets = new ArrayList<>();
        for (Comment comment : commentList) {
            Comment sentiComment = tagInstance(comment,attributeList);
            if (sentiComment.unpleasant != 0 || sentiComment.pleasant != 0 ||
                sentiComment.calm != 0 || sentiComment.excited != 0 ||
                sentiComment.inControl != 0 || sentiComment.outOfControl != 0) {
                sentiCommets.add(sentiComment);
            }
        }
        return sentiCommets;
    }

    public static Comment tagInstance(Comment comment, List<Attribute> attributeList) {
        Integer unpleasant= 0;
        Integer pleasant = 0;
        Integer calm = 0;
        Integer excited = 0;
        Integer inControl = 0;
        Integer outOfControl = 0;
        int count = 0;

        String[] wordvector = comment.comment.split(" ");

        for (Attribute attribute : attributeList) {
            for (String wotd : wordvector) {
                if (wotd.toLowerCase().equals(attribute.word.toLowerCase())) {
                    if (unpleasant == 0) {unpleasant = attribute.unpleasant;}
                    if (pleasant == 0) {pleasant = attribute.pleasant;}
                    if (calm == 0) {calm = attribute.calm;}
                    if (excited == 0) {excited = attribute.excited;}
                    if (inControl == 0) {inControl = attribute.inControl;}
                    if (outOfControl == 0) {outOfControl = attribute.outOfControl;}
                    count = count + 1;

                    if (!comment.wordIdList.contains(attribute.id)) {comment.wordIdList.add(attribute.id);}

                    /*System.out.println(attribute.word);
                    System.out.println("valence: "   + attribute.valence + "\t" +
                            "Arousal: "   + attribute.arousal + "\t" +
                            "Dominance: " + attribute.dominance);*/
                }
            }
        }

        comment.unpleasant =  unpleasant;
        comment.pleasant = pleasant;
        comment.calm = calm;
        comment.excited = excited;
        comment.inControl = inControl;
        comment.outOfControl = outOfControl;

        /*System.out.println(comment.comment);
        System.out.println("valence avg: "   + comment.valence + "\t" +
                "Arousal avg: "   + comment.arousal + "\t" +
                "Dominance avg: " + comment.dominance);
        System.out.println("");*/

        return comment;
    }
}
