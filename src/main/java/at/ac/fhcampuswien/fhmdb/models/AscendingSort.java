package at.ac.fhcampuswien.fhmdb.models;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AscendingSort implements SortState {
    @Override
    public void sort(List<Movie> movies) {
        Collections.sort(movies, Comparator.comparing(Movie::getTitle));
    }
}
