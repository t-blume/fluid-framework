package implementation.eval; /**
 * 
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Mathias+
 * @editor Till Blume
 *
 */
public class Comparator implements ComparatorInterface {

	protected Set<String> gold;
	protected Set<String> eval;

	private double precision = Double.NaN;
	private double recall = Double.NaN;

	private boolean isFinal = false;

	public Comparator() {
		this.gold = new TreeSet<>();
		this.eval = new TreeSet<>();
	}

	public Comparator(Set<String> gold, Set<String> eval) {
		this.gold = gold;
		this.eval = eval;
	}


	public void addSubsetToGold(Set<String> subsetGold){
		gold.addAll(subsetGold);
		isFinal = false;
	}
	public void addSubsetToEval(Set<String> subsetEval){
		eval.addAll(subsetEval);
		isFinal = false;
	}

	public Set<String> getWrong(){
		Set<String> wrong = new HashSet<>();
		eval.forEach(X -> {
			if(!gold.contains(X))
				wrong.add(X);
		});
		return wrong;
	}
	public Set<String> getMssing(){
		Set<String> missing = new HashSet<>();
		gold.forEach(X -> {
			if(!eval.contains(X))
				missing.add(X);
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


	public  double calculate(Set<String> base, Set<String> var){
		if (base.size() == 0) {
			if (var.size() == 0) //both empty, then its 1
				return 1.0;
			else
				return 0.0;	//only second is empty, then its 0
		} else {
			Iterator<String> baseIter = base.iterator();
			int count = 0;
			while (baseIter.hasNext()) {
				if (var.contains(baseIter.next()))
					count++;	//count as match

			}
			return 1.0 * count / base.size();
		}
	}
}
