package com.winger.webtv.service;

import com.winger.webtv.repositories.MoviesRepository;
import com.winger.webtv.entity.Movie;
import com.winger.webtv.util.Translit;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class Aggregator {

    MoviesRepository repository;

    // URL = HOST + $page + CATEGORY_URL;
    private final String URL_HOST;
    private final String URL_HOST_BROWSE;
    private final String URL_TOP_PEERS;
    //private final Properties imageLibrary;

    public Aggregator(MoviesRepository repository) {
        this.repository = repository;
        //imageLibrary = new Properties();
        Properties prop = new Properties();
        try {
            String LIBRARY = "src/main/resources/images.properties";
            String PROPERTIES = "src/main/resources/settings.properties";
            //imageLibrary.load(new FileReader(LIBRARY));
            prop.load(new FileReader(PROPERTIES));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        URL_HOST = prop.getProperty("URL_HOST");
        URL_HOST_BROWSE = prop.getProperty("URL_HOST_BROWSE");
        URL_TOP_PEERS = prop.getProperty("URL_TOP_PEERS");
    }

    public List<Movie> getMovies(int n)  {
        List<Movie> resList = new ArrayList<>();

        for (int j = 0; j < n ; j++) {
            try {
                Elements elements = parseHtmlPage("", j);
                for (int i = 0; i < 100; i++) {
                    resList.add(parseMovie(elements.get(i)));
                }
            } catch (IOException g){
                System.out.println("Exception in parsing page or movie");
            }

        }
        /*try {
            imageLibrary.store(new FileWriter("src/main/resources/images.properties"), "");
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }*/
        return resList;

    }

    public Elements parseHtmlPage(String cat, int page) throws IOException {
        Document document = Jsoup.parse(new URL(URL_HOST_BROWSE + page + URL_TOP_PEERS), 5000);
        return document.select("#index table tbody tr:not(.backgr)");
    }

    public Movie parseMovie(Element e) throws IOException {
        String date = e.select("td:first-child").text();
        String title = e.select("td:nth-child(2) a:last-child").text();
        String year;
        try {
            year = title.substring(title.indexOf("(") + 1, title.indexOf(")"));
        }catch (StringIndexOutOfBoundsException ex){
            year = "2020";
        }
        String ruTitle;
        try {
            ruTitle = title.substring(0, title.indexOf("/")).trim();
        }catch (IndexOutOfBoundsException ex){
            ruTitle = title.substring(0, title.indexOf("(")).trim();
        }
        String enTitle;
        try{
            if (title.contains("/")){
                enTitle = title.substring(title.lastIndexOf("/") +1, title.indexOf("(")).trim();
            } else {
                enTitle = Translit.getInstance().translate(ruTitle.toLowerCase());
            }
        }catch (IndexOutOfBoundsException ex){
            enTitle = Translit.getInstance().translate(ruTitle.toLowerCase());
        }
        String torrentLink = e.select("td:nth-child(2) a:nth-child(2)").attr("href");
        String size = e.select("td:last-child").prev().text();
        int peers;
        try {
         peers = Integer.parseInt(e.select("td:last-child .green").text());
        } catch (NumberFormatException ex){
            peers = 100;
        }
        String urlPage = URL_HOST + e.select("td:nth-child(2) a:last-child").attr("href");
        String id = urlPage.substring(urlPage.indexOf("torrent/") + 8, urlPage.lastIndexOf("/"));
        //String poster = getPosterLink(enTitle, urlPage);

        return new Movie(id, date, year, title, ruTitle, enTitle, size, peers, torrentLink, urlPage);


    }

    /*private String getPosterLink(String enTitle, String urlPage) throws IOException {
        String imgKey = enTitle.replaceAll(" ", "-").replaceAll(":", "")
                .toLowerCase().trim();

        if (imageLibrary.containsKey(imgKey)){
            //System.out.println("Already exists --- " + enTitle);
            return imageLibrary.getProperty(imgKey);
        }

        String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

        Document doc = Jsoup.parse(new URL(urlPage), 5000);
        String imgLink = doc.select("#details tbody tr:first-child td:nth-child(2) img:not(a img)")
                .attr("src");
        String fileName = "img/" + imgKey + ".jpg";
        String filePath = "src/main/webapp/" + fileName;
        HttpURLConnection con;
        try {
        con = (HttpURLConnection) new URL(imgLink).openConnection();
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept", "application/image");
        con.setConnectTimeout(2000);
        con.setReadTimeout(2000);
        }catch (IOException ex){
            return "img/no_img.png";
        }
        File file = new File(filePath);
        try {
            FileUtils.copyInputStreamToFile(
                    con.getInputStream(),
                    file
            );
        }catch (Exception ex){
            System.out.println("Exception - " + ex.getMessage() + " in - " + enTitle + ", Link is - " + imgLink);
            return "img/no_img.png";
        }
        if (file.exists() && file.length() > 1000) {
                imageLibrary.put(imgKey, fileName);
                System.out.println("Downloaded --- " + enTitle + " ---- " + file.length());

                List<Movie> list = repository.searchByTitle(enTitle);
                if (list != null){
                        list.forEach(m-> {
                            if (m.getPoster().equals("img/no_img.png")){
                                m.setPoster(fileName);
                                repository.save(m);
                                System.out.println("Repostering: " + enTitle);
                            }
                        });
                }

                return fileName;
        }

        return "img/no_img.png";
    }*/
}

