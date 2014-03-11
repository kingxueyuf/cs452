

import java.util.Set;

public interface MarkovDecisionProcess<S> {

	Set<S> states();

	void addState(S s);

}
