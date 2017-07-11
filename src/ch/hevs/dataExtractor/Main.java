package ch.hevs.dataExtractor;

import ch.hevs.dataExtractor.saveSystem.MySQLExtractor;

/**
 * Created by gael on 07.07.17.
 */
public class Main {
    public static void main(String[] args) {

        MySQLExtractor ex = new MySQLExtractor();
        ex.initConnection();

        String settings = "Settings.csv";
        ex.importSettings(settings);
        for(int i = 0; i < ex.totalNbrOfSet; i++) {
            String measures = "MeasureSet" + String.format("%03d", (i + 1)) + ".csv";
            ex.importMeasure(i + 1, measures);
        }
    }
}
