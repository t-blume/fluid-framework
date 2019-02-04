package output.implementation.sinks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.interfaces.IQuintSink;

import java.io.PrintStream;

/**
 * Created by Blume Till on 07.10.2016.
 */
public class FileTripleSink implements IQuintSink {
    private static final Logger logger = LogManager.getLogger(FileTripleSink.class.getSimpleName());

    private final PrintStream pw;
    private boolean debug;
    private int count = 0;

    public FileTripleSink(PrintStream pw, boolean debug){
        this.pw = pw;
        this.debug = debug;
    }
    @Override
    public void print(String... statement) {
        String[] prepared = prepareStatement(statement);
        if(prepared != null){
            StringBuilder sb = new StringBuilder();
            sb.append(prepared[0]);
            sb.append(" ");
            sb.append(prepared[1]);
            sb.append(" ");
            sb.append(prepared[2]);
            sb.append(" .");
            pw.println(sb);

//            count++;
//            if (count % 1000 == 0)
//                logger.info("Add Triple: %08d    \r");

        }

    }

    @Override
    public void remove(String... statement) {

    }

    @Override
    public void printComment(String comment) {
        if (debug) {
            StringBuilder sb = new StringBuilder();
            sb.append("# DEBUG ");
            sb.append(comment);
            pw.println(sb);
        }
    }

    @Override
    public String[] prepareStatement(String... statement) {
        if(statement == null || statement.length != 3)
           logger.error("Invalid statement: " + statement);
        else{
            String[] prepared = new String[statement.length];
            for (int i = 0; i < statement.length; i++){
                String tmp = statement[i].trim();
                if (!tmp.startsWith("<") && !tmp.startsWith("\""))
                    tmp = "<" + tmp;
                if (!tmp.endsWith(">") && !tmp.startsWith("\""))
                    tmp = tmp + ">";
                prepared[i] = tmp;
            }
            return prepared;
        }
        return null;
    }

    @Override
    public void close() {
        pw.close();
    }

    @Override
    public void clear() {
        //Nothing to do
    }


}
