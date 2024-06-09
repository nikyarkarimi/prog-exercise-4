package at.ac.fhcampuswien.fhmdb.controllers;

import at.ac.fhcampuswien.fhmdb.ClickEventHandler;
import at.ac.fhcampuswien.fhmdb.api.MovieAPI;
import at.ac.fhcampuswien.fhmdb.api.MovieApiException;
import at.ac.fhcampuswien.fhmdb.database.*;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.models.AscendingSort;
import at.ac.fhcampuswien.fhmdb.models.DescendingSort;
import at.ac.fhcampuswien.fhmdb.controllers.MovieListContext;
import at.ac.fhcampuswien.fhmdb.controllers.Observer;
import at.ac.fhcampuswien.fhmdb.ui.MovieCell;
import at.ac.fhcampuswien.fhmdb.ui.UserDialog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MovieListController implements Initializable, Observer {
    @FXML
    public JFXButton searchBtn;

    @FXML
    public TextField searchField;

    @FXML
    public JFXListView<Movie> movieListView;

    @FXML
    public JFXComboBox genreComboBox;

    @FXML
    public JFXComboBox releaseYearComboBox;

    @FXML
    public JFXComboBox ratingFromComboBox;

    @FXML
    public JFXButton sortBtn;

    public List<Movie> allMovies;

    protected ObservableList<Movie> observableMovies = FXCollections.observableArrayList();

    private MovieListContext movieListContext;

    private final ClickEventHandler onAddToWatchlistClicked = (clickedItem) -> {
        if (clickedItem instanceof Movie movie) {
            WatchlistMovieEntity watchlistMovieEntity = new WatchlistMovieEntity(movie.getId());
            try {
                WatchlistRepository repository = WatchlistRepository.getInstance();
                repository.addToWatchlist(watchlistMovieEntity);
            } catch (DataBaseException e) {
                UserDialog dialog = new UserDialog("Database Error", "Could not add movie to watchlist");
                dialog.show();
                e.printStackTrace();
            }
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeState();
        initializeLayout();

        // Register as an observer
        try {
            WatchlistRepository.getInstance().addObserver(this);
        } catch (DataBaseException e) {
            showAlert("Database Error", "Could not register observer");
            e.printStackTrace();
        }
    }

    public void initializeState() {
        List<Movie> result;
        try {
            result = MovieAPI.getAllMovies();
            writeCache(result);
        } catch (MovieApiException e) {
            UserDialog dialog = new UserDialog("MovieAPI Error", "Could not load movies from api. Get movies from db cache instead");
            dialog.show();
            result = readCache();
        }

        setMovies(result);
        setMovieList(result);
        movieListContext = new MovieListContext(new AscendingSort());
    }

    private List<Movie> readCache() {
        try {
            MovieRepository movieRepository = MovieRepository.getInstance();
            return MovieEntity.toMovies(movieRepository.getAllMovies());
        } catch (DataBaseException e) {
            UserDialog dialog = new UserDialog("DB Error", "Could not load movies from DB");
            dialog.show();
            return new ArrayList<>();
        }
    }

    private void writeCache(List<Movie> movies) {
        try {
            MovieRepository movieRepository = MovieRepository.getInstance();
            movieRepository.removeAll();
            movieRepository.addAllMovies(movies);
        } catch (DataBaseException e) {
            UserDialog dialog = new UserDialog("DB Error", "Could not write movies to DB");
            dialog.show();
        }
    }

    public void initializeLayout() {
        movieListView.setItems(observableMovies);
        movieListView.setCellFactory(movieListView -> new MovieCell(onAddToWatchlistClicked));

        Object[] genres = Genre.values();
        genreComboBox.getItems().add("No filter");
        genreComboBox.getItems().addAll(genres);
        genreComboBox.setPromptText("Filter by Genre");

        releaseYearComboBox.getItems().add("No filter");
        Integer[] years = new Integer[124];
        for (int i = 0; i < years.length; i++) {
            years[i] = 1900 + i;
        }
        releaseYearComboBox.getItems().addAll(years);
        releaseYearComboBox.setPromptText("Filter by Release Year");

        ratingFromComboBox.getItems().add("No filter");
        Integer[] ratings = new Integer[11];
        for (int i = 0; i < ratings.length; i++) {
            ratings[i] = i;
        }
        ratingFromComboBox.getItems().addAll(ratings);
        ratingFromComboBox.setPromptText("Filter by Rating");
    }

    public void setMovies(List<Movie> movies) {
        allMovies = movies;
    }

    public void setMovieList(List<Movie> movies) {
        observableMovies.clear();
        observableMovies.addAll(movies);
    }

    public void sortBtnClicked(ActionEvent actionEvent) {
        sortMovies();
    }

    public void sortMovies() {
        if (movieListContext.getSortState() instanceof AscendingSort) {
            movieListContext.setSortState(new DescendingSort());
        } else {
            movieListContext.setSortState(new AscendingSort());
        }
        sortAndDisplayMovies();
    }

    private void sortAndDisplayMovies() {
        movieListContext.sortMovies(observableMovies);
    }

    public List<Movie> filterByQuery(List<Movie> movies, String query) {
        if (query == null || query.isEmpty()) return movies;

        if (movies == null) {
            throw new IllegalArgumentException("movies must not be null");
        }

        return movies.stream().filter(movie ->
                        movie.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        movie.getDescription().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public List<Movie> filterByGenre(List<Movie> movies, Genre genre) {
        if (genre == null) return movies;

        if (movies == null) {
            throw new IllegalArgumentException("movies must not be null");
        }

        return movies.stream().filter(movie -> movie.getGenres().contains(genre)).toList();
    }

    public void applyAllFilters(String searchQuery, Object genre) {
        List<Movie> filteredMovies = allMovies;

        if (!searchQuery.isEmpty()) {
            filteredMovies = filterByQuery(filteredMovies, searchQuery);
        }

        if (genre != null && !genre.toString().equals("No filter")) {
            filteredMovies = filterByGenre(filteredMovies, Genre.valueOf(genre.toString()));
        }

        setMovieList(filteredMovies);
        sortAndDisplayMovies();
    }

    public void searchBtnClicked(ActionEvent actionEvent) {
        String searchQuery = searchField.getText().trim().toLowerCase();
        String releaseYear = validateComboboxValue(releaseYearComboBox.getSelectionModel().getSelectedItem());
        String ratingFrom = validateComboboxValue(ratingFromComboBox.getSelectionModel().getSelectedItem());
        String genreValue = validateComboboxValue(genreComboBox.getSelectionModel().getSelectedItem());

        Genre genre = null;
        if (genreValue != null) {
            genre = Genre.valueOf(genreValue);
        }

        List<Movie> movies = getMovies(searchQuery, genre, releaseYear, ratingFrom);

        setMovies(movies);
        setMovieList(movies);
    }

    public String validateComboboxValue(Object value) {
        if (value != null && !value.toString().equals("No filter")) {
            return value.toString();
        }
        return null;
    }

    public List<Movie> getMovies(String searchQuery, Genre genre, String releaseYear, String ratingFrom) {
        try {
            return MovieAPI.getAllMovies(searchQuery, genre, releaseYear, ratingFrom);
        } catch (MovieApiException e) {
            System.out.println(e.getMessage());
            UserDialog dialog = new UserDialog("MovieApi Error", "Could not load movies from api.");
            dialog.show();
            return new ArrayList<>();
        }
    }

    @Override
    public void update(String message) {
        showAlert("Watchlist Notification", message);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
