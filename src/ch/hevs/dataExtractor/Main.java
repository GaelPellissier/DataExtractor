package ch.hevs.dataExtractor;

import ch.hevs.dataExtractor.saveSystem.MySQLExtractor;

/**
 * @file    Main.java
 * @brief   Main class of this software.
 *
 * @class   Main
 * @brief   Main class of this software.
 * @details <h2>Generalities</h2>
 *          This class is made to access a saving system (for example a database) and import the wanted datas in a file.<br>
 *          The file is a .csv file called "temp.csv" and it is created in the root directory where the software is located.<br>
 *          <h2>Working</h2>
 *          While running, this software will connect itself to any database or saving system defined (in this case, a MySQL database).<br>
 *          Then it will import the datas in the .csv file. The datas will correspond to the device whose id is in the first argument
 *          (unless it is the -h option) and from the 2nd argument tiemstamp to 24h later.<br>
 *          <h2>Settings</h2>
 *          To run this software, use the command below:<br>
 *          <blockquote>java -jar DataExtractor.jar [OPTION] deviceID firstTimeMeasure</blockquote>
 *
 *          OPTION:
 *              <ul>
 *                  <li>-h: displays help menu.</li>
 *              </ul>
 *
 * @author G.Pellissier
 * @date    07.07.2017
 */
public class Main {
    public static void main(String[] args) {
        int n = args.length;

        if(n != 2 || args[0].equals("-h")) {
            System.out.println("DataExtractor Tool - Help\n");
            System.out.println("NAME:\n     DataExtractor\n");
            System.out.println("SYNOPSIS:\n     DataExtractor [OPTION] deviceID firstMeasureTime\n");
            System.out.println("DESCRIPTION:\n      Run the DataExtractor Tool to get saved datas and importing them in a .csv file.\n" +
                    "       (The time between the first and the last measure is 24h)\n");
            System.out.println("OPTION:");
            System.out.println("        -h, display help. No other argument needed.\n");
            System.out.println("ARGUMENTS:");
            System.out.println("        deviceID, ID of the wanted device as it is in the saving system\n" +
                    "        firstMeasureTime, timestamp in millisecond of the first measure to import in the file");

            System.exit(0);
        }
        else {
            final long DAY_DURATION_IN_MS = 86400000;
            long timeBegin = 0;
            MySQLExtractor ex = new MySQLExtractor();

            ex.initConnection();

            try {
                timeBegin = Long.parseLong(args[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long timeEnd = timeBegin + DAY_DURATION_IN_MS + 1000;
            ex.importMeasure(args[0], timeBegin, timeEnd);
        }
    }
}
