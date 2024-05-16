package BackupSQL;

import MongoDB.ConnectToSQL;

import java.io.IOException;
import java.sql.SQLException;

public class MainSQLBackup{



    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        MongoDB.ConnectToSQL connectToSQL = new ConnectToSQL();
        MongoDB.SQLBackup backup = new MongoDB.SQLBackup(connectToSQL);
    }
}
