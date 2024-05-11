package MongoDB;

import MongoDB.entities.Corredor;
import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.RequiredArgsConstructor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@RequiredArgsConstructor
public class TratarDadosTemperatura extends Thread {
    private List<Integer> leituras = new ArrayList<>();
    private final Connection sqlDb;
    private final DB mongoDb;
    private int outlierCount = 0;
    private DadosQueue queue = DadosQueue.getInstance();
    private CurrentExperiencia experiencia = CurrentExperiencia.getInstance();

    @Override
    public void run() {
        try {
            tratarDadosTemperatura();
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void tratarDadosTemperatura() throws SQLException, InterruptedException {
        while (true) {
            var tempData = queue.popData();
            var collection = mongoDb.getCollection("Sensor_Temperatura");
            var experienciaId = experiencia.getExperiencia().getId();
            // if outlier então
            if (isOutlier(tempData)) {
                outlierCount++;
                int maxNumOutliers = executeSPNumberMaxOutliers();

                BasicDBObject idQuery = new BasicDBObject("_id", tempData.getId());
                BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("outlier", "O"));
                collection.update(idQuery, update);

                if (outlierCount <= maxNumOutliers) {
                    queue.pushTempsTratadas(List.of(tempData));

                    String CallSP = "{ call CriarAlertaOutlierAmarelo(?,?) }";
                    CallableStatement c = sqlDb.prepareCall(CallSP);
                    c.setInt(1, outlierCount);
                    c.setInt(2, Integer.parseInt(experienciaId));
                    c.execute();

                    //Thread.sleep(30000);
                } else {

                    CallableStatement cs = null;
                    cs = sqlDb.prepareCall("{call CriarAlertaOutlierVermelho}");
                    cs.executeQuery();
                    //Thread.sleep(10000);
                }
            } else {
                queue.pushTempsTratadas(List.of(tempData));
                System.out.println("Não é outlier");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isOutlier(DadosTemperaturaMongoDB dadosTemperaturaMongoDB) {
        InterquartileRange interquartileRange = new InterquartileRange();
        if (leituras.size() < 40) {
            leituras.add(dadosTemperaturaMongoDB.getLeitura());
            return false;
        }
        var isOutlier = interquartileRange.isOutlier(leituras, leituras.size(), dadosTemperaturaMongoDB.getLeitura());
        if (isOutlier) {
            return true;
        }
        leituras.add(0, dadosTemperaturaMongoDB.getLeitura());
        leituras.remove(leituras.size() - 1);

        return false;
    }

    public int executeSPNumberMaxOutliers() throws SQLException {
        String CallSP = "{ call GetNumOutliersMaximo(?) }";
        CallableStatement c = sqlDb.prepareCall(CallSP);
        c.registerOutParameter(1, Types.INTEGER);
        c.execute();
        int maxNumberOutliers = c.getInt(1);

        return maxNumberOutliers;
    }

    public class InterquartileRange {

        // Function to give
        // index of the median
        public int median(List<Integer> a,
                          int l, int r) {
            int n = r - l + 1;
            n = (n + 1) / 2 - 1;
            return n + l;
        }

        // Function to calculate IQR
        public boolean isOutlier(List<Integer> a, int n, int value) {
            Collections.sort(a);

            // Index of median
            // of entire data
            int mid_index = median(a, 0, n - 1);

            // Median of first half
            int Q1;
            if (n % 2 == 0)
                Q1 = a.get(median(a, 0, mid_index));
            else
                Q1 = a.get(median(a, 0, mid_index - 1));

            // Median of second half
            int Q3;
            if (n % 2 == 0)
                Q3 = a.get(median(a, mid_index + 1, n - 1));
            else
                Q3 = a.get(median(a, mid_index + 1, n));

            // IQR calculation
            var IQR = Q3 - Q1;

            var upperFence = Q3 + (1.5 * IQR);
            var lowerFence = Q1 - (1.5 * IQR);

            return !(lowerFence <= value && value <= upperFence);
        }
    }
}