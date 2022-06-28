package com.winger.webtv.service;

import com.winger.webtv.entity.Description;
import com.winger.webtv.entity.Movie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
public class DescriptionGetter {

    public Description getDescription (Movie movie) throws IOException, InterruptedException {

        Document doc = null;
        try {
            doc = Jsoup.parse(new URL(movie.getUrlPage()), 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element font;
        try {
            font = doc.getElementById("details").select("tbody tr:first-child td:nth-child(2)").first();
        } catch (NullPointerException e){
            throw new MalformedURLException();
        }
        return parseDescription(movie.getEnTitle(), font.wholeText(), doc);
    }

    private Description parseDescription(String title, String whole, Element doc) {
        List<String > list = Arrays.stream(whole.split("\n"))
                .filter(s->!s.isBlank()).skip(1).limit(17).toList();

        String genre = "undef";
        String director = "undef";
        String casting = "undef";
        String info = "undef";
        String time = "undef";
        String src;

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (s.startsWith("Жанр")){
                genre = s.split(":")[1].trim();
            }
            else if (s.startsWith("Режиссер") || s.startsWith("Режиссёр")){
                director = s.split(":")[1].trim();
            }
            else if (s.startsWith("В ролях")){
                casting = s.split(":")[1].trim();
                if (casting.length() > 250) casting = casting.substring(0, 250);
            }
            else if (s.startsWith("О фильме") || s.startsWith("Описание")){
                info = s.split(":")[1];
                if (info.isBlank()){
                    info = list.get(i + 1).trim();
                    i++;
                }
            }
            else if (s.startsWith("Продолжительность")){
                time = s.replaceFirst(":", "#").split("#")[1].trim();
            }
        }
        src = getSrcLink(doc);

        return new Description(title, genre, director,casting, info, time, src);
    }

    private String getSrcLink(Element doc){
        String link =  doc.select("#details tbody tr:first-child td:nth-child(2) img:not(a img)")
                .attr("src");
        if (link.contains("radikal")) link = getAlternateSrc(doc);
        return link;

    }
    @Nullable
    private String getSrcLink(String url){
        Document doc;
        String imgSrc = null;
        try {
            doc = Jsoup.parse(new URL(url), 5000);
            imgSrc = doc.select("#details tbody tr:first-child td:nth-child(2) img:not(a img)")
                    .attr("src");
        } catch (IOException e) {
            System.out.println("Error in getSrcLink(String url)" + e.getMessage());
        }

        return imgSrc;

    }

    private String getAlternateSrc(Element doc){
        Elements trs = doc.select("#index tr");
        String img = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/No-Image-Placeholder.svg/1665px-No-Image-Placeholder.svg.png";
        for (int i = 1; i < Math.min(trs.size(), 10); i++) {
            String urlPage = "http://rutor.info" + trs.get(i).select("td:nth-child(2) a:last-child").attr("href");
            String tempSrc = getSrcLink(urlPage);
            if (tempSrc != null && !tempSrc.contains("radikal")) {
                img = tempSrc;
                System.out.println("Proceeded altImage");
                break;
            }
        }
        return img;
    }




}
