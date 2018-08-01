package mulan.ASBCT;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatasetGenerator {

    public static Integer unpleasantId = StringUtils.NUMBER_INSTANCES;
    public static Integer pleasantd = StringUtils.NUMBER_INSTANCES + 1;
    public static Integer calmId = StringUtils.NUMBER_INSTANCES + 2;
    public static Integer excitedId = StringUtils.NUMBER_INSTANCES + 3;
    public static Integer outOfControlId = StringUtils.NUMBER_INSTANCES + 4;
    public static Integer inControlId = StringUtils.NUMBER_INSTANCES + 5;

    public static String generateDataset(List<Comment> sentiCommentList, List<Attribute> attributeList) {
        String path = StringUtils.ASBCT_DATASET_ARFF;
        File file = new File(path);
        try {
            Writer sb = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"ISO-8859-1"));
            String relation = "@RELATION " +
                              "'DS: -C 6'";
            String newline = "" + '\n';
            String spaceline = "" + '\n'+'\n' ;

            sb.append(relation);
            sb.append(spaceline);

            for (Attribute attribute : attributeList) {
                String attr = "@ATTRIBUTE " + attribute.word + " {0,1}";
                sb.append(attr);
                sb.append(newline);
            }

            List<String> labels = new ArrayList<>();
            labels.add("unpleasant");
            labels.add("pleasant");
            labels.add("calm");
            labels.add("excited");
            labels.add("outOfControl");
            labels.add("inControl");

            for (String label : labels) {
                String attr = "@ATTRIBUTE " + label + " " + "{0,1}";
                sb.append(attr);
                sb.append(newline);
            }

            sb.append(newline);
            sb.append("@DATA");
            sb.append(newline);

            for (Comment comment : sentiCommentList) {
                StringBuilder instance = new StringBuilder();
                instance.append("{");

                for (Integer id : comment.wordIdList) {
                    instance.append(id.toString() + " 1,");
                }

                instance = new StringBuilder(instance.substring(0, instance.length() - 1));

                if (comment.unpleasant != 0) {instance.append("," + unpleasantId.toString() + " " + String.valueOf(comment.unpleasant));}
                if (comment.pleasant != 0)instance.append("," + pleasantd.toString() + " " + String.valueOf(comment.pleasant));
                if (comment.calm != 0)instance.append("," + calmId.toString() + " " + String.valueOf(comment.calm));
                if (comment.excited != 0)instance.append("," + excitedId.toString() + " " + String.valueOf(comment.excited));
                if (comment.outOfControl != 0)instance.append("," + outOfControlId.toString() + " " + String.valueOf(comment.outOfControl));
                if (comment.inControl != 0)instance.append("," + inControlId.toString() + " " + String.valueOf(comment.inControl));

                //instance = new StringBuilder(instance.substring(0, instance.length() - 1));
                instance.append("}");
                sb.append(instance);
                sb.append(newline);
            }
            sb.flush();
            sb.close();
            System.out.println(".arff done!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String generatedatasetHeader() {
        String path = StringUtils.ASBCT_DATASET_XML;
        File file = new File(path);
        try {
            Writer sb = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"ISO-8859-1"));
            sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+ '\n');
            sb.append("<labels xmlns=\"http://mulan.sourceforge.net/labels\">"+ '\n');
            sb.append("<label name=\"" + "unpleasant" + "\"></label>"+ '\n');
            sb.append("<label name=\"" + "pleasant" + "\"></label>"+ '\n');
            sb.append("<label name=\"" + "calm" + "\"></label>"+ '\n');
            sb.append("<label name=\"" + "excited" + "\"></label>"+ '\n');
            sb.append("<label name=\"" + "outOfControl" + "\"></label>"+ '\n');
            sb.append("<label name=\"" + "inControl" + "\"></label>"+ '\n');
            sb.append("</labels>");
            sb.flush();
            sb.close();
            System.out.println(".xml done!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
