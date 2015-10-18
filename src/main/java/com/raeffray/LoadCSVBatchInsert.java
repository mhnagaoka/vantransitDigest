package com.raeffray;

import com.raeffray.csv.loader.BatchInsertLoader;
import com.raeffray.raw.data.Agency;
import com.raeffray.raw.data.Routes;
import com.raeffray.raw.data.StopTimes;
import com.raeffray.raw.data.Trips;
import org.neo4j.graphdb.Label;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.raeffray.csv.CSVReaderTemplateBuilder.readCSVForClass;
import static org.neo4j.graphdb.DynamicLabel.label;

/**
 * Loads CSV file data into Neo4J.
 */
public class LoadCSVBatchInsert {

    public static void main(String[] args) throws Exception {
        BatchInserter inserter = null;
        Label agency = label("Agency");
        Label routes = label("Routes");
        Label trips = label("Trips");
        Label stopTimes = label("StopTimes");
        try {
            inserter = BatchInserters.inserter(
                    new File("import").getAbsolutePath());
            Map<String, Long> nodeIdMap = new HashMap<>(); // map no formato: "agency_id=<agency id>" -> "<agency nodeId>"
            inserter.createDeferredSchemaIndex(agency).on("agency_id");
            readCSVForClass(Agency.class).processWith(new BatchInsertLoader(inserter, nodeIdMap, agency));
            inserter.createDeferredSchemaIndex(routes).on("agency_id");
            inserter.createDeferredSchemaIndex(routes).on("route_id");
            readCSVForClass(Routes.class).processWith(new BatchInsertLoader(inserter, nodeIdMap, routes));
            inserter.createDeferredSchemaIndex(trips).on("route_id");
            inserter.createDeferredSchemaIndex(trips).on("trip_id");
            readCSVForClass(Trips.class).processWith(new BatchInsertLoader(inserter, nodeIdMap, trips));
            inserter.createDeferredSchemaIndex(stopTimes).on("trip_id");
            inserter.createDeferredSchemaIndex(stopTimes).on("stop_id");
            readCSVForClass(StopTimes.class).processWith(new BatchInsertLoader(inserter, nodeIdMap, stopTimes));
        } finally {
            if (inserter != null) {
                inserter.shutdown();
            }
        }

        // após executar a inserção dos dados. mover a pasta import para a pasta graph.db e mudar o owner dos arquivos
        // docker stop neo4j; sudo rm -rf neo4j-data/graph.db/*; sudo mv import/* neo4j-data/graph.db; sudo chown -R root: neo4j-data/graph.db/*; docker start neo4j

        //match (a:Agency),(r:Routes) where a.agency_id = r.agency_id and not(a-->r) create a-[rl:OPERATES]->r return a,r limit 100
        //match (r:Routes),(t:Trips) where r.route_id = t.route_id and not(r-->t) create r-[rl:TRAVELS]->t return r,t limit 100

    }
}
