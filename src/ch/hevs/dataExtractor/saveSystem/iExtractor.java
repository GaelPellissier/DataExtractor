package ch.hevs.dataExtractor.saveSystem;

/**
 * @file    iExtractor.java
 * @brief   Extractor interface.
 *
 * @class   iExtractor
 * @biref   Extractor interface.
 * @details This class implements the interface <i>iExtractor</i>. It will connects itself to a MySQL database and import the wanted datas.
 *
 * @author  G.Pellissier
 * @date    11.07.17.
 */

public interface iExtractor {

    /**
     * @details Method needed to establish a connection between the software and the saving system (to let empty if no connection is needed).
     */
    void initConnection();

    /**
     * @details Basic method to import the datas
     * @param   product             : Device whose datas are wanted (it can be an ID, a name, ...).
     * @param   firstMeasureTime    : Timestamp for the first measure to collect.
     * @param   lastMeasureTime     : Timestamp for the last measure to collect.
     */
    void importMeasure(String product, long firstMeasureTime, long lastMeasureTime);
}
