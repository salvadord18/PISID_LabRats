package MongoDB;

import MongoDB.entities.*;
import MongoDB.entities.enums.ExperienciaStatus;
import MongoDB.mappers.DadosPortasMongoDBMapper;
import com.mongodb.*;
import com.mysql.cj.x.protobuf.MysqlxSession;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainMongoDB {

    ConnectToMongo connectMongo;
    MongoClient myConnectionToMongo;
    ConnectToSQL connectToSQL;
    static Statement s;
    static List<Thread> threadsList = new ArrayList<>();

    public static int IniciarExperiencia(ConnectToSQL connectToSQL) throws InterruptedException, SQLException {
        while (true) {
            //Não esquecer de passar a experiencia de a aguardar para em processamento!
            String testeCallSP = "{CALL Get_ProximaExperiencia(?)}";
            CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(testeCallSP);
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();

            int resultado = cs.getInt(1);
            if (resultado != -1) {
                return resultado;
            }
            System.out.println("Não foi encontrada nenhuma experiencia");
            Thread.sleep(5000);
        }
    }

    public static Experiencia getCorredoresCurrentExperiencia(ConnectToSQL connectToSQL, int idExperiencia) throws SQLException, InterruptedException {

//        String procedureCall = "{CALL GetCorredores(?)}";
//        CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(procedureCall);
//        cs.setInt(1, idExperiencia);
//        ResultSet resultado = cs.executeQuery();
//
//        Experiencia experiencia = new Experiencia();
//        experiencia.setId(String.valueOf(idExperiencia));
//
//        setExperienciaEmProcessamento(connectToSQL, experiencia);
//        CurrentExperiencia.getInstance().setEstadoExperiencia(ExperienciaStatus.EM_PROCESSAMENTO);
//
//        while (resultado.next()) {
//            JSONArray jsonArray = new JSONArray(resultado.getString(1));
//            Corredor[] corredores = new Corredor[jsonArray.length()];
//            int i = 0;
//
//            for (; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                Corredor corredor = new Corredor();
//                corredor.setSalaOrigem(jsonObject.getString("Sala_Origem_ID"));
//                corredor.setSalaDestino(jsonObject.getString("Sala_Destino_ID"));
//                corredores[i] = corredor;
//
//            }
//            LocalDateTime currentDate = LocalDateTime.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
//
//            experiencia.setDataHora(currentDate.format(formatter));
//            experiencia.setId(String.valueOf(idExperiencia));
//            experiencia.setCorredores(corredores);
//
//        }
//        return experiencia;
        String procedureCall = "{CALL GetCorredores(?)}";
        Experiencia experiencia = new Experiencia();
        experiencia.setId(String.valueOf(idExperiencia));

        setExperienciaEmProcessamento(connectToSQL, experiencia);
        CurrentExperiencia.getInstance().setEstadoExperiencia(ExperienciaStatus.EM_PROCESSAMENTO);

        boolean hasResult = false;
        int retryDelay = 5000; // tempo de espera entre as tentativas (em milissegundos)

        while (!hasResult) {
            try (CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(procedureCall)) {
                cs.setInt(1, idExperiencia);
                try (ResultSet resultado = cs.executeQuery()) {
                    if (resultado.next()) {
                        JSONArray jsonArray = new JSONArray(resultado.getString(1));
                        if (jsonArray.length() > 1) {
                            Corredor[] corredores = new Corredor[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
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

                            hasResult = true; // Sinaliza que temos um resultado válido
                        }
                    }
                }
            } catch (SQLException e) {
                // Trate a exceção conforme necessário, talvez registre e rethrow ou apenas rethrow
                System.out.println("Ainda não há corredores");
            }
            if (!hasResult) {
                Thread.sleep(retryDelay); // Aguarda antes da próxima tentativa
            }
        }
        return experiencia;
    }

    public static void validaPrimeiroMovimentoValido(ConnectToSQL connectToSQL, Experiencia experiencia, DB mongoDb) throws SQLException {
        //Sala de origem inicial é sempre 1.
        //Encontra salaDestino correta
        int salaDestino = 0;
        int aux;
        System.out.println(experiencia.getCorredores().length);
        for (int i = 0; i < experiencia.getCorredores().length; i++) {
            if (experiencia.getCorredores()[i].getSalaOrigem().equals("1")) {
                salaDestino = Integer.parseInt(experiencia.getCorredores()[i].getSalaDestinoComOrigem(String.valueOf(1)));
            }
        }

        //Depois ir encontrar no mongo, qual é o primeiro movimento valido

        BasicDBObject query = new BasicDBObject("catch", new BasicDBObject("$exists", false));
        var collection = mongoDb.getCollection("Sensor_Porta");

        int flag = 0;
        while (flag == 0) {
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

                    Timestamp dataDaMedicao = Timestamp.valueOf(dataMongo);

                    String SPInsertPassagem = "{ call Insert_MedicaoPassagem(?,?,?,?) }";
                    CallableStatement cs = connectToSQL.getConnectionSQL().prepareCall(SPInsertPassagem);
                    cs.setInt(1, Integer.parseInt(experiencia.getId()));
                    cs.setInt(2, Integer.parseInt(salaOrigemValue));
                    cs.setInt(3, salaDestino);
                    cs.setTimestamp(4, dataDaMedicao);
                    cs.execute();

                    String fimExperiencia = "{CALL CriarAlertaInicioExperiencia (?)}";
                    CallableStatement callableStatement = connectToSQL.getConnectionSQL().prepareCall(fimExperiencia);
                    callableStatement.setInt(1, Integer.valueOf(experiencia.getId()));
                    callableStatement.execute();
                    System.out.println("Alerta Inicio Experiencia");

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



    public static void dropMongo(DB mongoDB) {
        mongoDB.getCollection("Sensor_Porta").drop();
        mongoDB.getCollection("Sensor_Temperatura").drop();
        System.out.println("Dados Apagados no Mongo");
    }

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        // Quando a experiencia tiver no estado terminado, vai reiniciar o processo novamente
        while (true) {
            //Esta parte foi toda para dentro do ConnectToMongo
            ConnectToMongo connectMongo = new ConnectToMongo();
//            MongoClient myConnectionToMongo = connectMongo.getConnectionMongo();
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

// limpa mongo facil
//        dropMongo(mongoDb);
            ResetMongoDB backupMongo = new ResetMongoDB(connectToSQL.getConnectionSQL(), mongoDb);
            //Thread que faz reset a Mongo semanalmente
            //backupMongo.start();

//        //****  Exemplo de como ir buscar informação às tabelas e mapea-las ****
//        var result = s.executeQuery("select * from experiencia;");
//        var experiencias = ExperienciaMapper.mapList(result);
//        for (Experiencia experiencia : experiencias) {
//            System.out.println("Experiencia " + "Data_Hora: " + experiencia.getDataHora() + " Id:" + experiencia.getId());
//        }


            //Neste momento é preciso lançar as Threads
            var fetchTempsMongo = new ProcessarTemperatura(mongoDb);
            var fetchDoorsMongo = new ProcessarPortas(mongoDb);
            var threadDealWithTemperatura = new TratarDadosTemperatura(connectToSQL.getConnectionSQL(), mongoDb);
            var threadDealWithPortas = new TratarDadosPortas(connectToSQL.getConnectionSQL(), mongoDb);
            var threadFetchTempsToSql = new InsertMedicaoTemperatura(connectToSQL.getConnectionSQL());
            var threadFetchPassagensToSql = new InsertMedicaoPassagem(connectToSQL.getConnectionSQL());
            var threadCheckExperienciaStatus = new CheckExperienciaStatus(connectToSQL.getConnectionSQL());

            fetchTempsMongo.start();
            fetchDoorsMongo.start();
            threadDealWithTemperatura.start();
            threadDealWithPortas.start();
            threadFetchTempsToSql.start();
            threadFetchPassagensToSql.start();
            threadCheckExperienciaStatus.start();

            threadsList.add(fetchTempsMongo);
            threadsList.add(fetchDoorsMongo);
            threadsList.add(threadDealWithTemperatura);
            threadsList.add(threadDealWithPortas);
            threadsList.add(threadFetchTempsToSql);
            threadsList.add(threadFetchPassagensToSql);
            threadsList.add(threadCheckExperienciaStatus);

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
}
