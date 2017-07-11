package ch.hevs.dataExtractor.saveSystem;

import java.io.File;

/**
 * Created by gael on 11.07.17.
 */
public interface iExtractor {

    void initConnection();

    void importMeasure(int set, File f);

    void importSettings(File f);
}
