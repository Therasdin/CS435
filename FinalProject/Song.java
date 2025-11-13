package FinalProject;

import java.util.Arrays;
import java.util.List;

public class Song {
    private String title;
    private String tag;          // genre
    private String artist;
    private int year;
    private long views;
    private List<String> features;
    private String lyrics;
    private String id;
    private String languageCld3;
    private String languageFt;
    private String language;

    public Song(String title, String tag, String artist, int year, long views,
                List<String> features, String lyrics, String id,
                String languageCld3, String languageFt, String language) {
        this.title = title;
        this.tag = tag;
        this.artist = artist;
        this.year = year;
        this.views = views;
        this.features = features;
        this.lyrics = lyrics;
        this.id = id;
        this.languageCld3 = languageCld3;
        this.languageFt = languageFt;
        this.language = language;
    }

    // Convenience constructor for comma-separated features
    public static List<String> parseFeatures(String featureString) {
        if (featureString == null || featureString.isEmpty()) return List.of();
        // remove braces and quotes like {"Cam\\'ron","Opera Steve"}
        String clean = featureString.replaceAll("[{}\"\\\\]", "");
        String[] parts = clean.split(",");
        return Arrays.asList(parts);
    }

    // Getters
    public String getTitle() { return title; }
    public String getTag() { return tag; }
    public String getArtist() { return artist; }
    public int getYear() { return year; }
    public long getViews() { return views; }
    public List<String> getFeatures() { return features; }
    public String getLyrics() { return lyrics; }
    public String getId() { return id; }
    public String getLanguageCld3() { return languageCld3; }
    public String getLanguageFt() { return languageFt; }
    public String getLanguage() { return language; }

    @Override
    public String toString() {
        return "Song [title=" + title + ", tag=" + tag +
               ", artist=" + artist + ", year=" + year +
               ", views=" + views + ", id=" + id + "]";
    }
}
