package com.winger.webtv.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    private String id;
    private String date;
    private String year;
    private String title;
    private String ruTitle;
    private String enTitle;
    private String size;
    private Integer peers;
    private String torrentLink;
    private String urlPage;
    @OneToOne
    private Description description;


    public Movie(String id, String date, String year, String title, String ruTitle, String enTitle,
                 String size, Integer peers, String torrentLink, String urlPage) {
        this.id = id;
        this.date = date;
        this.year = year;
        this.title = title;
        this.ruTitle = ruTitle;
        this.enTitle = enTitle;
        this.size = size;
        this.peers = peers;
        this.torrentLink = torrentLink;
        this.urlPage = urlPage;
    }

    public Movie() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRuTitle() {
        return ruTitle;
    }

    public void setRuTitle(String ruTitle) {
        this.ruTitle = ruTitle;
    }

    public String getEnTitle() {
        return enTitle;
    }

    public void setEnTitle(String enTitle) {
        this.enTitle = enTitle;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getPeers() {
        return peers;
    }

    public void setPeers(Integer peers) {
        this.peers = peers;
    }

    public String getTorrentLink() {
        return torrentLink;
    }

    public void setTorrentLink(String torrentLink) {
        this.torrentLink = torrentLink;
    }

    public String getUrlPage() {
        return urlPage;
    }

    public void setUrlPage(String urlPage) {
        this.urlPage = urlPage;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "urlId='" + id + '\'' +
                ", date='" + date + '\'' +
                ", year='" + year + '\'' +
                ", title='" + title + '\'' +
                ", ruTitle='" + ruTitle + '\'' +
                ", enTitle='" + enTitle + '\'' +
                ", size='" + size + '\'' +
                ", peers='" + peers + '\'' +
                ", torrentLink='" + torrentLink + '\'' +
                ", urlPage='" + urlPage + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(enTitle, movie.enTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enTitle);
    }
}