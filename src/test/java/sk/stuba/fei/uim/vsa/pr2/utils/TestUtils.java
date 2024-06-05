package sk.stuba.fei.uim.vsa.pr2.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Slf4j
public class TestUtils {

    public static String capitalize(String str) {
        if (str.length() == 0) return "";
        if (str.length() == 1) return str.toUpperCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String camelCase(String str) {
        if (str.length() == 0) return "";
        if (str.length() == 1) return str.toLowerCase();
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static Connection getDBConnection(String url, String username, String password, String driver) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void runSQLStatement(Connection con, String sql, boolean silent) {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            if (!silent)
                log.error(ex.getMessage(), ex);
        }
    }

    public static List<String> tables = new ArrayList<>();

    private static final Set<String> IGNORE_TABLES = new HashSet<>(Arrays.asList("seq_gen_sequence", "sequence"));

    public static void clearDB(Connection dbConnection) {
        if (tables.isEmpty()) {
            try (Statement stmt = dbConnection.createStatement()) {
                ResultSet set = stmt.executeQuery("SELECT tablename FROM pg_tables WHERE schemaname = current_schema()");
                while (set.next()) {
                    String table = set.getString("tablename");
                    if (table != null && !table.isEmpty() && !IGNORE_TABLES.contains(table)) {
                        tables.add(table);
                    }
                }
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        //runSQLStatement(dbConnection, "SET FOREIGN_KEY_CHECKS=0", true);
        tables.forEach(table -> runSQLStatement(dbConnection, "TRUNCATE TABLE " + table + " CASCADE", false));
        //runSQLStatement(dbConnection, "SET FOREIGN_KEY_CHECKS=1", true);
    }

}
