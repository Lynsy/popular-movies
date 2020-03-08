package udacity.project.lynsychin.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class Trailer {

    @SerializedName("iso_639_1")
    String Iso639;

    @SerializedName("iso_3166_1")
    String Iso3166;

    String id;
    String key;
    String name;
    String site;
    Long size;
    String type;

    public String getIso639() {
        return Iso639;
    }

    public void setIso639(String iso639) {
        Iso639 = iso639;
    }

    public String getIso3166() {
        return Iso3166;
    }

    public void setIso3166(String iso3166) {
        Iso3166 = iso3166;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
