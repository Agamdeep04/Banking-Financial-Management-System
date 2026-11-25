package util;

import java.util.*;

// Generic class demonstration: generic class, generic methods, diamond operator, bounded wildcard
public class GenericStorage<T> {
    private final List<T> items = new ArrayList<>();

    public void add(T item) { items.add(item); }
    public T get(int index) { return items.get(index); }
    public int size() { return items.size(); }

    // Generic method with diamond operator usage in caller
    public static <E> void swap(List<E> list, int i, int j) {
        E temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    // Bounded wildcard <? extends Number>
    public static double sum(List<? extends Number> numbers) {
        double total = 0;
        for (Number n : numbers) total += n.doubleValue();
        return total;
    }

    public void printAll() {
        items.forEach(System.out::println);
    }
}
