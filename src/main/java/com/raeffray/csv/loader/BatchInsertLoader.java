package com.raeffray.csv.loader;

import com.raeffray.csv.CSVRowHandler;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.unsafe.batchinsert.BatchInserter;

import java.util.Map;

/**
 * Created by mau on 18/10/15.
 */
public class BatchInsertLoader implements CSVRowHandler {

    private static final Logger logger = Logger.getLogger(BatchInsertLoader.class);

    private final BatchInserter inserter;
    private final Label[] labels;
    private final Map<String, Long> nodeIdMap;

    public BatchInsertLoader(BatchInserter inserter, Map<String, Long> nodeIdMap, Label... labels) {
        this.inserter = inserter;
        this.labels = labels;
        this.nodeIdMap = nodeIdMap;
    }

    @Override
    public void processLine(Map<String, ? super String> row) {
        long nodeId = inserter.createNode((Map<String, Object>) row, labels);
        if (labels[0].name().equals("Agency")) {
            nodeIdMap.put("agency_id=" + row.get("agency_id"), nodeId);
        } else if (labels[0].name().equals("Routes")) {
            nodeIdMap.put("route_id=" + row.get("route_id"), nodeId);
            inserter.createRelationship(nodeIdMap.get("agency_id=" + row.get("agency_id")), nodeId, DynamicRelationshipType.withName("OPERATES"), null);
        }
    }
}
