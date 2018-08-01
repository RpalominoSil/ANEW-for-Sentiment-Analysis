package mulan.ASBCT;

import com.sun.org.apache.xalan.internal.xsltc.dom.Filter;
import com.sun.org.apache.xalan.internal.xsltc.dom.SimpleResultTreeImpl;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Lemmatization {
    static List<String> tokenlist = new ArrayList<>();
    public static void request(String inputpath, String outputpath){
        String key = "XXXXX" // YOUR API KEY;
        String lang = "es";
        //String txt = "Descubrí mi blog de filosofía y pensamiento contemporáneo, Anuncios de cursos y talleres, Entrevistas. Escritos sob… https://t.co/yIg7QuNWxz\n";
        String docpath = inputpath;

        BufferedReader br = null;
        Writer sb = null;
        String line = "";
        File file = new File(outputpath);
        Integer count = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(docpath),"ISO-8859-1"));
            sb = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"));
            while ((line = br.readLine()) != null) {
                tokenlist = new ArrayList<>();
                innerrequest(key,lang,line);
                Thread.sleep(500);
                StringBuilder sentence = new StringBuilder();
                for (String token: tokenlist) {
                    sentence.append(token + " ");
                }
                sb.append(sentence);
                sb.append('\n');
                System.out.println("["+count.toString()+"] "+sentence);
                count++;
            }
            br.close();
            sb.flush();
            sb.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    private static void innerrequest(String key, String lang, String txt) {
        OkHttpClient client = new OkHttpClient();
        //MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", key)
                .addFormDataPart("lang",lang)
                .addFormDataPart("txt",txt)
                //.addFormDataPart("doc",doc.getName(),RequestBody.create(MediaType.parse("text/csv"),ous.toByteArray()))
                .build();
        Request request = new Request.Builder()
                .url("http://api.meaningcloud.com/parser-2.0")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObj1 = new JSONObject(response.body().string());
            //System.out.println(jsonObj1.toString());
            JSONArray sentencesPrev = jsonObj1.getJSONArray("token_list");
            getToken(sentencesPrev);
            System.out.println(response.body().string());
            //System.out.println(tokens.toString());
        } catch (Exception e) {
            //System.out.println(e);
        }
    }

    private static void getToken(JSONArray sentence){
        for (int i=0;i<sentence.length();i++) {
            try {
                JSONObject jsonObj = sentence.getJSONObject(i);
                JSONArray phrase = jsonObj.getJSONArray("token_list");
                //System.out.println(phrase.toString());
                getToken(phrase);
            } catch (Exception e) {
                try {
                    JSONObject jsonObj = sentence.getJSONObject(i);
                    JSONArray analisislist = jsonObj.getJSONArray("analysis_list");
                    JSONObject analisis = analisislist.getJSONObject(0);
                    Object tag = analisis.get("tag");
                    if (!tag.toString().equals("1D--")) {
                        Object word = analisis.get("lemma");
                        tokenlist.add(word.toString());
                        //System.out.println(word.toString());
                    }
                }catch (Exception ex) {
                    try {
                        JSONObject jsonObj = sentence.getJSONObject(i);
                        Object word = jsonObj.get("form");
                        tokenlist.add(word.toString());
                        //System.out.println(word.toString());
                    } catch (Exception exc) {
                        System.out.println(exc);
                    }
                }
            }
        }
    }
    public static byte[] docToCsv(String docpath) {
        BufferedReader br = null;
        ByteArrayOutputStream ous = null;
        String line = "";
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(docpath),"ISO-8859-1"));
            ous = new ByteArrayOutputStream();
            while ((line = br.readLine()) != null) {
                ous.write(line.getBytes());
            }
            ous.close();
            br.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return ous.toByteArray();
    }
}
