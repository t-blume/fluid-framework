package implementation.utils.datastructs; /**
 *
 */



import implementation.eval.Comparator;
import implementation.eval.ComparatorGeneric;
import implementation.eval.ComparatorInterface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * @author Mathias
 */
public class EvalValueArray {
    public class EvalValueEntry{
        EvalValueEntry(double precision, double recall, double f1){
            this.precision = precision;
            this.recall = recall;
            this.f1 = f1;
        }
        private double precision;
        private double recall;
        private double f1;


        private void addPrecision(double precision){
            this.precision += precision;
        }
        private void addRecall(double recall){
            this.recall += recall;
        }
        private void addF1(double f1){
            this.f1 += f1;
        }
        private void add(EvalValueEntry value){
            addPrecision(value.precision);
            addRecall(value.recall);
            addF1(value.f1);
        }
        private void apply(Function<Double, Double> function){
            precision = function.apply(precision);
            recall = function.apply(recall);
            f1 = function.apply(f1);
        }


        @Override
        public String toString() {
            DecimalFormat df = new DecimalFormat("0.00000");
            return df.format(precision) + ";" + df.format(recall) + ";" + df.format(f1);
        }

        public double getPrecision() {
            return precision;
        }

        public double getRecall() {
            return recall;
        }

        public double getF1() {
            return f1;
        }
    }

    ArrayList<EvalValueEntry> values;

    public EvalValueArray() {
        values = new ArrayList<>();
    }

    public boolean addResult(ComparatorInterface comp) {

        return values.add(new EvalValueEntry(comp.precision(), comp.recall(), comp.f1()));
    }

    public boolean addResultTest(ComparatorGeneric comp) {

        return values.add(new EvalValueEntry(comp.precision(), comp.recall(), comp.f1()));
    }


    public EvalValueEntry average() {
        EvalValueEntry valueEntry = new EvalValueEntry(0, 0, 0);
        if (size() == 0)
            return valueEntry;
         else {
            Iterator<EvalValueEntry> iter = values.iterator();
            while (iter.hasNext())
                valueEntry.add(iter.next());

            valueEntry.apply(X -> X/= values.size());
            return valueEntry;
        }
    }

    public EvalValueEntry median() {
        EvalValueEntry valueEntry = new EvalValueEntry(0, 0, 0);
        if (size() == 0)
            return valueEntry;
         else {
            List<Double> precs = new ArrayList<>();
            List<Double> recs = new ArrayList<>();
            List<Double> f1s = new ArrayList<>();

            for(EvalValueEntry value : values){
                precs.add(value.precision);
                recs.add(value.recall);
                f1s.add(value.f1);
            }
            Collections.sort(precs);
            Collections.sort(recs);
            Collections.sort(f1s);
            valueEntry.precision = precs.get(Math.round(values.size() / 2));
            valueEntry.recall = recs.get(Math.round(values.size() / 2));
            valueEntry.f1 = f1s.get(Math.round(values.size() / 2));
            return valueEntry;
        }
    }

    public EvalValueEntry stddev() {
        EvalValueEntry valueEntry = new EvalValueEntry(0, 0, 0);
        if (size() == 0)
            return valueEntry;
        else{
            Iterator<EvalValueEntry> iter = values.iterator();
            EvalValueEntry ave = average();

            while (iter.hasNext()) {
                EvalValueEntry next = iter.next();
                next.precision -= ave.precision;
                next.recall -= ave.recall;
                next.f1 -= ave.f1;

                next.apply((X -> Math.pow(X, 2) ));
                valueEntry.add(next);
            }
            valueEntry.apply(X -> Math.sqrt(X/(size() - 1)));
            return valueEntry;
        }
    }

    public EvalValueEntry min() {
        EvalValueEntry valueEntry = new EvalValueEntry(-1, -1, -1);
        if (size() == 0)
            return valueEntry;
        else {
            valueEntry = new EvalValueEntry(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            for(EvalValueEntry value : values) {
                if(value.precision < valueEntry.precision)
                    valueEntry.precision = value.precision;

                if(value.recall < valueEntry.recall)
                    valueEntry.recall = value.recall;

                if(value.f1 < valueEntry.f1)
                    valueEntry.f1 = value.f1;
            }
            return valueEntry;
        }
    }

    public EvalValueEntry max() {
        EvalValueEntry valueEntry = new EvalValueEntry(-1, -1, -1);
        if (size() == 0)
            return valueEntry;
        else {
            valueEntry = new EvalValueEntry(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
            for(EvalValueEntry value : values) {
                if(value.precision > valueEntry.precision)
                    valueEntry.precision = value.precision;

                if(value.recall > valueEntry.recall)
                    valueEntry.recall = value.recall;

                if(value.f1 > valueEntry.f1)
                    valueEntry.f1 = value.f1;
            }
            return valueEntry;
        }
    }


    public int size() {
        return values.size();
    }

    public void toFile(String _filename) {
        try {
            File file = new File(_filename);
            boolean exists = file.exists();
            if (!exists) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            // Create file
            FileWriter fstream = new FileWriter(_filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(size() + ";Precision;Recall;F1\r\n");
            out.write("Average; " + average() + "\r\n");
            out.write("Median; " + median() + "\r\n");
            out.write("Min; " + min() + "\r\n");
            out.write("Max; " + max() + "\r\n");
            out.write("StdDev; " + stddev() + "\r\n");
            out.write("\r\n");
            // Close the output stream
            out.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage() + " " + _filename);
        }
    }

}
