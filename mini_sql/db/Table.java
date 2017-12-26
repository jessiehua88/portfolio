package db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class Table {
    private ArrayList<Column> columns;
    private ArrayList<Row> rows;
    public static final String[] comps = {">", "<", ">=", "<=", "==", "!="};
    public static final String[] ops = {"+", "-", "*", "/"};
    public static final ArrayList<String> operators = new ArrayList<>(Arrays.asList(ops));
    public static final ArrayList<String> comparators = new ArrayList<>(Arrays.asList(comps));

    public Table(ArrayList<Column> cols){
        columns = cols;
        rows = new ArrayList<>();
    }

    public Table(){
        columns = new ArrayList<>();
        rows = new ArrayList<>();
    }

    public void insert(Row row){
        rows.add(row);
    }

    public Row getRow(int index){
        return rows.get(index);
    }

    public Column getColumn(int index){
        return columns.get(index);
    }
    public int size(){
        return rows.size();
    }

    public ArrayList<Column> getColumns(){
        return columns;
    }

    public static Table select(ArrayList<Table> tables, ArrayList<String> expressions, ArrayList<String> conds){
        Table temp = Table.recurseJoin(tables);
        if (expressions.get(0).equals("*")){
            return temp.filter(conds);
        }
        temp = temp.selectExpressions(expressions);
        return temp.filter(conds);
    }
    private Table selectExpressions(ArrayList<String> expressions){
        Table selectedTable = new Table();
        selectedTable.addNrows(size());
        for (int j = 0; j < expressions.size(); j++){
            if (expressions.get(j).contains(" as ")){
                String operator = containsWhichArith(expressions.get(j));
                StringTokenizer expressionSplitter = new StringTokenizer(expressions.get(j), operator);
                String item1 = expressionSplitter.nextToken();
                String intermediate = expressionSplitter.nextToken();
                StringTokenizer expressionSplitter2 = new StringTokenizer(intermediate, " ");
                String item2 = expressionSplitter2.nextToken();
                expressionSplitter2.nextToken();
                String item3 = expressionSplitter2.nextToken();
                Operation operation = getOperation(operator);
                Table temp = combineColumns(item3.trim(), item1.trim(), item2.trim(), operation);
                selectedTable.addColumn(0, temp);

            } else {
                for (int i = 0; i < columns.size(); i++) {
                    if (expressions.get(j).equals(columns.get(i).getName())) {
                        selectedTable.addColumn(i, this);
                    }
                }
            }
        }
        return selectedTable;
    }
    private Table combineColumns(String name, String col1, String col2, Operation operation){
        int index1 = getIndex(col1);
        int index2 = getIndex(col2);
        Column column1 = columns.get(index1);
        Column column2 = columns.get(index2);
        Column colToAdd = new Column(name, column1.getType());
        if (column1.getType().equals("float") || column2.getType().equals("float")){
            colToAdd = new Column(name, "float");
        }
            ArrayList<Column> listed = new ArrayList<>();
            listed.add(colToAdd);
            Table table = new Table(listed);
            table.addNrows(this.size());
            for(int i = 0; i < table.size(); i ++){
                table.rows.get(i).addItem((operation.apply(rows.get(i).getItem(index1), rows.get(i).getItem(index2))));
            }
            return table;
    }
    private String containsWhichArith(String expression){
        if(expression.contains("+")){
            return "+";
        } else if (expression.contains("-")){
            return "-";
        } else if (expression.contains("*")){
            return "*";
        } else {
            return "/";
        }
    }
    private Operation getOperation(String op){
        switch(op) {
            case "+": return new Add();
            case "-": return new Subtract();
            case "*": return new Multiply();
            case "/": return new Divide();
        }
        throw new RuntimeException("ERROR: INVALID OPERATOR");
    }
    private boolean containsArith(ArrayList<String> expressions){
        for(String s : expressions){
            if (s.contains(" as ")){
                return true;
            }
        }
        return false;
    }
    /*Identify literal and column name to filter*/
    public Table filter(ArrayList<String> conds){
        if (conds.size() % 3 != 0) {
            throw new RuntimeException("ERROR: Malformed where clause");
        }
        for (int i = 0; i < conds.size() - 2; i += 3){
            String item1 = conds.get(i);
            String item2 = conds.get(i + 1);
            String item3 = conds.get(i + 2);

            ArrayList<String> conditionalStatement = new ArrayList<>();
            conditionalStatement.add(item1);
            conditionalStatement.add(item2);
            conditionalStatement.add(item3);

            this.conditionalFormatChecker(conditionalStatement);
            Comparator comparator = getComparator(item2);

            boolean binary = (getColNames(columns).contains(conditionalStatement.get(2)));

            int col1index = getIndex(item1);
            int col2index = getIndex(item3);

            for (int j = rows.size() - 1; j >= 0; j --){
                if (binary){
                    if(!comparator.compare(this.getRow(j).getItem(col1index), this.getRow(j).getItem(col2index))){
                        rows.remove(j);
                    }
                }
                else{
                    if(!comparator.compare(this.getRow(j).getItem(col1index), item3)){
                        rows.remove(j);
                    }
                }
            }
        }
        return this;
    }
    private Comparator getComparator(String comp){
        switch (comp) {
            case "<=" : return new LessThanEqualTo();
            case ">" : return new GreaterThan();
            case "<" : return new LessThan();
            case ">=" : return new GreaterThanEqualTo();
            case "==" : return new EqualTo();
            case "!=" : return new NotEqualTo();
        }
        throw new RuntimeException("ERROR: INVALID COMPARATOR");
    }
    private void conditionalFormatChecker(ArrayList<String> items){
        if(!(getColNames(columns).contains(items.get(0)) && Table.comparators.contains(items.get(1)))){
            throw new RuntimeException("ERROR: MALFORMED WHERE CLAUSE");
        }
    }
    private void addNrows(int n){
        for (int i = 0; i < n; i ++){
            this.insert(new Row());
        }
    }
    public void deleteColumn(int index){
        columns.remove(index);
        for (Row row : rows) {
            row.delete(index);
        }
    }
    public void addColumn(int index, Table table) {
        this.columns.add(table.getColumn(index));
        for(int i = 0; i < this.rows.size(); i++){
            this.rows.get(i).addItem(table.getRow(i).getItems().get(index));
        }
    }
    public static Table recurseJoin(ArrayList<Table> tables) {
        if(tables.size() == 1){
            return tables.get(0);
        }
        else if(tables.size() == 2){
            return Table.join(tables.get(0), tables.get(1));
        }
        else{
            Table one = tables.get(0);
            Table two = tables.get(1);
            tables.remove(0);
            tables.remove(0);
            Table newTable = Table.join(one, two);
            tables.add(0, newTable);
            return recurseJoin(tables);
        }
    }
    public static Table join(Table T1, Table T2) {
        ArrayList<Column> columns = new ArrayList<>(T1.columns.size()+T2.columns.size());
        for (int i = 0; i < T1.columns.size(); i++){
            columns.add(T1.getColumn(i));
        }

        for (int i = 0; i < T2.columns.size(); i++){
            columns.add(T2.getColumn(i));
        }

        Table joinedTable = new Table(columns);
        for (int i = 0; i < T1.size(); i++) {
            for (int j = 0; j < T2.size(); j++) {
                Row joinedRow = T1.getRow(i).concatRow(T2.getRow(j));
                joinedTable.insert(joinedRow);
            }
        }

        ArrayList<Column> repeats = T1.findDuplicate(columns);

        if(repeats == null) {
            return joinedTable;
        }
        else {
            for(int i = repeats.size() - 1; i >= 0 ; i --) {
                ArrayList<Integer> repeatedIndex = T1.findIndexDuplicate(repeats.get(i), columns);
                int index1 = repeatedIndex.get(0);
                int index2 = repeatedIndex.get(1);
                for(int j = joinedTable.rows.size()-1 ; 0 <= j; j--) {
                    Item one = joinedTable.getRow(j).getItems().get(index1);
                    Item two = joinedTable.getRow(j).getItems().get(index2);
                    if(!(one.equals(two))) {
                        joinedTable.rows.remove(j);
                    }
                    else {
                        joinedTable.getRow(j).delete(index2);
                    }
                }
                joinedTable.columns.remove(index2);
            }
            ArrayList<Table> input = new ArrayList<>();
            input.add(joinedTable);
            return select(input, Table.orderColumns(repeats, T1, T2), new ArrayList<String>());
        }
    }
    private static ArrayList<String> orderColumns(ArrayList<Column> shared, Table T1, Table T2){
        ArrayList<String> ordered = new ArrayList<>();
        ordered.addAll(Table.getColNames(shared));

        ArrayList<String> colT1 = T1.removeDupes(ordered);
        ArrayList<String> colT2 = T2.removeDupes(ordered);

        ordered.addAll(colT1);
        ordered.addAll(colT2);

        return ordered;
    }
    private ArrayList<String> removeDupes(ArrayList<String> ordered){
        ArrayList<String> removed = Table.getColNames(columns);
        for(int i = 0; i < ordered.size(); i ++){
            if (removed.contains(ordered.get(i))){
                removed.remove(ordered.get(i));
            }
        }
        return removed;
    }
    //finds all columns that are repeated in col
    public static ArrayList<Column> findDuplicate (ArrayList<Column> col) {
        ArrayList<Column> repeats = new ArrayList<>();
        for (int i = 0; i < col.size()-1; i++) {
            for (int j = i+1; j < col.size(); j++) {
                if( (col.get(i).getName().equals(col.get(j).getName())) && (i != j) ) {
                    repeats.add(col.get(i));
                }
            }
        }
        if(repeats.isEmpty()){
            return null;
        }
        return repeats;
    }
    //returns pair of indices where col is in columns
    public static ArrayList<Integer> findIndexDuplicate (Column col, ArrayList<Column> columns) {
        ArrayList<Integer> repeatedIndex = new ArrayList<>();
        for(int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(col.getName())){
                repeatedIndex.add(i);
            }
        }
        return repeatedIndex;
    }
    public int getIndex(Column col){
        for(int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(col.getName())){
                return i;
            }
        }
        return 0;
    }
    public int getIndex(String col){
        for(int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(col)){
                return i;
            }
        }
        return 0;
    }
    private String printCol(){
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < columns.size(); i++) {
            joiner.add(columns.get(i).print());
        }
        return joiner.toString() + "\n";
    }
    public String print(){
        String printable = "";
        printable += printCol();
        for (Row x: rows){
            printable += x.print();
        }
        return printable;
    }
    private static ArrayList<String> getColNames(ArrayList<Column> cols){
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < cols.size(); i ++){
            names.add(cols.get(i).getName());
        }
        return names;
    }
    public Table copy(){
        ArrayList<Column> columnList = new ArrayList<>();
        columnList.addAll(this.columns);
        Table copiedTable = new Table(columnList);
        for (Row row : this.rows){
            copiedTable.insert(row.copyRow());
        }
        return copiedTable;
    }
}
