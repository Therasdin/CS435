package FinalProject;

import java.io.Serializable;

/**
 * Song Data Model - used by Spark/Hadoop to create a DataFrame or pass objects in MapReduce
 */
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;
    private String tag;
    private String artist;
    private String year;
    private long views;
    private String features;
    private String lyrics;
    private String id;
    private String language;

    // Empty constructor required for Spark/Hadoop
    public Song() {}

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public long getViews() { return views; }
    public void setViews(long views) { this.views = views; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getLyrics() { return lyrics; }
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    // ------------------------
    // Extra methods for debugging and collections
    // ------------------------

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", tag='" + tag + '\'' +
                ", artist='" + artist + '\'' +
                ", year='" + year + '\'' +
                ", views=" + views +
                ", id='" + id + '\'' +
                ", language='" + language + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return id != null && id.equals(song.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
