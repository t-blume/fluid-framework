package implementation.eval; /**
 *
 */

import java.util.*;

/**
 * @author Mathias+
 * @editor Till Blume
 *
 */
public class ComparatorGeneric implements ComparatorInterface {

    protected Collection gold;
    protected Collection eval;

    private double precision = Double.NaN;
    private double recall = Double.NaN;

    private boolean isFinal = false;

    public ComparatorGeneric() {
        this.gold = new TreeSet<Collection>();
        this.eval = new TreeSet<Collection>();
    }

    public ComparatorGeneric(Collection gold, Collection eval) {
        this.gold = gold;
        this.eval = eval;
    }


    public void addSubsetToGold(Collection subsetGold){
        gold.addAll(subsetGold);
        isFinal = false;
    }
    public void addSubsetToEval(Collection subsetEval){
        eval.addAll(subsetEval);
        isFinal = false;
    }

    public Collection getWrong(){
        Collection wrong = new HashSet<>();
        eval.forEach(A -> {
            if(!gold.contains(A))
                wrong.add(A);
        });
        return wrong;
    }
    public Collection getMssing(){
        Collection missing = new HashSet<>();
        gold.forEach(A -> {
            if(!eval.contains(A))
                missing.add(A);
        });
        return missing;
    }


    public double precision(){
        if(isFinal && !Double.isNaN(precision))
            return precision;
        precision = calculate(eval, gold);
        isFinal = true;
        return precision;
    }

    public double recall(){
        if(isFinal && !Double.isNaN(recall))
            return recall;
        recall = calculate(gold, eval);
        isFinal = true;
        return recall;
    }

    public double f1(){
        if(precision() == 0 && recall() == 0)
            return 0.0;

        return 2.0 * ((precision() * recall())/(precision() + recall()));
    }


    public double calculate(Collection base, Collection var){
        if (base.size() == 0) {
            if (var.size() == 0) //both empty, then its 1
                return 1.0;
            else
                return 0.0;	//only second is empty, then its 0
        } else {
            Iterator baseIter = base.iterator();
            int count = 0;
            while (baseIter.hasNext()) {
                if (var.contains(baseIter.next()))
                    count++;	//count as match

            }
            return 1.0 * count / base.size();
        }
    }
}
