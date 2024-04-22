package MongoDB;

import com.mongodb.DB;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;

@Builder
@RequiredArgsConstructor
public class ProcessarTemperatura extends Thread {
    private final DB mongoDb;
    private final Connection sqlDb;

    @Override
    public void run() {

    }
}
