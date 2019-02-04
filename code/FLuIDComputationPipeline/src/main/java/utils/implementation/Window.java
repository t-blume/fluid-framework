package utils.implementation;

import common.interfaces.IInstanceElement;
import common.interfaces.IQuint;
import common.interfaces.IResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.interfaces.IElementCache;
import utils.interfaces.IElementCacheListener;

import java.util.*;

public class Window implements IElementCache<IInstanceElement> {
    private static final Logger logger = LogManager.getLogger(Window.class.getSimpleName());
    private int loggingInterval = 1000;

    //optimize data structure: HashMaps tend to get messy in java after too many deletions
    private int deletionCounter = 0;


    private Map<IResource, IInstanceElement> elements;
    private Queue<IResource> queue;
    private int capacity;
    private List<IElementCacheListener<IInstanceElement>> listeners;

    //maximum reached size
    private int maxSize = 0;


    public Window() {
        this(Integer.MAX_VALUE);
    }

    public Window(int capacity) {
        elements = new HashMap<>();
        listeners = new ArrayList<>();
        queue = new ArrayDeque<>();
        this.capacity = capacity;
    }

    @Override
    public boolean contains(IInstanceElement i) {
        return elements.containsKey(i.getLocator());
    }

    @Override
    public boolean contains(IResource res) {
        return elements.containsKey(res);
    }

    @Override
    public IInstanceElement get(IResource res) {
        return elements.get(res);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public void add(IInstanceElement i) {
        if (capacity <= 0)//deactivated window
            return;
        if (!contains(i)) {
            int currentSize = size();
            maxSize = Math.max(maxSize, currentSize);
            if (capacity == Integer.MAX_VALUE && maxSize % loggingInterval == 0)
                logger.info("Adding to IC: " + currentSize + " / " + capacity);

            // If the capacity is full -> remove first entry added
            if (currentSize == capacity) {
                if (capacity == Integer.MAX_VALUE) {
                    logger.info("Instance Cache limit " + maxSize + " reached - Gold standard failed !");
                    System.exit(-1);
                }
                removeLast();
            }

            elements.put(i.getLocator(), i);
            queue.add(i.getLocator());
        }
    }

    @Override
    public void flush() {
        while (size() != 0)
            removeLast();
    }

    @Override
    public void registerCacheListener(IElementCacheListener<IInstanceElement> listener) {
        listeners.add(listener);
    }

    @Override
    public void close() {
        int instanceCount = elements.size();
        int minDS = Integer.MAX_VALUE;
        int maxDS = Integer.MIN_VALUE;
        int sumDS = 0;
        int minDSGreaterThanOne = Integer.MAX_VALUE;
        int maxDSGreaterThanOne = Integer.MIN_VALUE;
        int sumDSGreaterThanOne = 0;

        Set<IInstanceElement> moreThanOneDS = new HashSet<>();
        for (IInstanceElement instanceElement : elements.values()) {
            Set<IResource> contexts = new HashSet<>();
            for (IQuint q : instanceElement.getOutgoingQuints())
                contexts.add(q.getContext());

            sumDS += contexts.size();
            if(contexts.size() > maxDS)
                maxDS = contexts.size();
            if(contexts.size() < minDS)
                minDS = contexts.size();

            if(contexts.size() > 1) {
                moreThanOneDS.add(instanceElement);
                sumDSGreaterThanOne += contexts.size();
                if(contexts.size() > maxDSGreaterThanOne)
                    maxDSGreaterThanOne = contexts.size();
                if(contexts.size() < minDSGreaterThanOne)
                    minDSGreaterThanOne = contexts.size();
            }
        }
        double avgDS = (double) sumDS / (double) instanceCount;
        double avgDSGreaterThanOne = (double) sumDSGreaterThanOne / (double) moreThanOneDS.size();

        double varianceSum = 0.0;
        double varianceSumGreaterThanOne = 0.0;
		for (IInstanceElement instanceElement : elements.values()) {
            Set<IResource> contexts = new HashSet<>();
            for (IQuint q : instanceElement.getOutgoingQuints())
                contexts.add(q.getContext());

			double tmpK = (double) contexts.size();

            if(tmpK > 1)
                varianceSumGreaterThanOne += Math.pow(tmpK - avgDSGreaterThanOne, 2);

			varianceSum += Math.pow(tmpK - avgDS, 2);
		}
		double variance = varianceSum / (double) instanceCount;
        double varianceGreaterThanOne = varianceSumGreaterThanOne / (double) moreThanOneDS.size();

        logger.info("----- " + this.getClass().getSimpleName() + " Statistics Overview -----");
        logger.info("Instances Statistics: ");
        logger.info("Count;\t" + instanceCount);
        logger.info("Sum;\t" + sumDS);
        logger.info("Min;\t" + minDS);
        logger.info("Max;\t" + maxDS);
        logger.info("Avg;\t" + avgDS);
		logger.info("Var;\t" + variance);
        logger.info("-------------------------");
        logger.info("Count > 1;\t" + moreThanOneDS.size());
        logger.info("Percentage:" + (double) moreThanOneDS.size() / (double) instanceCount * 100 + "%");
        logger.info("Sum;\t" + sumDSGreaterThanOne);
        logger.info("Min;\t" + minDSGreaterThanOne);
        logger.info("Max;\t" + maxDSGreaterThanOne);
        logger.info("Avg;\t" + avgDSGreaterThanOne);
        logger.info("Var;\t" + varianceGreaterThanOne);
//        for(IInstanceElement instanceElement : moreThanOneDS)
//            logger.info(instanceElement);
        logger.info("-------------------------");
        flush();
        logger.debug("Window is emptied, no more instances left.");
        for (IElementCacheListener<IInstanceElement> l : listeners)
            l.finished();
    }

    @Override
    public IInstanceElement removeNext() {
        IResource first = queue.poll();
        return get(first);
    }


    private void removeLast() {
        IResource first = queue.poll();
        IInstanceElement el = get(first);
        if (capacity != Integer.MAX_VALUE) { //do not remove flushed instance when constructing gold standard
            elements.remove(first);
            deletionCounter++;
        }

        int currentSize = size();
        maxSize = Math.max(maxSize, currentSize);
        if (capacity == Integer.MAX_VALUE && maxSize % loggingInterval == 0)
            logger.info("Removing from IC: " + currentSize + " / " + capacity);

        //TODO: empirically estimate
        if (10 * capacity > 0 && deletionCounter > 10 * capacity) { //integer overflow
            long start = System.currentTimeMillis();
            logger.debug("Cleaning window...");
            Map<IResource, IInstanceElement> newElements = new HashMap<>();
            for (Map.Entry<IResource, IInstanceElement> entry : elements.entrySet())
                newElements.put(entry.getKey(), entry.getValue());

            elements = newElements;
            logger.debug("...done cleaning in " + (System.currentTimeMillis() - start) + " ms");
            deletionCounter = 0;
        }
        notifyListeners(el);
    }

    private void notifyListeners(IInstanceElement el) {
        for (IElementCacheListener<IInstanceElement> l : listeners) {

            l.elementFlushed(el);
        }

    }
}
