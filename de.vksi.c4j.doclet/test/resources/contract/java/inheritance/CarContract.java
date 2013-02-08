package inheritance;

import static de.vksi.c4j.Condition.ignored;
import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.vksi.c4j.ClassInvariant;
import de.vksi.c4j.Target;

public class CarContract implements Car {

	private Set<String> m_roadsExistsBetween = new HashSet<String>(Arrays.asList(new String[] {"Los Angeles", "Denver", "Atlanta", "Boston", "New York"}));
	
	@Target
	private Car target;

	@ClassInvariant
	public void classInvariant() {
	}

	@Override
	public void setDestination(String destination) {
		if (preCondition()) {
			assert m_roadsExistsBetween.contains(destination) : "destination reachable by car";
		}
		if (postCondition()) {
			assert target.getDestination().equals(destination) : "destination set";
		}
	}

	@Override
	public String getDestination() {
		return ignored();
	}

}
