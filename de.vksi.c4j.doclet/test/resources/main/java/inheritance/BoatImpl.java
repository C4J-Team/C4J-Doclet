package inheritance;

public class BoatImpl implements Boat {

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
