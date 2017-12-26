package db;

public class Column<T> extends AbstractCell<T, Column>{
    private String name;

    public Column(String name, String type){
        this.name = name;
        this.type = type;
    }
    public String print(){
        return name + " " + type;
    }
    public String getName(){
        return name;
    }
    public Column copy(){
        return new Column(this.name, this.type);
    }
}
