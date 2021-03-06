package utils.implementation;

import common.IInstanceElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.interfaces.IElementCacheListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class MemoryTracker implements IElementCacheListener<IInstanceElement> {
    private static final Logger logger = LogManager.getLogger(MemoryTracker.class.getSimpleName());
    private static final int loggingInterval = 10000;
    private PrintStream out;
    private long n = 0;
    private long lastTime = System.currentTimeMillis();
    private long start = System.currentTimeMillis();

    private double maxTime = Double.MIN_VALUE;
    private double minTime = Double.MAX_VALUE;

    private long maxMemory = 0;
    private long maxUsedMemory = 0;


    public MemoryTracker(String output) {
        try {
            out = new PrintStream(new FileOutputStream(Helper.createFile(output + File.separator + "instances.txt")));
        } catch (FileNotFoundException e) {
            logger.warn("Cannot create output stream to " + output + File.separator + "instances.txt\n Using system out instead.");
            out = System.out;
        }
    }


    @Override
    public void elementFlushed(IInstanceElement instance) {
        n++;
        if (n % loggingInterval == 0) {
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - lastTime;
            lastTime = currentTime;
            double instancesPerSecond = (100000.0 / delta * 1000.0);
            maxTime = Math.max(instancesPerSecond, maxTime);
            minTime = Math.min(instancesPerSecond, minTime);

            long runtimeMaxMemory = getCurrentlyMaxMemory();
            long runtimeUsedMemory = getReallyUsedMemory();

            maxMemory = Math.max(runtimeMaxMemory, maxMemory);
            maxUsedMemory = Math.max(maxUsedMemory, runtimeUsedMemory);

            logger.info("--------------------------------------");
            logger.info("Instance count: " + n);
            logger.info("Instances per second: " + instancesPerSecond);
            logger.info("Max Memory: " + (runtimeMaxMemory / 1024 / 1024) + "MB");
            logger.info("Used Memory: " + (runtimeUsedMemory / 1024 / 1024) + "MB");
            logger.info("--------------------------------------");
        }
    }

    @Override
    public void finished() {
        out.println("--------Instances---------");
        out.println("Num: " + n);
        out.println("Time: " + (System.currentTimeMillis() - start) / 1000 + "s");
        out.println("Maximum instances per second: " + maxTime);
        out.println("Minimum instances per second: " + minTime);
        out.println("Maximum memory allocated: " + (maxMemory / 1024 / 1024) + "MB");
        out.println("Maximum memory used: " + (maxUsedMemory / 1024 / 1024) + "MB");
        out.println("--------------------------");
        logger.info("--------Instances---------");
        logger.info("Num: " + n);
        logger.info("Time: " + (System.currentTimeMillis() - start) / 1000 + "s");
        logger.info("Maximum instances per second: " + maxTime);
        logger.info("Minimum instances per second: " + minTime);
        logger.info("Maximum memory allocated: " + (maxMemory / 1024 / 1024) + "MB");
        logger.info("Maximum memory used: " + (maxUsedMemory / 1024 / 1024) + "MB");
        logger.info("--------------------------");
    }


    ////DIRTY LITTLE HELPER
    private long getReallyUsedMemory() {
        long before = getGcCount();
        System.gc();
        while (getGcCount() == before) ;
        return getCurrentlyUsedMemory();
    }

    private long getGcCount() {
        long sum = 0;
        for (GarbageCollectorMXBean b : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = b.getCollectionCount();
            if (count != -1) {
                sum += count;
            }
        }
        return sum;
    }

    private long getCurrentlyUsedMemory() {
        return
                ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() +
                        ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
    }

    private long getCurrentlyMaxMemory() {
        return
                ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() +
                        ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax();
    }
}
