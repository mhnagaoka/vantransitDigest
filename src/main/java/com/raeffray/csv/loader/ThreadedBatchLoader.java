package com.raeffray.csv.loader;

import com.raeffray.csv.InstanceHandler;
import com.raeffray.raw.data.RawData;
import com.raeffray.rest.cient.LabeledNode;
import com.raeffray.rest.cient.RestClient;
import org.apache.commons.lang3.Validate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mau on 18/10/15.
 */
public class ThreadedBatchLoader implements InstanceHandler<RawData> {

    private int batchSize = 10;

    private final ExecutorService threadPool;

    private List<LabeledNode> batchBuffer;

    public ThreadedBatchLoader(int batchSize, int poolSize) {
        Validate.isTrue(batchSize > 0, "batchSize=%d (should be greater than 0)", batchSize);
        this.batchSize = batchSize;
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.batchBuffer = new LinkedList<>();
    }

    @Override
    public void processInstance(RawData instance) {
        batchBuffer.add(new LabeledNode(labelsFor(instance), instance));
        if (batchBuffer.size() >= batchSize) {
            flush();
        }
    }

    public void flush() {
        if (batchBuffer.size() > 0) {
            final List<LabeledNode> batchBuffer = this.batchBuffer;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    RestClient.getInstance().createLabeledNodes(batchBuffer);
                }
            });
            this.batchBuffer = new LinkedList<>();
        }
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    private String[] labelsFor(RawData instance) {
        return new String[]{instance.getClass().getSimpleName()};
    }

}
