package udacity.project.lynsychin.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewDB {

    Integer id;
    Integer page;

    @SerializedName("total_pages")
    Integer totalPages;

    @SerializedName("total_results")
    Integer totalResults;

    @SerializedName("results")
    List<Review> reviews;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
