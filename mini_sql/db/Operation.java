package db;

public interface Operation {
    Item apply(Item x, Item y);
    Item apply(Item x, String literal);
}
