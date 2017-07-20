package ch.hevs.dataExtractor.saveSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by gael on 07.07.17.
 */
public class MySQLExtractor implements iExtractor {
    private Connection conn;
    public int totalNbrOfSet;

    @Override
    public void initConnection() {
        final String driverURL = "com.mysql.jdbc.Driver";
        // URL of the database's server
        final String url = "jdbc:mysql://153.109.5.139/";
        //final String url = "jdbc:mysql://localhost/";
        // Name of the database
        final String database = "network_analyzer";
        // User and password values (make sure that this user has the right privileges, even in case of a dynamic IP)
        final String userName = "remote";
        final String password = "user";

        Statement stmt;
        try {
            // Load the JDBC driver for MySQL connection
            Class.forName(driverURL).newInstance();

            // Connection to database
            conn = DriverManager.getConnection(url + database, userName, password);

            //Initialize totalNbrOfSet
            stmt = conn.createStatement();
            String query = "SELECT Max(id) AS maxSet FROM MeasureSet;";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                this.totalNbrOfSet = rs.getInt("maxSet");
            }

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void importMeasure(int set, String fileName) {
        Statement stmt;
        String response = "";

        try {
            stmt = conn.createStatement();

            if(set > totalNbrOfSet) {
                set = totalNbrOfSet;
            }

            String query = "SELECT * FROM Measure WHERE measureSetID=" + set + ";";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                response = response
                        + String.format("%04d",(rs.getInt("measureSetID"))) + ","
                        + String.format("%05d",(rs.getInt("id"))) + ","
                        + String.valueOf(rs.getLong("invalidateTime")) + ","
                        + String.valueOf(rs.getLong("updateTime")) + ","
                        + rs.getString("isFailed") + ","
                        + String.format("%03d",(rs.getLong("ping"))) + ","
                        + rs.getString("expectedState") + ","
                        + rs.getString("state") + ","
                        + Integer.toString(rs.getInt("rssi")) + ","
                        + rs.getString("value") + ";\n";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        saveInFile(fileName, response);
    }

    @Override
    public void importSettings(int firstSet, int lastSet, String fileName) {
        Statement stmt;
        String response = "";

        try {
            stmt = conn.createStatement();


            String query = "SELECT MeasureSet.id AS setID, Techno.product AS product, Gateway.name AS gateway, Device.model AS device,"
                    + " Device.serialNbr AS serialNbr, TypeDevice.kindDevice AS deviceType, TypeDevice.battery AS battery,"
                    + " MeasureSet.distanceToGateway AS distance, MeasureSet.nbrWalls AS topology, MeasureSet.period AS period,"
                    + " MeasureSet.nbrMeasures AS nbrMeasures FROM MeasureSet INNER JOIN"
                    + " (SELECT Technology.name AS product, Gateway.id AS id FROM Technology"
                    + " INNER JOIN Gateway ON Gateway.technologyID = Technology.id) AS Techno"
                    + " ON Techno.id = MeasureSet.gatewayID INNER JOIN Gateway ON Gateway.id = MeasureSet.gatewayID"
                    + " INNER JOIN Device ON Device.id = MeasureSet.deviceID LEFT OUTER JOIN"
                    + " (SELECT Device.id AS id, DeviceType.name AS kindDevice, DeviceType.battery AS battery FROM DeviceType"
                    + " INNER JOIN Device ON DeviceType.id = Device.deviceTypeID) AS TypeDevice ON TypeDevice.id = MeasureSet.deviceID"
                    + " WHERE (MeasureSet.id>=" + firstSet + ") AND (MeasureSet.id<=" + lastSet + ");";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                response = response
                        + String.format("%04d",(rs.getInt("setID"))) + ","
                        + rs.getString("product") + ","
                        + rs.getString("gateway") + ","
                        + rs.getString("device") + ","
                        + rs.getString("serialNbr") + ","
                        + rs.getString("deviceType") + ","
                        + rs.getBoolean("battery") + ","
                        + String.format("%02d",(rs.getInt("distance"))) + ","
                        + String.format("%02d",(rs.getInt("topology"))) + ","
                        + String.format("%05d",(rs.getInt("period"))) + ","
                        + String.format("%03d",(rs.getInt("nbrMeasures"))) + ";\n";
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        saveInFile(fileName, response);
    }

    private void saveInFile(String fileName, String s) {
        File f = new File(fileName);
        try {
            // Create a new file if it doesn't exist already
            if(!f.exists()){
                f.createNewFile();
            }
            PrintWriter out = new PrintWriter(new FileWriter(f, true));
            out.append(s);
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
