package udacity.project.lynsychin.popularmovies.view_model;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import udacity.project.lynsychin.popularmovies.database.MovieDatabase;
import udacity.project.lynsychin.popularmovies.database.MovieEntry;

public class MovieDetailViewModel extends ViewModel {

    private LiveData<MovieEntry> movie;

    public MovieDetailViewModel(MovieDatabase db, int id){
        movie = db.movieDao().getMovieById(id);
    }

    public LiveData<MovieEntry> getMovie(){ return movie; }
}
