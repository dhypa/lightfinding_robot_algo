import java.awt.image.BufferedImage;

import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;

public class DarkSearcher extends Searcher {
	public DarkSearcher(SwiftBotAPI API,ExecutionLog newLog){
		super(API, newLog);
	}

	@Override
	public void pathFind() throws ObstructionFoundException{

		searchTimer.resetTimer();  	
		while (searchTimer.getTimeElapsed()<5000) {
			BufferedImage image = swiftBot.takeStill(ImageSize.SQUARE_1080x1080); 

			double forwardLuminance = scanForward(image);
			double rightLuminance = scanRight(image);
			double leftLuminance = scanLeft(image);

			// checks if the values beat threshold, if so the swiftbot will approach light
			boolean darkFound= isFound(forwardLuminance,rightLuminance,leftLuminance);

			// checks if the values are the lowest detected this execution
			checkIfHighestValuesFound(forwardLuminance,rightLuminance,leftLuminance);

			if (darkFound) {
				luminanceResult result = findApexLuminance(leftLuminance,forwardLuminance,rightLuminance);
				approach(result.direction);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		probe(); 

	}

	@Override
	protected void checkIfHighestValuesFound(double forwardLuminance, double rightLuminance, double leftLuminance) {
		if (leftLuminance<log.getLowestLuminanceLeft()) { log.setLowestLuminanceLeft(leftLuminance); }
		if (forwardLuminance<log.getLowestLuminanceForward()) { log.setLowestLuminanceForward(forwardLuminance); }
		if (rightLuminance<log.getLowestLuminanceRight()) { log.setLowestLuminanceRight(rightLuminance); }
		
	}
	
	@Override
	protected boolean isFound(double forwardLuminance, double rightLuminance, double leftLuminance) {
		boolean darkFound=false;
		if (leftLuminance<threshold) { log.incrementDarkDetectionCount(); darkFound = true; }
		if (forwardLuminance<threshold) { log.incrementDarkDetectionCount(); darkFound = true;}
		if (rightLuminance<threshold) { log.incrementDarkDetectionCount(); darkFound = true;}
		return darkFound;
	}


	@Override
	protected Searcher.luminanceResult findApexLuminance(double leftLuminance, double forwardLuminance, double rightLuminance) {
		String direction;
		double minLuminance = Math.min(Math.min(leftLuminance, rightLuminance), forwardLuminance);
		if (minLuminance == leftLuminance) {
			direction = "left";
		} 
		else if (minLuminance == rightLuminance) {
			direction = "right";
		} 
		else {
			direction = "forward";
		}
		
		return new Searcher.luminanceResult(minLuminance,direction);
	}



	

}
