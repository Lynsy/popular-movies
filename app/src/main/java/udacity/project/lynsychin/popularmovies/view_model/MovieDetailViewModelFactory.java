package udacity.project.lynsychin.popularmovies.view_model;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import udacity.project.lynsychin.popularmovies.database.MovieDatabase;

public class MovieDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieDatabase mDB;
    private final int mMovieId;

    public MovieDetailViewModelFactory(MovieDatabase db, int movieId){
        mDB = db;
        mMovieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieDetailViewModel(mDB, mMovieId);
    }
}
