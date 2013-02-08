package inheritance;

import de.vksi.c4j.ContractReference;

@ContractReference(BoatContract.class)
public interface Boat {
	
	public void setDestination(String destination);

	public String getDestination();
}
