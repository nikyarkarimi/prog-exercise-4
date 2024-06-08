package at.ac.fhcampuswien.fhmdb.controllers;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.models.SortState;
import at.ac.fhcampuswien.fhmdb.models.SortedState;

import java.util.List;

/**
 * Context class to manage the states
 */
public class MovieListContext {
    private SortState sortState;    // instantiate interface

    public MovieListContext(SortState sortState) {  // declaring state at start
        this.sortState = sortState;
    }

    public void setSortState(SortState sortState) {    // setting state at runtime
        this.sortState = sortState;
    }

    public SortState getSortState() {
        return sortState;
    }

    // methods to delegate the sorting logic to the respective states
    public void sortMovies(List<Movie> movies) {
        sortState.sort(movies);
    }
}
