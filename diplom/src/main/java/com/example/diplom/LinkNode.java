package com.example.diplom;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveTask;


public class LinkNode extends RecursiveTask<Set<String>> {

    public static final Logger LOGGER = LogManager.getLogger(DiplomApplication.class);
    private static final Set<String> mapOfSite = new TreeSet<>();
    private final String link;

    public LinkNode(String link) {
        this.link = link;
    }


    @Override
    protected Set<String> compute() {
        List<LinkNode> taskList = new ArrayList<>();
        Set<String> childSetOfLink = LinkNode.getChilds(link);
        System.out.println(Thread.currentThread().getName() + " : " + mapOfSite.size());
        for (String child : childSetOfLink) {
            LinkNode task = new LinkNode(child);
            task.fork();
            taskList.add(task);
        }
        for (LinkNode linkNode : taskList) {
            linkNode.join();
        }
        return mapOfSite;
    }

    public static synchronized Set<String> getChilds(String link) {
        Set<String> childSetOfLink = new TreeSet<>();

        try {
            Document doc;
            doc = Jsoup.connect(link).ignoreContentType(true).ignoreHttpErrors(true).maxBodySize(0).timeout(0).get();
            Elements elements = doc.select("a[href]");
            elements.forEach(element -> {
                String childLink = element.attr("abs:href");
                if ((LinkNode.checkChildLink(childLink)) && (LinkNode.isNotAlreadyAddedToMapOfSite(childLink))){
                    childSetOfLink.add(childLink);
                }
            });
            Thread.sleep(125);
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e);
        }
        return childSetOfLink;
    }
    private static boolean isNotAlreadyAddedToMapOfSite(String link) {

        return mapOfSite.add(link);
    }

    private static boolean checkChildLink(String childLink) {

        return (childLink.startsWith(DiplomApplication.ROOT_URL) & (!childLink.contains("#"))
                & (!childLink.endsWith(".jpg"))) ;
    }
}


