package MongoDB;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLBackup {

    //Caminho estatico, é preciso alterar
    String backupDirectory = "C:\\Users\\Alexsilva\\Desktop";



    public SQLBackup(ConnectToSQL connectToSQL){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());


        String backupFileName = String.format("%s_%s.sql", connectToSQL.getDatabaseName(), timeStamp);
        //É preciso mapear corretamente o caminho para mysqldump.exe. Pode variar de PC para PC
        String command = String.format("C:\\xampp\\mysql\\bin\\mysqldump.exe -u %s -p%s %s --result-file=%s/%s",
                connectToSQL.getUserName(), connectToSQL.getPassword(), connectToSQL.getDatabaseName(), backupDirectory, backupFileName);
        //Sysout para validar mensagem de erro. Se der erro a criar o backup, descomentar linha
        //System.out.println(command);
        try {
            Process process = Runtime.getRuntime().exec(command);

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Database backup successful.");
            } else {
                System.err.println("Error backing up database.");
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing backup command: " + e.getMessage());
        }
    }

    }

