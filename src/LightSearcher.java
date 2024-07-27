import java.awt.image.BufferedImage;

import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;

public class LightSearcher extends Searcher {
	public LightSearcher(SwiftBotAPI API, ExecutionLog newLog) {
		super(API, newLog);
	}

	@Override
	public void pathFind() throws ObstructionFoundException {
	
		searchTimer.resetTimer();
		while (searchTimer.getTimeElapsed() < 5000) {
			BufferedImage image = swiftBot.takeStill(ImageSize.SQUARE_1080x1080);

			double forwardLuminance = scanForward(image);
			double rightLuminance = scanRight(image);
			double leftLuminance = scanLeft(image);

			// checks if the values beat threshold, if so the swiftbot will approach light
			boolean lightFound = isFound(forwardLuminance, rightLuminance, leftLuminance);

			// checks if the values are the highest detected this execution
			checkIfHighestValuesFound(forwardLuminance, rightLuminance, leftLuminance);

			if (lightFound) {
				luminanceResult result = findApexLuminance(leftLuminance, forwardLuminance, rightLuminance);
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
		if (leftLuminance > log.getHighestLuminanceLeft()) {
			log.setHighestLuminanceLeft(leftLuminance);
		}
		if (forwardLuminance > log.getHighestLuminanceForward()) {
			log.setHighestLuminanceForward(forwardLuminance);
		}
		if (rightLuminance > log.getHighestLuminanceRight()) {
			log.setHighestLuminanceRight(rightLuminance);
		}
	}

	protected boolean isFound(double forwardLuminance, double rightLuminance, double leftLuminance) {
		boolean lightFound = false;
		if (leftLuminance > threshold) {
			log.incrementLightDetectionCount();
			lightFound = true;
		}
		if (forwardLuminance > threshold) {
			log.incrementLightDetectionCount();
			lightFound = true;
		}
		if (rightLuminance > threshold) {
			log.incrementLightDetectionCount();
			lightFound = true;
		}
		return lightFound;
	}

	@Override
	protected Searcher.luminanceResult findApexLuminance(double leftLuminance, double forwardLuminance, double rightLuminance) {
		String direction;
		double maxLuminance = Math.max(Math.max(leftLuminance, rightLuminance), forwardLuminance);
		if (maxLuminance == leftLuminance) {
			direction = "left";
		} else if (maxLuminance == rightLuminance) {
			direction = "right";
		} else {
			direction = "forward";
		}

		return new Searcher.luminanceResult(maxLuminance, direction);
	}



}
