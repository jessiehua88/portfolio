package db;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class Database {
    private HashMap<String, Table> tables;
    public Database() {
        tables = new HashMap<>();
    }
    public String transact(String query) {
        return Parse.eval(query, this);
    }
    public String createSelectedTable(String name, ArrayList<Table> tabs,
                                      ArrayList<String> expressions, ArrayList<String> conds) {
        try {
            Table tableToAdd = selectTable(tabs, expressions, conds);
            return addTable(name, tableToAdd);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }
    private Table selectTable(ArrayList<Table> tabs, ArrayList<String> names,
                              ArrayList<String> conds) {
        return Table.select(tabs, names, conds);
    }
    public String selectPrint(ArrayList<Table> tabs, ArrayList<String> names,
                              ArrayList<String> conds) {
        try {
            return selectTable(tabs, names, conds).print();
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }
    private String addTable(String name, Table table) {
        tables.put(name, table);
        return "";
    }
    public String dropTable(String name) {
        return tables.remove(name) == null ? "ERROR: TABLE DOES NOT EXIST" : "";
    }
    public String loadTable(String fileName) {
        try {
            addTable(fileName, DatabaseHelperClass.generateTableFromTBL(fileName));
            return "";
        } catch (NoSuchElementException e) {
            return "ERROR: ROW TOO FEW ELEMENTS";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }
    public String store(String name) {
        try {
            PrintWriter writer = new PrintWriter(name + ".tbl", "UTF-8");
            writer.println(tables.get(name).print());
            writer.close();
            return "";
        } catch (IOException e) {
            // do something
            return "";
        } catch (NullPointerException e) {
            return "ERROR: TABLE DOES NOT EXIST";
        }
    }
    private String insertInto(String name, Row row) {
        tables.get(name).insert(row);
        return "";
    }
    public String insertInto(String name, String values) {
        try {
            return insertInto(name, DatabaseHelperClass.makeRow(values,
                    tables.get(name).getColumns()));
        } catch (NoSuchElementException e) {
            return "ERROR: TOO FEW ELEMENTS IN ROW";
        } catch (NumberFormatException  e) {
            return "ERROR: INCORRECT TYPE ASSIGNMENT";
        } catch (NullPointerException e) {
            return "ERROR: TABLE DOES NOT EXIST";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }
    public String print(String name) {
        try {
            return tables.get(name).print();
        } catch (NullPointerException e) {
            return "ERROR: TABLE DOES NOT EXIST";
        }
    }
    public String createTable(String name, String columns) {
        try {
            ArrayList<Column> columnList = DatabaseHelperClass.makeColumnList(columns);
            addTable(name, new Table(columnList));
            return "";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }
    public Table getTable(String name) {
        return tables.get(name);
    }
    public static void main(String[] args) {
        Database db = new Database();
        System.out.println(db.transact("create table test1 (x string, y float, z int)"));
        System.out.println(db.transact("insert into test1 values 'Alex', 2.000,0"));
        System.out.println(db.transact("select x from test1 where x != 'Alex LeTu'"));
        //System.out.println(db.transact("insert into test1 values 7.300, 0.000,5"));
        //System.out.println(db.transact("create table test2 as select
        // x/y as a, y/z as b from test1"));
        //System.out.println(db.transact("select a+b as c from test2"));
        //System.out.println(db.transact("create table test2 (b string, c string, d string)"));
        //System.out.println(db.transact("insert into test2 values 'b','c','d'"));
        //System.out.println(db.transact("create table test3 as select * from test1, test2"));
        //System.out.println(db.transact("print test3"));
    }
}
