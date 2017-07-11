package ch.hevs.dataExtractor.saveSystem;

import java.io.File;
import java.io.FileOutputStream;
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
        //final String url = "jdbc:mysql://153.109.5.139/";
        final String url = "jdbc:mysql://localhost/";
        // Name of the database
        final String database = "network_analyzer";
        // User and password values (make sure that this user has the right privileges, even in case of a dynamic IP)
        final String userName = "remote";
        final String password = "user";
        try {
            // Load the JDBC driver for MySQL connection
            Class.forName(driverURL).newInstance();

            // Connection to database
            conn = DriverManager.getConnection(url + database, userName, password);

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void importMeasure(int set, File f) {
        Statement stmt;
        String response = "";

        try {
            stmt = conn.createStatement();

            String query ="SELECT * FROM Measure WHERE measureSetID=" + set + ";";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                response = response
                        + String.format("%02d",(rs.getInt("measureSetID"))) + ","
                        + String.format("%03d",(rs.getInt("id"))) + ","
                        + String.valueOf(rs.getLong("invalidateTime")) + ","
                        + String.valueOf(rs.getLong("updateTime")) + ","
                        + rs.getString("isFailed") + ","
                        + String.format("%03d",(rs.getLong("ping"))) + ","
                        + rs.getString("expectedState") + ","
                        + rs.getString("state") + ","
                        + Integer.toString(rs.getInt("rssi")) + ";\n";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        saveInFile(f, response);
    }

    @Override
    public void importSettings(File f) {
        Statement stmt;
        String response = "";

        try {
            stmt = conn.createStatement();
            String query = "SELECT Max(id) AS maxSet FROM MeasureSet;";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                this.totalNbrOfSet = rs.getInt("maxSet");
            }

            query = "SELECT MeasureSet.id AS setID, Techno.product AS product, Gateway.name AS gateway, Device.model AS device,"
                    + " Device.serialNbr AS serial, TypeDevice.kindDevice AS deviceType, TypeDevice.battery AS battery,"
                    + " PositionDevice.description AS topology, MeasureSet.period AS period, MeasureSet.nbrMeasures AS nbrMeasures"
                    + " FROM MeasureSet INNER JOIN"
                    + " (SELECT Technology.name AS product, Gateway.id AS id FROM Technology"
                    + " INNER JOIN Gateway ON Gateway.technologyID = Technology.id) AS Techno"
                    + " ON Techno.id = MeasureSet.gatewayID INNER JOIN Gateway ON Gateway.id = MeasureSet.gatewayID"
                    + " INNER JOIN Device ON Device.id = MeasureSet.deviceID LEFT OUTER JOIN"
                    + " (SELECT Device.id AS id, DeviceType.name AS kindDevice, DeviceType.battery AS battery FROM DeviceType"
                    + " INNER JOIN Device ON DeviceType.id = Device.deviceTypeID) AS TypeDevice ON TypeDevice.id = MeasureSet.deviceID"
                    + " INNER JOIN PositionDevice ON PositionDevice.id = MeasureSet.positionDeviceID;";
            rs = stmt.executeQuery(query);
            while(rs.next()) {
                response = response
                        + Integer.toString(rs.getInt("setID")) + ","
                        + rs.getString("product") + ","
                        + rs.getString("gateway") + ","
                        + rs.getString("device") + ","
                        + rs.getString("serial") + ","
                        + rs.getString("deviceType") + ","
                        + rs.getBoolean("battery") + ","
                        + rs.getString("topology") + ","
                        + Integer.toString(rs.getInt("period")) + ","
                        + Integer.toString(rs.getInt("nbrMeasures")) + ";\n";
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        saveInFile(f, response);
    }

    private void saveInFile(File f, String s) {
        System.out.println(s);
    }
}
