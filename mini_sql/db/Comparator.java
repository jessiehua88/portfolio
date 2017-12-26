package db;

public interface Comparator {
    boolean compare(Item x, Item y);
    boolean compare(Item x, String literal);
}
