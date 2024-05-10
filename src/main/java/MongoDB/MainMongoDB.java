package MongoDB;

import MongoDB.entities.*;
import MongoDB.entities.enums.ExperienciaStatus;
import MongoDB.mappers.DadosPortasMongoDBMapper;
import com.mongodb.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        experiencia.setId(String.valueOf(idExperiencia));

        setExperienciaEmProcessamento(connectToSQL, experiencia);
        CurrentExperiencia.getInstance().setEstadoExperiencia(ExperienciaStatus.EM_PROCESSAMENTO);

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

    public static void validaPrimeiroMovimentoValido(ConnectToSQL connectToSQL, Experiencia experiencia, DB mongoDb) throws SQLException {
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

                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

                var dataDadosMongo = mappedPortas.get(i).getHora();

                LocalDateTime dataMongo = LocalDateTime.parse(dataDadosMongo, formatter1);
                LocalDateTime dataExperiencia = LocalDateTime.parse(experiencia.getDataHora(), formatter1);

                if ((salaOrigemValue.equals("1") && (salaDestinoValue.equals(String.valueOf(salaDestino))
                        && dataMongo.compareTo(dataExperiencia) > 0))) {
                    System.out.println("Sala origem: " + salaOrigemValue + " Sala destino: " + salaDestinoValue);
                    experiencia.setDataHora(dataDadosMongo);

                    // faz set da Experiencia, caso encontre o dado válido
                    CurrentExperiencia.getInstance().setExperiencia(experiencia);

                    setExperienciaEmCurso(connectToSQL, experiencia);

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
    }

    public static void setExperienciaEmProcessamento(ConnectToSQL connectToSQL, Experiencia experiencia) throws SQLException {
        String testeCallSP = "{CALL setEmProcessamento (?)}";
        CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(testeCallSP);
        cs.setInt(1, Integer.valueOf(experiencia.getId()));
        cs.execute();
        System.out.println("Experiencia " + Integer.valueOf(experiencia.getId()) + " em processamento.");
    }

    public static void setExperienciaEmCurso(ConnectToSQL connectToSQL, Experiencia experiencia) throws SQLException {
        String testeCallSP = "{CALL Set_IniciarExperiencia (?)}";
        CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(testeCallSP);
        cs.setInt(1, Integer.valueOf(experiencia.getId()));
        cs.execute();
        System.out.println("Experiencia " + Integer.valueOf(experiencia.getId()) + " em curso.");
    }

    public static void validaIfExperienciaTerminou(ConnectToSQL connectToSQL, Experiencia experiencia) throws SQLException, InterruptedException {
        int estadoExperiencia = 0;
        int flag = 0;
        while(flag == 0) {
            String testeCallSP = "{CALL Get_EstadoExperiencia (?,?)}";
            CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(testeCallSP);
            cs.setInt(1, Integer.valueOf(experiencia.getId()));
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();

            estadoExperiencia = cs.getInt(2);


            if(estadoExperiencia == 5){
                System.out.println("Experiencia " + Integer.valueOf(experiencia.getId()) + " Terminada.");
                CurrentExperiencia.getInstance().setEstadoExperiencia(ExperienciaStatus.TERMINADA);
                //IniciarExperiencia(connectToSQL);
                flag = 1;
                break;
            }
            Thread.sleep(2000);
        }
    }

    public static void dropMongo(DB mongoDb){
        mongoDb.getCollection("Sensor_Porta").drop();
        mongoDb.getCollection("Sensor_Temperatura").drop();
        System.out.println("Dados Apagados no Mongo");
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
        validaPrimeiroMovimentoValido(connectToSQL, experiencia, mongoDb);
        System.out.println("Hora final: " + experiencia.getDataHora());


//        dropMongo(mongoDb);



//        //****  Exemplo de como ir buscar informação às tabelas e mapea-las ****
//        var result = s.executeQuery("select * from experiencia;");
//        var experiencias = ExperienciaMapper.mapList(result);
//        for (Experiencia experiencia : experiencias) {
//            System.out.println("Experiencia " + "Data_Hora: " + experiencia.getDataHora() + " Id:" + experiencia.getId());
//        }


        //Neste momento é preciso lançar as Threads
        //Ao mesmo tempo é preciso chamar validaIfExperienciaTerminou(connectToSQL, experiencia)
        //Esta função vai validar se a experiencia passou para o estado "Terminada"

//        validaIfExperienciaTerminou(connectToSQL,experiencia);
       // if(CurrentExperiencia.getInstance().isEstado(ExperienciaStatus.TERMINADA)){

//        }
        var fetchTempsMongo = new ProcessarTemperatura(mongoDb);
        var fetchDoorsMongo = new ProcessarPortas(mongoDb);
        var threadDealWithTemperatura = new TratarDadosTemperatura(connectToSQL.getConnectionSQL(), mongoDb);
        var threadDealWithPortas = new TratarDadosPortas(connectToSQL.getConnectionSQL(), mongoDb);
        var threadFetchTempsToSql = new InsertMedicaoTemperaturaMysql(connectToSQL.getConnectionSQL());
        var threadFetchPassagensToSql = new InsertMedicaoPassagem(connectToSQL.getConnectionSQL());

        fetchTempsMongo.start();
        fetchDoorsMongo.start();
        threadDealWithTemperatura.start();
        threadDealWithPortas.start();
        threadFetchTempsToSql.start();
        threadFetchPassagensToSql.start();

        fetchTempsMongo.join();
        fetchDoorsMongo.join();
        threadDealWithTemperatura.join();
        threadDealWithPortas.join();
        threadFetchPassagensToSql.join();
        threadFetchTempsToSql.join();

        //Esta linha de codigo não pode estar aqui.
        //Tem que estar no Java que corre no SQL. Depois de Pipa acabar
        //SQLBackup backup = new SQLBackup(connectToSQL);


    }


}
