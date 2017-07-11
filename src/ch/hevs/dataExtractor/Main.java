package ch.hevs.dataExtractor;

import ch.hevs.dataExtractor.saveSystem.MySQLExtractor;

import java.io.File;
import java.util.Scanner;

/**
 * Created by gael on 07.07.17.
 */
public class Main {
    public static void main(String[] args) {

        MySQLExtractor ex = new MySQLExtractor();
        ex.initConnection();

        File settings = new File("Settings.csv");
        ex.importSettings(settings);
        for(int i = 0; i < ex.totalNbrOfSet; i++) {
            File measures = new File("MeasureSet" + String.format("%03d", (i + 1)) + ".csv");
            ex.importMeasure(i + 1, measures);
        }
    }
}
