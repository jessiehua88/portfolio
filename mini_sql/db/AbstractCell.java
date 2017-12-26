package db;

public abstract class AbstractCell<T, E extends AbstractCell>{
    protected String type;
    abstract public String print();
    public String getType(){
        return type;
    }
    public abstract E copy();
}
