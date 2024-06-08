package at.ac.fhcampuswien.fhmdb.models;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DescendingSort implements SortState{
    @Override
    public void sort(List<Movie> movies) {
        // Ascending Sort
        Collections.sort(movies, Comparator.comparing(Movie::getTitle).reversed());
    }
}
