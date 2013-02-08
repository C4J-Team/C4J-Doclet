package inheritance;

public class CarImpl extends BoatImpl implements Car {
	
	@Override
	public void setDestination(String destination) {
		super.setDestination(destination);
	}
	
}
