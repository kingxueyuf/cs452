
import java.util.ArrayList;

public class yufan_MarkovDecisionProcessImpl<State> {

	ArrayList<State> list = new ArrayList<State>();

	public ArrayList states() {
		return this.list;
	}

	public void addState(State s) {
		this.list.add(s);
	}

}
