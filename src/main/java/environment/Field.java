package environment;

import actors.Entity;

import java.util.List;

/**
 * Field is the abstract representation of a data layer which
 * encapsulates the locations of objects in relation to one
 * another.
 * @param <E> the Objects within the field layer
 * @param <L> the location / relation of Objects
 *           to each other
 */
public interface Field<E, L> extends Zone {
    void initialise();
    void clearAll();
    void place(L l, E e);
    void clearLocation(L l);
    E getObjectAt(L l);
    List<L> getAllAdjacentLocations(L l);
    List<L> getAllFreeAdjacentLocations(L l);
    L freeAdjacentLocation(L l);
    <T extends E> boolean isNeighbourTo(L l, Class<T> c);
    <T extends E> List<T> getAllNeighbours(L l, Class<T> c);
    int getDimensions();
    List<E> getAllEntities();
}
