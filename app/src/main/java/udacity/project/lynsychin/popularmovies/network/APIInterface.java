package udacity.project.lynsychin.popularmovies.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import udacity.project.lynsychin.popularmovies.model.MovieDB;

public interface APIInterface {

    @GET("movie/{sort}?")
    Call<MovieDB> getMovies(@Path("sort") String sort, @Query("api_key") String api_key);
}
