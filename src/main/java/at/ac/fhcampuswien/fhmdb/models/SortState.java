package at.ac.fhcampuswien.fhmdb.models;

import java.util.List;

/**
 * Sort State interface for declaring the state-specific methods
 */
public interface SortState {
    void sort(List<Movie> movies);
}
