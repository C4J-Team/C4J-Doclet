package inheritanceWithClasses;

import de.vksi.c4j.ContractReference;


@ContractReference(CarContract.class)
public interface Car {

	public void setDestination(String destination);

	public String getDestination();
}
