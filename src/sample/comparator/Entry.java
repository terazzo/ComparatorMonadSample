package sample.comparator;

import java.util.Date;

public class Entry {
    private final Integer id;
    private final Integer weblogId;
    private final String title;
    private final String content;
    private final Date pubDate;
    public Entry(Integer id, Integer weblogId, Date pubDate,
            String title, String content) {
        // null‚Í‹–‰Â‚µ‚È‚¢•ûŒü‚ÅB
        if (id == null || pubDate == null) {
            throw new IllegalArgumentException("id or pubDate is null");
        }
        this.id = id;
        this.weblogId = weblogId;
        this.title = title;
        this.content = content;
        this.pubDate = pubDate;
    }
    public Integer getId() {
        return id;
    }
    public Integer getWeblogId() {
        return weblogId;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public Date getPubDate() {
        return pubDate;
    }
}