package CloudToMongo;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.eclipse.paho.client.mqttv3.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;

public class CloudToMongoSensoresPortas implements MqttCallback {
    MqttClient mqttclient;
    static MongoClient mongoClient;
    static DB db;
    static DBCollection mongocol;
    static String mongo_user = new String();
    static String mongo_password = new String();
    static String mongo_address = new String();
    static String cloud_server = new String();
    static String cloud_topic_maze = new String();
    static String mongo_host = new String();
    static String mongo_replica = new String();
    static String mongo_database = new String();
    static String mongo_collection_maze = new String();
    static String mongo_authentication = new String();
    static JTextArea documentLabel = new JTextArea("\n");


    private static void createWindow() {
        JFrame frame = new JFrame("Cloud to Mongo Sensores Portas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel textLabel = new JLabel("Data from broker: ", SwingConstants.CENTER);
        textLabel.setPreferredSize(new Dimension(600, 30));
        JScrollPane scroll = new JScrollPane(documentLabel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(600, 200));
        JButton b1 = new JButton("Stop the program");
        frame.getContentPane().add(textLabel, BorderLayout.PAGE_START);
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        frame.getContentPane().add(b1, BorderLayout.PAGE_END);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        createWindow();
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("cloudToMongo.ini"));
            mongo_address = p.getProperty("mongo_address");
            mongo_user = p.getProperty("mongo_user");
            mongo_password = p.getProperty("mongo_password");
            mongo_replica = p.getProperty("mongo_replica");
            cloud_server = p.getProperty("cloud_server");
            cloud_topic_maze = p.getProperty("cloud_topic1");
            mongo_host = p.getProperty("mongo_host");
            mongo_database = p.getProperty("mongo_database");
            mongo_authentication = p.getProperty("mongo_authentication");
            mongo_collection_maze = p.getProperty("mongo_collection_maze");
        } catch (Exception e) {
            System.out.println("Error reading CloudToMongo.ini file " + e);
            JOptionPane.showMessageDialog(null, "The CloudToMongo.ini file wasn't found.", "CloudToMongo", JOptionPane.ERROR_MESSAGE);
        }
        new CloudToMongoSensoresPortas().connecCloud();
        new CloudToMongoSensoresPortas().connectMongo();
    }

    public void connecCloud() {
        int i;
        try {
            i = new Random().nextInt(100000);
            mqttclient = new MqttClient(cloud_server, "CloudToMongo_" + String.valueOf(i) + "_" + cloud_topic_maze);
            mqttclient.connect();
            mqttclient.setCallback(this);
            mqttclient.subscribe(cloud_topic_maze);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connectMongo() {
        String mongoURI = new String();
        mongoURI = "mongodb://";
        System.out.println(mongo_authentication);
        if (mongo_authentication.equals("true")) mongoURI = mongoURI + mongo_user + ":" + mongo_password + "@";
        mongoURI = mongoURI + mongo_address;
        if (!mongo_replica.equals("false"))
            if (mongo_authentication.equals("true"))
                mongoURI = mongoURI + "/?replicaSet=" + mongo_replica + "&authSource=admin";
            else mongoURI = mongoURI + "/?replicaSet=" + mongo_replica;
        else if (mongo_authentication.equals("true")) mongoURI = mongoURI + "/?authSource=admin";
        MongoClientURI bru = (new MongoClientURI(mongoURI));
        MongoClient mongoClient = new MongoClient(bru);
        db = mongoClient.getDB(mongo_database);
        mongocol = db.getCollection(mongo_collection_maze);
    }


    @Override
    public void messageArrived(String topic, MqttMessage c)
            throws Exception {
        try {
            DBObject document_json;
            document_json = (DBObject) JSON.parse(c.toString());
            documentLabel.append(c.toString() + "\n");

            if (validarFormatoHora((String) document_json.get("Hora"))) {

                try {
                    int SalaOrigem = Integer.parseInt(document_json.get("SalaOrigem").toString());
                    System.out.println("A Sala Origem é: " + SalaOrigem);
                    int SalaDestino = Integer.parseInt(document_json.get("SalaDestino").toString());
                    System.out.println("A Sala Destino é: " + SalaDestino);

                    if (SalaOrigem <= 0 || SalaDestino <= 0) {
                        // O valor de SalaOrigem ou SalaDestino não é válido
                        System.out.println("Valores inferiores ou iguais a 0 para SalaOrigem ou SalaDestino: SalaOrigem="
                                + SalaOrigem + ", SalaDestino=" + SalaDestino);
                    }else{
                        mongocol.insert(document_json);
                    }
                } catch (NumberFormatException e) {
                    // Dado inválido recebido para o campo SalaOrigem ou SalaDestino
                    System.out.println("Dado inválido recebido para o campo SalaOrigem ou SalaDestino: " + "SalaOrigem=" +
                            document_json.get("SalaOrigem") + ", SalaDestino=" + document_json.get("SalaDestino"));
                }

            } else {
                // A hora não está no formato esperado
                System.out.println("Formato inválido para o campo Hora: " + document_json.get("Hora"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean validarFormatoHora(String hora) {
        // Expressão regular para o formato YYYY-MM-DD HH:mm:ss.SSSSSS
        String regex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{6}";
        return hora.matches(regex);
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

}
