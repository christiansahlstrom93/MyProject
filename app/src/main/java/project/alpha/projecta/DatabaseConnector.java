package project.alpha.projecta;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by Christian on 2015-06-01.
 */
public class DatabaseConnector {

    private Statement state;
    private Connection conn;


    public boolean connectingDatabase() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
        }

        try {
            setConn(DriverManager
                    .getConnection("jdbc:mysql://109.74.1.55:3306/CashnetDB"
                            + "?user=" + "root" + "&password=" + "r6vzpvjn"));
            return true;
        } catch (Exception ex) {
            Log.e("rolle","Vafan2 " + ex);
            return false;
        }
    }

    public Statement getStatement() {
        return this.state;
    }

    public void setStatement(Statement state) {
        this.state = state;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
