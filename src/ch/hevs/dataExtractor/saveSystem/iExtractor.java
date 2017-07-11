package ch.hevs.dataExtractor.saveSystem;

import java.io.File;

/**
 * Created by gael on 11.07.17.
 */
public interface iExtractor {

    void initConnection();

    void importMeasure(int set, String fileName);

    void importSettings(String fileName);
}
