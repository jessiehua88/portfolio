package db;

public class LessThan implements Comparator{

    @Override
    public boolean compare(Item x, String literal) {
        if(x.isNoValue()){
            return false;
        }
        if (x.isNaN()){
            return false;
        }
        if (x.getType().equals(Parse.literalType(literal))) {
            if (Parse.literalType(literal).equals("int")) {
                return (int) x.getValue() < Integer.valueOf(literal);
            } else if (Parse.literalType(literal).equals("float")) {
                return (float) x.getValue() < Float.valueOf(literal);
            } else if (Parse.literalType(literal).equals("string")) {
                if (x.getValue().toString().compareTo(String.valueOf(literal)) < 0) {
                    return true;
                }
                return false;
            }
        }
        else if (x.getType().equals("int") && Parse.literalType(literal).equals("float")) {
            return (int) x.getValue() < Float.valueOf(literal);
        } else if (x.getType().equals("float") && Parse.literalType(literal).equals("int")) {
            return (float) x.getValue() < Integer.valueOf(literal);
        }
        throw new RuntimeException("Comparing item and literal with different types.");
    }

    public boolean compare(Item x, Item y) {
        if(x.isNoValue() || y.isNoValue()){
            return false;
        }
        if (x.isNaN() && y.isNaN()){
            return false;
        }
        if (x.isNaN()){
            return false;
        }
        if (y.isNaN()){
            return true;
        }
        if (x.getType().equals(y.getType())){
            if(x.getType().equals("int")){
                return (int)x.getValue() < (int)y.getValue();
            }
            else if(x.getType().equals("float")){
                return (float)x.getValue() < (float)y.getValue();
            }
            else if(x.getType().equals("string")){
                if(x.getValue().toString().compareTo(String.valueOf(y.getValue().toString())) < 0){
                    return true;
                }
                return false;
            }
        } else if(x.getType().equals("int") && y.getType().equals("float")){
            return (int) x.getValue() < (float) y.getValue();
        } else if(x.getType().equals("float") && y.getType().equals("int")){
            return (float) x.getValue() < (int) y.getValue();
        }
        throw new RuntimeException("Comparing items with different types.");
    }
}