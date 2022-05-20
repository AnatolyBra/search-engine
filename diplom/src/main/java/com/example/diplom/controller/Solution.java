package com.example.diplom.controller;


import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

public class Solution {


    private List<String> stringList = new ArrayList<>();
    private HashMap<String, Integer> hashMap = new HashMap<>();

    /**
     * подготовить коллекцию где будут отдельные очереди как для
     * **/

    public HashMap<String, Integer> getList(String letters) throws IOException {
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        String[] arr = clear(letters).split(" ");
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].length()>0){
                try {
                    List<String> wordBaseException =
                            luceneMorph.getMorphInfo(arr[i]);

                    if (!wordBaseException.get(0).contains("СОЮЗ") &&
                            !wordBaseException.get(0).contains("ПРЕДЛ") &&
                            !wordBaseException.get(0).contains("МЕЖД") &&
                            !wordBaseException.get(0).contains("ЧАСТ") &&
                            !wordBaseException.get(0).contains("Г") &&
                            !wordBaseException.get(0).contains("ИНФИНИТИВ") &&
                            !wordBaseException.get(0).contains("Н") &&
                            !wordBaseException.get(0).contains("МС") && arr[i].length() > 2
                    ) {

                        List<String> wordBaseForms = luceneMorph.getNormalForms(arr[i].trim());
                        if (hashMap.containsKey(wordBaseForms.get(0))) {
                            int count = hashMap.get(wordBaseForms.get(0));
                            count += 1;
                            hashMap.put(wordBaseForms.get(0), count);
                        } else hashMap.put(wordBaseForms.get(0), 1);
                    }
                } catch (WrongCharaterException ex) {
                    System.out.println("побежала ошибка табуляции");
                }
            }}
        return hashMap;
    }

    public String clear(String letters) {
        letters = letters.replaceAll("\\n|[^а-яА-Я\\s]|\\s{2}", "")
                .toLowerCase(Locale.ROOT);
        return letters;
    }

    public void print(HashMap<String, Integer> hashMap) {
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}

