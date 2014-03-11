package hw4;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MazeMDP<S> implements MarkovDecisionProcess<S> {

	Set<S> set = new LinkedHashSet<S>();

	@Override
	public Set<S> states() {
		// TODO Auto-generated method stub
		return this.set;
	}

	@Override
	public void addState(S s) {
		// TODO Auto-generated method stub
		this.set.add(s);
	}
	
	

}
