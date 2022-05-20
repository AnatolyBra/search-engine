package com.example.diplom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class PrintToTxtFile {
    public static final Logger LOGGER = LogManager.getLogger(DiplomApplication.class);

    public StringBuilder siteMapToOutput(String prefix, Set<String> siteMap){
        StringBuilder output = new StringBuilder();
        for(String link : siteMap){
            output.append(tabsForAppend(prefix, link));
            output.append(link);
            output.append("\n");
        }
        return output;
    }

    public String tabsForAppend(String prefix, String link) {
        String strForAppend;
        String replacedString = link.replaceFirst(prefix, "");
        if (link.startsWith(prefix)) {
            int countOfTabs;
            countOfTabs = replacedString.split("\\/",-1).length -1;
            String tab = "\t";
            strForAppend = tab.repeat(countOfTabs);
        }else {
            strForAppend = "";
        }
        return strForAppend;
    }

    public void print(String path, String output) {
        try ( FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            fileOutputStream.write(output.getBytes(StandardCharsets.UTF_8));
            System.out.println("Выгрузка карты сайта " + DiplomApplication.ROOT_URL +
                    " успешна завершена в файл " + path + " ! ");
        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }
}
