package db;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.StringJoiner;

public class Parse {
    // Various common constructs, simplifies parsing.
    private static final String REST  = "\\s*(.*)\\s*",
                                COMMA = "\\s*,\\s*",
                                AND   = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
                                 LOAD_CMD   = Pattern.compile("load " + REST),
                                 STORE_CMD  = Pattern.compile("store " + REST),
                                 DROP_CMD   = Pattern.compile("drop table " + REST),
                                 INSERT_CMD = Pattern.compile("insert into " + REST),
                                 PRINT_CMD  = Pattern.compile("print " + REST),
                                 SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+\\s+\\S+\\s*" +
                                               "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
                                 SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                                               "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                                               "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+" +
                                               "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
                                 CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                                                   SELECT_CLS.pattern()),
                                 INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                                               "\\s*(?:,\\s*.+?\\s*)*)");

    public static String eval(String query, Database db) {
        query = query.trim();
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
             return createTable(m.group(1), db);
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
             return loadTable(m.group(1), db);
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
             return storeTable(m.group(1), db);
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
             return dropTable(m.group(1), db);
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
             return insertRow(m.group(1), db);
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
             return printTable(m.group(1), db);
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
             return select(m.group(1), db);
        } else {
            return "ERROR: Malformed query.";
        }
    }

    private static String createTable(String expr, Database db) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA), db);
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4), db);
        } else {
            return "ERROR: Malformed create.";
        }
    }
    private static String createNewTable(String name, String[] cols, Database db) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 0; i < cols.length; i++) {
            joiner.add(cols[i].trim());
        }
        return db.createTable(name.trim(), joiner.toString());
    }
    private static String createSelectedTable(String name, String exprs, String tables, String conds, Database db) {
        return select(name, exprs, tables, conds, db, true);
    }
    private static String loadTable(String name, Database db) {
        return db.loadTable(name.trim());
    }
    private static String storeTable(String name, Database db) {
        return db.store(name.trim());
    }
    private static String dropTable(String name, Database db) {
        return db.dropTable(name.trim());
    }
    private static String insertRow(String expr, Database db) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            return "ERROR: Malformed Insert.";
        }
        return db.insertInto(m.group(1).trim(), m.group(2).trim());
    }
    private static String printTable(String name, Database db) {
        return db.print(name.trim());
    }
    private static String select(String expr, Database db) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            return "ERROR: Malformed Select.";
        }
        return select("", m.group(1), m.group(2), m.group(3), db, false);
    }
    private static String select(String name, String exprs, String tables, String conds, Database db, boolean create){
        try {
            StringTokenizer exprsSplitter = new StringTokenizer(exprs, ",");
            StringTokenizer tableSplitter = new StringTokenizer(tables, ",");
            ArrayList<Table> tableList = new ArrayList<>();
            ArrayList<String> expressions = new ArrayList<>();
            ArrayList<String> conditionals = new ArrayList<>();

            if (conds != null){
                StringTokenizer condsSplitter = new StringTokenizer(conds, ", ");
                while (condsSplitter.hasMoreTokens()) {
                    String next = condsSplitter.nextToken().trim();
                    if(next.startsWith("'")) {
                        while (!next.endsWith("'")) {
                            next += " ";
                            next += condsSplitter.nextToken().trim();
                        }
                    }
                    if(!next.equals("and")) {
                        conditionals.add(next);
                    }
                }
            }
            while (exprsSplitter.hasMoreTokens()) {
                expressions.add(exprsSplitter.nextToken().trim());
            }
            while (tableSplitter.hasMoreTokens()) {
                tableList.add(db.getTable(tableSplitter.nextToken()).copy());
            }
            if (create)
                return db.createSelectedTable(name, tableList, expressions, conditionals);
            return db.selectPrint(tableList, expressions, conditionals);
        } catch (NullPointerException e) {
            return "ERROR: Malformed Select";
        }
    }

    public static String literalType(String literal){
        if (literal.startsWith("'") && literal.endsWith("'")){
            return "string";
        }
        if (literal.contains(".")){
            return "float";
        }
        return "int";
    }

}

