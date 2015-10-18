package com.raeffray.csv.loader;

import com.raeffray.csv.CSVRowHandler;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Label;
import org.neo4j.unsafe.batchinsert.BatchInserter;

import java.util.Map;

import static org.neo4j.graphdb.DynamicLabel.label;

/**
 * Created by mau on 18/10/15.
 */
public class BatchInsertLoader implements CSVRowHandler {

    private static final Logger logger = Logger.getLogger(BatchInsertLoader.class);

    private final BatchInserter inserter;
    private final Label[] labels;

    public BatchInsertLoader(BatchInserter inserter, String... labelNames) {
        this(inserter, labels(labelNames));
    }

    public BatchInsertLoader(BatchInserter inserter, Label... labels) {
        this.inserter = inserter;
        this.labels = labels;
    }

    @Override
    public void processLine(Map<String, ? super String> row) {
        inserter.createNode((Map<String, Object>) row, labels);
    }

    private static Label[] labels(final String[] labelNames) {
        Label[] labels = new Label[labelNames.length];
        for (int i = 0; i < labelNames.length; i++) {
            labels[i] = label(labelNames[i]);
        }
        return labels;
    }
}
