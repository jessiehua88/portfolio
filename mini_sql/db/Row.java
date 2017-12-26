package db;

import java.util.ArrayList;
import java.util.StringJoiner;

public class Row{
    private ArrayList<Item> items;

    public Row(){
        items = new ArrayList<>();
    }
    public Row(ArrayList<Item> items){
        this.items = items;
    }

    public int getSize(){
        return items.size();
    }

    public void addItem(Item item){
        items.add(item);
    }

    public void addItem(int index, Item item){
        items.add(index, item);
    }

    public Item getItem(int index) { return items.get(index); }

    public String print(){
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < items.size(); i++) {
            String toAdd = items.get(i).print();
            joiner.add(toAdd);
        }
        return joiner.toString() + "\n";
    }

    public void delete(int index){
        items.remove(index);
    }

    public ArrayList<Item> getItems(){
        return items;
    }

    public Row copyRow(){
        Row newRow = new Row();
        for (Item x : items){
            newRow.addItem(x.copy());
        }
        return newRow;
    }

    public Row concatRow(Row other){
        ArrayList<Item> list = (ArrayList<Item>) this.items.clone();
        list.addAll(other.getItems());
        return new Row(list);
    }
}
