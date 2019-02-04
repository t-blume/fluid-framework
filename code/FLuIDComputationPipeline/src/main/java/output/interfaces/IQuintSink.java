package output.interfaces;


/**
 * Created by Blume Till on 07.10.2016.
 */

public interface IQuintSink {

    //Function Interface magic
    @FunctionalInterface
    interface Sink {
        IQuintSink getSink();
    }

    /**
     *
     * @param statement
     */
    void print(String... statement);

    /**
     *
     * @param statement
     */
    void remove(String... statement);

    /**
     *
     * @param comment
     */
    void printComment(String comment);

    /**
     *
     * @param statement
     * @return
     */
    String[] prepareStatement(String... statement);

    /**
     *
     */
    void close();

    /**
     *
     */
    void clear();


}
