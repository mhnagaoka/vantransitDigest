package com.raeffray;

import com.raeffray.csv.loader.BatchLoader;
import com.raeffray.csv.loader.ThreadedBatchLoader;
import com.raeffray.raw.data.Agency;
import com.raeffray.raw.data.Routes;
import com.raeffray.raw.data.StopTimes;
import com.raeffray.raw.data.Trips;

import static com.raeffray.csv.CSVReaderTemplateBuilder.readCSVForClass;

/**
 * Loads CSV file data into Neo4J.
 */
public class LoadCSV {

    public static void main(String[] args) throws Exception {
//        final BatchLoader loader = new BatchLoader(100);
        final ThreadedBatchLoader loader = new ThreadedBatchLoader(100, 4);
        readCSVForClass(Agency.class).processWith(loader);
        readCSVForClass(Routes.class).processWith(loader);
        readCSVForClass(Trips.class).processWith(loader);
//        readCSVForClass(StopTimes.class).processWith(loader);
        loader.flush();
        loader.shutdown();

        //match (a:Agency),(r:Routes) where a.agency_id = r.agency_id and not(a-->r) create a-[rl:OPERATES]->r return a,r limit 100
        //match (r:Routes),(t:Trips) where r.route_id = t.route_id and not(r-->t) create r-[rl:TRAVELS]->t return r,t limit 100
        //create index on :Trips(tripId)
        //create index on :StopTimes(tripId)
        //match (t:Trips),(st:StopTimes) where t.tripId = st.tripId create t-[rl:STOPS_AT]->st return rl

    }
}
