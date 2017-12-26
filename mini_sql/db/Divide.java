package db;

public class Divide implements Operation{
    public Item apply(Item a, Item b){
        boolean isNoValue = false;
        if (a.isNoValue() && b.isNoValue()){
            isNoValue = true;
        }
        if (a.isNaN()){
            Item newItem = new Item("NaN", a.getType(), isNoValue);
            newItem.changeToNaN();
            return newItem;
        }
        else if(b.isNaN() || (b.getType().equals("int") && (int)b.getValue() == 0) || (b.getType().equals("float") && (float) b.getValue() == 0.0)) {
            Item newItem = new Item("NaN", "int", isNoValue);
            if (b.getType().equals("float") || a.getType().equals("int")){
                newItem = new Item("NaN", "float", isNoValue);
            }
            newItem.changeToNaN();
            return newItem;
        }
        else if(a.getType().equals(b.getType())){
            if(a.getType().equals("int")){
                int newValue = (int)a.getValue() / (int)b.getValue();
                return new Item(newValue, "int", isNoValue);
            }
            else if(a.getType().equals("float")){
                float newValue = (float)a.getValue() / (float)b.getValue();
                return new Item(newValue, "float", isNoValue);
            }
            throw new RuntimeException("ERROR: dividing wrong type.");
        }
        else if(a.getType().equals("float") && b.getType().equals("int")){
            float newValue = (float)a.getValue() / (int)b.getValue();
            return new Item(newValue, "float", isNoValue);
        }
        else if(a.getType().equals("int") && b.getType().equals("float")){
            float newValue = (int)a.getValue() / (float)b.getValue();
            return new Item(newValue, "float", isNoValue);
        }
        throw new RuntimeException("ERROR: dividing wrong type.");
    }

    public Item apply(Item a, String literal){

        if (a.isNaN()|| (Parse.literalType(literal).equals("int") && Integer.valueOf(literal) == 0) || (Parse.literalType(literal).equals("float") && Float.valueOf(literal) == 0)){
            Item newItem = new Item("NaN", a.getType(), false);
            newItem.changeToNaN();
            return newItem;
        }
        else if(a.getType().equals(Parse.literalType(literal))){
            if(a.getType().equals("int")){
                int newValue = (int)a.getValue() / Integer.parseInt(literal);
                return new Item(newValue, "int", false);
            }
            else if(a.getType().equals("float")){
                float newValue = (float)a.getValue() /  Float.parseFloat(literal);
                return new Item(newValue, "float", false);
            }
            throw new RuntimeException("ERROR: dividing wrong type.");
        }
        else if(a.getType().equals("float") && Parse.literalType(literal).equals("int")){
            float newValue = (float)a.getValue() / Integer.getInteger(literal);
            return new Item(newValue, "float", false);
        }
        else if(a.getType().equals("int") && Parse.literalType(literal).equals("float")){
            float newValue = (int)a.getValue() / Float.parseFloat(literal);
            return new Item(newValue, "float", false);
        }
        throw new RuntimeException("ERROR: dividing wrong type.");
    }
}
