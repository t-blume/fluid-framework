package output.implementation;

import common.IQuint;
import common.IResource;
import common.interfaces.ISchemaElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.implementation.sinks.FileTripleSink;
import output.interfaces.IElementListener;
import output.interfaces.IElementStore;
import output.interfaces.IQuintSink;
import processing.computation.implementation.schema.ComplexSchemaElement;
import processing.computation.implementation.schema.ObjectCluster;
import processing.computation.implementation.schema.PropertyCluster;
import processing.computation.implementation.schema.PropertyObjectCluster;
import utils.implementation.FLuIDPOJO;
import utils.implementation.Helper;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class FileElementStore implements IElementStore<ISchemaElement> {
    private static final Logger logger = LogManager.getLogger(FileElementStore.class.getSimpleName());

    private String outputFolder;
    private String schemaName;
    private boolean isClosed = false;
    private IQuintSink fileQuintSink;

    private int OC_written = 0;
    private int PC_written = 0;
    private int POC_written = 0;
    private int CSE_written = 0;

    public FileElementStore(String outputFolder , String schemaName, boolean zip) throws IOException {
        this.outputFolder = outputFolder;
        this.schemaName = schemaName;
        PrintStream outputStream;
        if (zip) {
            outputStream = new PrintStream(new BufferedOutputStream(new GZIPOutputStream(
                    new FileOutputStream(Helper.createFile(outputFolder + File.separator + schemaName + ".nt.gz")))));
        } else
            outputStream = new PrintStream(Helper.createFile(outputFolder + File.separator + schemaName + ".nt"));

        fileQuintSink = new FileTripleSink(outputStream, logger.isDebugEnabled());
    }

    @Override
    public boolean contains(IResource locator) {
        logger.warn("Method \"contains\" unsupported for " + getClass().getSimpleName() + "!");
        return false;
    }

    @Override
    public ISchemaElement getSchemaElement(IResource elementLocator, Class<? extends ISchemaElement> schemaElementType) {
        logger.warn("Method \"getSchemaElement\" unsupported for " + getClass().getSimpleName() + "!");
        return null;
    }

    @Override
    public boolean removeElement(IResource elementLocator, Class<? extends ISchemaElement> elementType) {
        logger.warn("Method \"removeElement\" unsupported for " + getClass().getSimpleName() + "!");
        return false;
    }

    @Override
    public List<ISchemaElement> removeLast(int n) {
        logger.warn("Method \"removeLast\" unsupported for " + getClass().getSimpleName() + "!");
        return null;
    }

    @Override
    public void add(ISchemaElement element) {
        List<IQuint> quints = FLuIDPOJO.exportSchemaElement(element);
        for (IQuint quint : quints)
            fileQuintSink.print(quint.getSubject().toN3(), quint.getPredicate().toN3(), quint.getObject().toN3());


        if(element instanceof ComplexSchemaElement)
            CSE_written++;
        else if (element instanceof PropertyObjectCluster)
            POC_written++;
        else if (element instanceof PropertyCluster)
            PC_written++;
        else if (element instanceof ObjectCluster)
            OC_written++;
    }

    @Override
    public int size() {
        return (CSE_written + POC_written + PC_written + OC_written);
    }

    @Override
    public void flush() {
        logger.warn("Method \"flush\" unsupported for " + getClass().getSimpleName() + "!");

    }

    @Override
    public void close() {
        logger.debug("Closing " +  getClass().getSimpleName() + "..");
        isClosed = true;
        fileQuintSink.close();
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void registerCacheListener(IElementListener<ISchemaElement> listener) {
        logger.warn("No listeners supported for " + getClass().getSimpleName() + "!");
    }

    @Override
    public void elementEmitted(ISchemaElement el) {
        System.out.println(el);
    }

    @Override
    public void finished(IElementStore<ISchemaElement> emitter) {
        int total = emitter.size();
        logger.info("Bulk dumping remaining " + total + " SEs to disk (" + outputFolder + File.separator + schemaName + ")");
        Collection<ISchemaElement> schemaElements = emitter.removeLast(emitter.size());
        int i = 0;
        for (ISchemaElement schemaElement : schemaElements) {
            List<IQuint> quints = FLuIDPOJO.exportSchemaElement(schemaElement);
            for (IQuint quint : quints)
                fileQuintSink.print(quint.getSubject().toN3(), quint.getPredicate().toN3(), quint.getObject().toN3());

            i++;
            if (i % 10000 == 0) {
                logger.info("Progress: " + i + "/" + total);
                logger.info("Progress: " + ((int) (((double) i / (double) total) * 100.0)) + "%");
            }
        }
    }
}
