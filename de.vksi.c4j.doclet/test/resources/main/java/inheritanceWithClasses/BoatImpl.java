package inheritanceWithClasses;

public class BoatImpl extends Boat {

	private String dest;
	
	@Override
	public void setDestination(String destination) {
		dest = destination;
	}

	@Override
	public String getDestination() {
		return dest;
	}

}
