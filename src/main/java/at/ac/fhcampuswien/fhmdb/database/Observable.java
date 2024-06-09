package at.ac.fhcampuswien.fhmdb.database;
import at.ac.fhcampuswien.fhmdb.controllers.Observer;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String message);
}
