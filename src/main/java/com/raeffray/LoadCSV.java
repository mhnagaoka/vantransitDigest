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

        //match (a:Agency),(r:Routes) where a.agencyId = r.agencyId create a-[rl:OPERATES]->r return rl
        //match (r:Routes),(t:Trips) where r.routeId = t.routeId create r-[rl:TRAVELS]->t return rl
        //create index on :Trips(tripId)
        //create index on :StopTimes(tripId)
        //match (t:Trips),(st:StopTimes) where t.tripId = st.tripId create t-[rl:STOPS_AT]->st return rl

    }
}
