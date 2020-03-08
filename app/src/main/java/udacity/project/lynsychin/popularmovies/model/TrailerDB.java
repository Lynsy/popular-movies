package udacity.project.lynsychin.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerDB {

    Integer id;

    @SerializedName("results")
    List<Trailer> trailers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
