package udacity.project.lynsychin.popularmovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MoviesDao {

    @Query("SELECT * FROM movie ORDER BY :sort")
    LiveData<List<MovieEntry>> loadAllMovies(String sort);

    @Insert
    void insertMovie(MovieEntry movieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieEntry movieEntry);

    @Query("SELECT * FROM movie WHERE id = :id")
    MovieEntry loadMovieById(int id);

}
