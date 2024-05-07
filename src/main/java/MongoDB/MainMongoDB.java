package MongoDB;

import MongoDB.entities.*;
import MongoDB.entities.enums.ExperienciaStatus;
import MongoDB.mappers.CorredorMapper;
import MongoDB.mappers.DadosPortasMongoDBMapper;
import MongoDB.mappers.ExperienciaMapper;
import com.mongodb.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Objects;
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
            System.out.println("Não foi encontrada nenhuma experiencia");
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

            }
            LocalDateTime currentDate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            experiencia.setDataHora(currentDate.format(formatter));
            experiencia.setId(String.valueOf(idExperiencia));
            experiencia.setCorredores(corredores);

        }
        return experiencia;
    }

    public static void validaPrimeiroMovimentoValido(Experiencia experiencia, DB mongoDb){
        //Sala de origem inicial é sempre 1.
        //Encontra salaDestino correta
        int salaDestino=0;
        int aux;
        System.out.println(experiencia.getCorredores().length);
        for(int i = 0; i < experiencia.getCorredores().length; i++){
            if(experiencia.getCorredores()[i].getSalaOrigem().equals("1")) {
                salaDestino = Integer.parseInt(experiencia.getCorredores()[i].getSalaDestinoComOrigem(String.valueOf(1)));
            }
        }

        //Depois ir encontrar no mongo, qual é o primeiro movimento valido

        BasicDBObject query = new BasicDBObject("catch", new BasicDBObject("$exists", false));
        var collection = mongoDb.getCollection("Sensor_Porta");

        int flag = 0;
        while(flag == 0) {
            var iterator = collection.find(query).iterator();
            var mappedPortas = DadosPortasMongoDBMapper.mapList(iterator);
            for (int i = 0; i < mappedPortas.size(); i++) {
                String salaOrigemValue = String.valueOf(mappedPortas.get(i).getSalaOrigem());
                String salaDestinoValue = String.valueOf(mappedPortas.get(i).getSalaDestino());

                //int compare = LocalDateTime.parse(experiencia.getDataHora()).compareTo(LocalDateTime.parse(mappedPortas.get(i).getHora()));
               /* DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS");
                String horasMongo = mappedPortas.get(i).getHora();
                String horasExperiencia = experiencia.getDataHora();
                System.out.println(mappedPortas.get(i).getHora());
                LocalDateTime mongoTime = LocalDateTime.parse(horasMongo, formatter1);
                LocalDateTime experienciaTime = LocalDateTime.parse(horasExperiencia, formatter2);
                int compare = mongoTime.compareTo(experienciaTime);*/

                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

                var dataDadosMongo = mappedPortas.get(i).getHora();

                LocalDateTime dataMongo = LocalDateTime.parse(dataDadosMongo, formatter1);
                LocalDateTime dataExperiencia = LocalDateTime.parse(experiencia.getDataHora(), formatter1);

                if ((salaOrigemValue.equals("1") && (salaDestinoValue.equals(String.valueOf(salaDestino))
                        && dataMongo.compareTo(dataExperiencia) > 0))) {
                    System.out.println("Sala origem: " + salaOrigemValue + " Sala destino: " + salaDestinoValue);
                    experiencia.setDataHora(mappedPortas.get(i).getHora());
                    // faz set da Experiencia, caso encontre o dado válido
                    CurrentExperiencia.getInstance().setExperiencia(experiencia);

                    //Depois do SP que passa o estado da experiencia para em execução, faz set do estado da experiencia
                    // no java, para execucao
                    CurrentExperiencia.getInstance().setEstadoExperiencia(ExperienciaStatus.EM_CURSO);

                    flag = 1;
                    break;
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


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


        //Experiencia criada com id, data e array de Corredores (posições validas)
        Experiencia experiencia = getCorredoresCurrentExperiencia(connectToSQL, id);
        System.out.println("Hora inicial: " + experiencia.getDataHora());
        validaPrimeiroMovimentoValido( experiencia, mongoDb);
        System.out.println("Hora final: " + experiencia.getDataHora());


        /*for(int i = 0; i < experiencia.getCorredores().length; i++){
            System.out.println(experiencia.getCorredores()[i].getSalaOrigem());
            System.out.println(experiencia.getCorredores()[i].getSalaDestino());
        }*/




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
