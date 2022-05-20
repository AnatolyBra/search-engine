package com.example.diplom;

import com.example.diplom.controller.Solution;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;

@SpringBootApplication
public class DiplomApplication {

    //    public static final String ROOT_URL = "http://www.playback.ru/";
    public static final String ROOT_URL = "https://tortishnaya.ru/";
    public static final String TXT_FILE = "map_of_site.txt";
    public static final HashMap<String, Integer> frequencyTitle = new HashMap<>();
    public static final HashMap<String, Integer> frequencyBody = new HashMap<>();

    public static void main(String[] args) throws IOException, SQLException {

        SpringApplication.run(DiplomApplication.class, args);


        LinkNode linkNode = new LinkNode(ROOT_URL);
        System.out.println("Поток : Количество уникальных ссылок");
        Set<String> mapOfSite = new ForkJoinPool().invoke(linkNode);
        mapOfSite.add(ROOT_URL);
        System.out.println(mapOfSite.size());
//        PrintToTxtFile printToTxtFile = new PrintToTxtFile();
//        String output = printToTxtFile.siteMapToOutput(ROOT_URL, mapOfSite).toString();
//        printToTxtFile.print(TXT_FILE, output);
        Date date = new Date();
        long start = System.currentTimeMillis();
        System.out.println(date + " поехали");
        for (String s : mapOfSite) {
            long startLine = System.currentTimeMillis();

            Connection.Response response = Jsoup.connect(s).newRequest()
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(0)
                    .followRedirects(true)
                    .execute();

            Document doc = response.parse();

            s = s.replace(ROOT_URL, "/");

            DBConnection.executeInsertPage(response.statusCode(), doc.html(), s);
            DBConnection.executeSelector("title", doc.tagName("title").tagName());
            DBConnection.executeSelector("body", doc.body().tagName());
            Solution solution = new Solution();
            for (Map.Entry<String, Integer> entry : solution.getList(doc.tagName("title").text()).entrySet()) {
                frequencyTitle.put(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Integer> entry : solution.getList(doc.tagName("body").text()).entrySet()) {
                frequencyBody.put(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Integer> entry : solution.getList(doc.html()).entrySet()) {
                DBConnection.executeInsertLemma(entry.getKey(), entry.getValue());

            }
            for (Map.Entry<String, Integer> entry : solution.getList(doc.html()).entrySet()) {

                DBConnection.calculateRank( s, frequencyTitle.get(entry.getKey()), frequencyBody.get(entry.getKey()));
            }
            /** нужно посчитать количество повторений в тэгах
             * title
             * body
             *
             * нужно это число теперь вставить в формулу rank
             * как попасть "в посчитать в одном цикле" значения повторений как для title так и для body
             * мне нужна коллекция hashmap где будет и количество в title и в количестве body?
            * **/

            System.out.println((System.currentTimeMillis() - startLine) / 1000 + " s");
        }
        System.out.println((System.currentTimeMillis() - start) / 60000 + " min");
        System.out.println((System.currentTimeMillis() - start) + " ms");
        System.out.println("конец");
        DBConnection.getConnection().close();

    }
}


