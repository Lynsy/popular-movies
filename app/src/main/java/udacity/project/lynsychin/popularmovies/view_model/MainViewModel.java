package udacity.project.lynsychin.popularmovies.view_model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import udacity.project.lynsychin.popularmovies.R;
import udacity.project.lynsychin.popularmovies.database.MovieDatabase;
import udacity.project.lynsychin.popularmovies.database.MovieEntry;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<MovieEntry>> movies;

    public MainViewModel(Application application){
        super(application);
        MovieDatabase db = MovieDatabase.getInstance(this.getApplication());
        movies = db.movieDao().loadAllMovies();
    }

    public LiveData<List<MovieEntry>> getMovies() { return movies; }

    public boolean hasMovie(int id){
        MovieDatabase db = MovieDatabase.getInstance(this.getApplication());
        MovieEntry movie = db.movieDao().loadMovieById(id);
        return movie != null;
    }

    public void addMovie(MovieEntry movie){
        MovieDatabase db = MovieDatabase.getInstance(this.getApplication());
        db.movieDao().insertMovie(movie);
    }

}
