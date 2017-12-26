package db;

import edu.princeton.cs.algs4.In;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class DatabaseHelperClass {
    public static ArrayList<Column> makeColumnList(String columnData){
        ArrayList<Column> colObjs = new ArrayList<>();
        StringTokenizer splitter = new StringTokenizer(columnData, " ,");
        while(splitter.hasMoreTokens()){
            String name = splitter.nextToken();
            String type = splitter.nextToken();
            if (!(type.equals("string") || type.equals("int") || type.equals("float"))){
                throw new RuntimeException("ERROR: Invalid column type.");
            }
            colObjs.add(new Column(name, type));
        }
        return colObjs;
    }
    public static Item makeString(String value, boolean nan){
        if (nan) {
            return new Item("", "string", nan);
        }
        else if (value.endsWith("'") && value.startsWith("'")) {
            return new Item(value, "string", nan);
        }
        throw new RuntimeException("ERROR: INCORRECT TYPE ASSIGNMENT STRING");
    }
    public static Item makeInt(String value, boolean nan) {
        if (nan) {
            return new Item(Integer.valueOf("0"), "int", nan);
        }
        else if (!value.contains(".")) {
            return new Item(Integer.parseInt(value), "int", nan);
        }
        throw new RuntimeException("ERROR: INCORRECT TYPE ASSIGNMENT");
    }
    public static Item makeFloat(String value, boolean nan){
        if (nan) {
            return new Item(Float.valueOf("0.0"), "float", nan);
        }
        return new Item<Float>(Float.parseFloat(value), "float", nan);
    }
    public static Row makeRow(String nextLine, ArrayList<Column> columns){
        Row row = new Row();
        StringTokenizer splitter = new StringTokenizer(nextLine, ",");

        for (int i = 0; i < columns.size(); i ++){
            String type = columns.get(i).getType();
            String value = splitter.nextToken();
            boolean nan = value.equals("NOVALUE");

            if (type.equals("string")){
                row.addItem(DatabaseHelperClass.makeString(value, nan));
            } else if (type.equals("float")){
                row.addItem(DatabaseHelperClass.makeFloat(value, nan));
            } else {
                row.addItem(DatabaseHelperClass.makeInt(value, nan));
            }
        }
        if (splitter.hasMoreTokens()){
            throw new RuntimeException("ERROR: ROW TOO MANY ELEMENTS");
        }
        return row;
    }
    public static void checkFileExist(String fileName){
        File f = new File(fileName + ".tbl");
        if(!f.exists() || f.isDirectory()) {
            throw new RuntimeException("ERROR: FILE DOES NOT EXIST");
        }
    }
    public static Table generateTableFromTBL(String fileName){
        DatabaseHelperClass.checkFileExist(fileName);

        In allData = new In(fileName + ".tbl");

        String columnData = allData.readLine();
        ArrayList<Column> cols = DatabaseHelperClass.makeColumnList(columnData);

        if(cols.size() == 0){
            throw new RuntimeException("ERROR: TABLE HAS NO COLUMNS");
        }

        Table table = new Table(cols);
        while (allData.hasNextLine()) {
            String nextRowData = allData.readLine();
            if (nextRowData.trim().equals("")){
                return table;
            }
            Row row = DatabaseHelperClass.makeRow(nextRowData, cols);
            table.insert(row);
        }
        return table;
    }
}
