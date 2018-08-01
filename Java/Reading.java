package mulan.ASBCT;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Reading {

    public static  List<Attribute> readAttributes(String file) {
        String csvFile = file;//"/Users/mkyong/csv/country.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        List<Attribute> attributeList = new ArrayList<>();
        Integer id = 0;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] instance = line.split(cvsSplitBy);
                Attribute attribute = new Attribute(id++,
                                                    instance[0],
                                                    Integer.parseInt(instance[1]),
                                                    Integer.parseInt(instance[2]),
                                                    Integer.parseInt(instance[3]),
                                                    Integer.parseInt(instance[4]),
                                                    Integer.parseInt(instance[5]),
                                                    Integer.parseInt(instance[6]));
                attributeList.add(attribute);
            }
            StringUtils.NUMBER_INSTANCES = id;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return attributeList;
    }

    public static List<Comment> readComments(String file) {
        String csvFile = file;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        List<Comment> attributeList = new ArrayList<>();

        try {
            //br = new BufferedReader(new FileReader(csvFile));
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile),"ISO-8859-1"));
            while ((line = br.readLine()) != null) {
                String[] instance = line.split(cvsSplitBy);
                Comment comment = new Comment(instance[0]);
                attributeList.add(comment);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return attributeList;
    }
}
