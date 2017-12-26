package db;

public class Item<T> extends AbstractCell<T, Item>{
    private boolean nan;
    private boolean noValue;
    private T value;

    public Item(T value, String type, boolean noValue){
        this.value = value;
        this.type = type;
        this.nan = false;
        this.noValue = noValue;
     }

    public Item(T value, String type, boolean noValue, boolean nan){
        this.value = value;
        this.type = type;
        this.nan = nan;
        this.noValue = noValue;
    }

    public void changeToNaN() {
        this.nan = true;
        this.noValue = false;
        if (this.type == "float"){
            this.value = (T) (Float) Float.MAX_VALUE;
        } else {
            this.value = (T) (Integer) Integer.MAX_VALUE;
        }
    }
    public String print() {
        if (this.nan){
            return "NaN";
        } else if (this.noValue) {
            return "NOVALUE";
        } else if (type.equals("string")) {
            return String.valueOf(value);
        } else if (type.equals("float")) {
            return String.format("%.3f", value);
        }
        return String.valueOf(value);
    }
    public boolean equals(Item other) {
        return other.getType().equals(this.getType()) && other.getValue().equals(this.getValue());
    }
    public T getValue(){
        return value;
    }
    public boolean isNaN(){
        return nan;
    }
    public boolean isNoValue(){
        return noValue;
    }
    public Item copy(){
        return new Item<T>(this.value, this.type, this.noValue, this.nan);
    }
}
