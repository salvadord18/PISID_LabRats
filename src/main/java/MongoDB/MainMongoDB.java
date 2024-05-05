package MongoDB;

import MongoDB.entities.Corredor;
import MongoDB.entities.Experiencia;
import MongoDB.mappers.CorredorMapper;
import MongoDB.mappers.ExperienciaMapper;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;

public class MainMongoDB {

    ConnectToMongo connectMongo;
    MongoClient myConnectionToMongo;
    ConnectToSQL connectToSQL;
    static Statement s;

    public static int IniciarExperiencia(ConnectToSQL connectToSQL) throws InterruptedException, SQLException {
        while (true) {
            //Não esquecer de passar a experiencia de a aguardar para em processamento!
            String testeCallSP = "{CALL Get_ProximaExperiencia(?)}";
            CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(testeCallSP);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            int resultado = cs.getInt(1);
            if (resultado != -1){
                return resultado;
            }
            System.out.println("Não ha threads a aguardar");
            Thread.sleep(5000);
        }
    }

    public static Experiencia getCorredoresCurrentExperiencia(ConnectToSQL connectToSQL, int idExperiencia) throws SQLException {

        String procedureCall = "{CALL GetCorredores(?)}";
        CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(procedureCall);
        cs.setInt(1, idExperiencia);
        ResultSet resultado =  cs.executeQuery();
        Experiencia experiencia = new Experiencia();

        while(resultado.next()) {
            JSONArray jsonArray = new JSONArray(resultado.getString(1));
            Corredor[] corredores = new Corredor[jsonArray.length()];
            int i = 0;

            for (; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Corredor corredor = new Corredor();
                corredor.setSalaOrigem(jsonObject.getString("Sala_Origem_ID"));
                corredor.setSalaDestino(jsonObject.getString("Sala_Destino_ID"));
                corredores[i] = corredor;

                //System.out.println(resultado.getString(1));
                //tratar json e popular corredores

            }
            LocalDate currentDate = LocalDate.now();
            String currentDateString = currentDate.toString();
            experiencia.setDataHora(currentDateString);
            experiencia.setId(String.valueOf(idExperiencia));
            experiencia.setCorredores(corredores);

        }
        return experiencia;
    }

    public void validaPrimeiroMovimentoValido(Experiencia experiencia, DB mongoDb){
        //Sala de origem inicial é sempre 1.
        //Encontrar a sala de destino
        //Depois ir encontrar no mongo, qual é o primeiro movimento valido
        //Alterar a data da experiencia para data de primeiro movimento valido
        //passar experiencia para em execução
    }

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        //Esta parte foi toda para dentro do ConnectToMongo

        ConnectToMongo connectMongo  = new ConnectToMongo();
        MongoClient myConnectionToMongo = connectMongo.getConnectionMongo();
        ConnectToSQL connectToSQL = new ConnectToSQL();
        var mongoDb = connectMongo.getDataBase();

        var s = connectToSQL.getConnectionSQL().createStatement();

        //Recebe id da experiencia que está a aguardar à mais tempo
        int id = IniciarExperiencia(connectToSQL);
        System.out.println(id);

        //Experiencia criada com id, data e array de Corredores (posições validas)
        Experiencia experiencia = getCorredoresCurrentExperiencia(connectToSQL, id);

        for(int i = 0; i < experiencia.getCorredores().length; i++){
            System.out.println(experiencia.getCorredores()[i].getSalaOrigem());
            System.out.println(experiencia.getCorredores()[i].getSalaDestino());
        }




//        //****  Exemplo de como ir buscar informação às tabelas e mapea-las ****
//        var result = s.executeQuery("select * from experiencia;");
//        var experiencias = ExperienciaMapper.mapList(result);
//        for (Experiencia experiencia : experiencias) {
//            System.out.println("Experiencia " + "Data_Hora: " + experiencia.getDataHora() + " Id:" + experiencia.getId());
//        }




        var fetchTempsMongo = new ProcessarTemperatura(mongoDb);
        var fetchDoorsMongo = new ProcessarPortas(mongoDb);
        var threadFetchToSql = new EnviarDadosMysql(connectToSQL.getConnectionSQL());
        var threadDealWithData = new TratarDados(connectToSQL.getConnectionSQL(), mongoDb);


        fetchTempsMongo.start();
        fetchDoorsMongo.start();
        threadDealWithData.start();
        threadFetchToSql.start();



        fetchTempsMongo.join();
        fetchDoorsMongo.join();
        threadDealWithData.join();
        threadFetchToSql.join();


    }


}
