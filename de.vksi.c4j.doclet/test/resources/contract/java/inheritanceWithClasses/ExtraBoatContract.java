package inheritanceWithClasses;

import static de.vksi.c4j.Condition.ignored;
import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;
import de.vksi.c4j.Contract;
import de.vksi.c4j.PureTarget;
import de.vksi.c4j.Target;

@Contract
public class ExtraBoatContract extends Boat {

	@Target
	private Boat target;

	@Override
	public void setDestination(String destination) {
		if (preCondition()) {
			assert true : "EXTRA BOAT CONTRACT --> PRECONDITION";
		}
		if (postCondition()) {
			assert true : "EXTRA BOAT CONTRACT --> POSTCONDITION";
		}
	}

	@Override
	@PureTarget
	public String getDestination() {
		return ignored();
	}

}
