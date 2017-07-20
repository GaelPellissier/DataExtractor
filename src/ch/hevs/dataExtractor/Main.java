package ch.hevs.dataExtractor;

import ch.hevs.dataExtractor.saveSystem.MySQLExtractor;

/**
 * @file    Main.java
 * @brief   Class extracting the data from the database and generates a .csv file.
 *
 * @class   Main
 * @brief   Class extracting the data from the database and generates a .csv file.
 * @details This class is made to access a MySQL database and create for each measure set a new .csv file containing all the measurement datas.
 *
 * @author G.Pellissier
 * @date    07.07.2017
 */
public class Main {
    public static void main(String[] args) {

        boolean valid = false;
        int firstSet = 1, lastSet = 1;
        MySQLExtractor ex = new MySQLExtractor();
        ex.initConnection();

        // File name for saving the settings. Each measure set is a new line
        String settings = "Settings.csv";
        switch(args.length) {
            case 0:
                // No arguments -> importing the full sets and measures corresponding
                firstSet = 1;
                lastSet = ex.totalNbrOfSet;
                valid = true;
                break;

            case 2:
                // 2 arguments -> importing the sets and measures from args[0] to args[1]
                firstSet = Integer.parseInt(args[0]);
                lastSet = Integer.parseInt(args[1]);

                // In case the user exchanges the 2 arguments last <-> first
                if(firstSet > lastSet) {
                    int temp = lastSet;
                    lastSet = firstSet;
                    firstSet = temp;
                }

                valid = true;
                break;

            default:
                // Error in the number of arguments -> do nothing
                System.out.println("Invalid number of arguments. Try again with :");
                System.out.println("No arguments to import all the measures");
                System.out.println("2 Arguments to import all the measures from the sets whose ID are between first (included). and second arguments (included).");
        }


        if(valid) {
            ex.importSettings(firstSet, lastSet, settings);
            for (int i = firstSet; i <= lastSet; i++) {
                String measures = "MeasureSet" + String.format("%03d", i) + ".csv";
                ex.importMeasure(i, measures);
            }
            System.out.println("Extraction ended");
        }
    }
}
