package inheritance;

import static de.vksi.c4j.Condition.ignored;
import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.vksi.c4j.ClassInvariant;
import de.vksi.c4j.Target;

public class BoatContract implements Boat {

	private Set<String> m_portExistsAt = new HashSet<String>(Arrays.asList(new String[] {"Los Angeles", "Boston", "New York"}));
	
	@Target
	private Boat target;

	@ClassInvariant
	public void classInvariant() {
	}

	@Override
	public void setDestination(String destination) {
		if (preCondition()) {
			assert m_portExistsAt.contains(destination) : "destination not reachable by boat";
		}
		if (postCondition()) {
			System.out.println(target.getDestination());
			assert target.getDestination().equals(destination) : "destination set";
		}
	}

	@Override
	public String getDestination() {
		return ignored();
	}

}
