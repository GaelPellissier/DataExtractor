package ch.hevs.dataExtractor.saveSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;

/**
 * @file    MySQLExtractor.java
 * @brief   Extractor for a MySQL database.
 *
 * @class   MySQLExtractor
 * @biref   Extractor for a MySQL database.
 * @details This class implements the interface <i>iExtractor</i>. It will connects itself to a MySQL database and import the wanted datas.
 *
 * @author  G.Pellissier
 * @date    07.07.17.
 */
public class MySQLExtractor implements iExtractor {

    /**
     * @details Object used to connect the software to the MySQL database.
     */
    private Connection conn;

    /**
     * @details Opens a connection between the software and the database.
     */
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

        try {
            // Load the JDBC driver for MySQL connection
            Class.forName(driverURL).newInstance();

            // Connection to database
            conn = DriverManager.getConnection(url + database, userName, password);

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @details Method used to import the datas in the .csv file.
     * @param   id                  : id of the device whose measures are needed.
     * @param   firstMeasureTime    : Timestamp for the first measure to import
     * @param   lastMeasureTime     : Timestamp for the last measure to import
     */
    @Override
    public void importMeasure(String id, long firstMeasureTime, long lastMeasureTime) {
        Statement stmt;
        String response = "";

        try {
            stmt = conn.createStatement();

            String query = "SELECT deviceID, technology, ping, rssi, requestOK, valid, expState, state" +
                    " FROM MeasureSet INNER JOIN  (SELECT Measure.measureSetID AS setID, Device.id AS deviceID, Device.technology AS technology," +
                    " Measure.requestTime AS time, Measure.requestOK AS requestOK, Measure.rssi AS rssi," +
                    " Measure.valid AS valid, Measure.ping AS ping, Measure.expectedState AS expState, Measure.state AS state" +
                    " FROM Measure INNER JOIN Device ON Measure.deviceID = Device.id) AS Datas" +
                    " ON Datas.setID = MeasureSet.id AND Datas.deviceID=" + id +
                    " AND time>=" + firstMeasureTime + " AND time<"+ lastMeasureTime + " ORDER BY Datas.time;";

            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                response = response
                        + Integer.toString(rs.getInt("deviceID")) + ","
                        + rs.getString("technology") + ","
                        + String.format("%04d",(rs.getLong("ping"))) + ","
                        + Integer.toString(rs.getInt("rssi")) + ","
                        + Integer.toString(rs.getInt("requestOK")) + ","
                        + Integer.toString(rs.getInt("valid")) + ","
                        + rs.getString("expState") + ","
                        + rs.getString("state") + ";\n";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        saveInFile("temp.csv", response);
    }

    /**
     * @details  Method that creates the new file and fill it in with the datas collected.
     * @param   fileName    : Name of the file to create.
     * @param   s           : Expected content of the file.
     */
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
