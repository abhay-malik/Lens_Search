package com.example.lens_search;

public class searchRVModal {
    private String title;
    private String link;
    private String displayed_link;
    private String snippet;

    public searchRVModal(String title, String link, String displayed_link, String snippet) {
        this.title = title;
        this.link = link;
        this.displayed_link = displayed_link;
        this.snippet = snippet;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDisplayed_link(String displayed_link) {
        this.displayed_link = displayed_link;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDisplayed_link() {
        return displayed_link;
    }

    public String getSnippet() {
        return snippet;
    }
}
