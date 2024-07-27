import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;

abstract class Searcher {
	protected double threshold;
	protected static SwiftBotAPI swiftBot;
	protected static ExecutionLog log;
	protected boolean terminate;
	protected static TimeKeeper searchTimer;
	
	protected Searcher(SwiftBotAPI API, ExecutionLog newLog) {
		swiftBot=API;
		calculateThreshold();
		log=newLog;
		terminate=false;
		searchTimer = new TimeKeeper();
	}

	
	protected double getThreshold() {
		return this.threshold;
	}
	
	// works out average luminance of enviroment
	protected void calculateThreshold(){
		BufferedImage image = swiftBot.takeStill(ImageSize.SQUARE_1080x1080);
		for (int x = 0;x<=1079;x++) {	
			for (int y=0;y<= 1079;y++) {
				this.threshold+=luminanceConversion(image.getRGB(x,y));
			}
		}
		this.threshold/=1166400;
	}

	protected static double lineariseRGB(double x) {
		if (x<=0.04045) {
			return x/12.92;
		}
		else {
			return Math.pow((x+0.055)/1.055,2.4);
		}
	}
	
	// params: 
	// RGB - rbg 24 bit value 
	//
	// return value:
	// luminance value derived from RGB value
	protected static double luminanceConversion(int RGB) {
		double r = (RGB & 0xff0000) >> 16;
		double g = (RGB & 0xff00) >> 8;
		double b = RGB & 0xff;
		r= r/255;
		g= g/255;
		b= b/255;
		return (0.2126 * lineariseRGB(r) + 0.7152 * lineariseRGB(g) + 0.0722 * lineariseRGB(b));
	}
	
	// scan methods are responsible for getting luminance values from images
	// params:
	// image - reference to buffered image object,
	protected static double scanLeft(BufferedImage image) {
		double luminance = 0;
		// x = 1 is the beginning coordinate of the left section of the image
		for (int x = 0; x <= 359; ++x) {
			for (int y = 0; y <= 1079; ++y) {
				luminance+= luminanceConversion(image.getRGB(x,y));
			}
		}
		return luminance/388800;
	}
	protected static double scanForward(BufferedImage image) {
		double luminance = 0;
		// x = 361 is the beginning coordinate of the centre section of the image
		for (int x = 360;x<= 719;x++) {
			for (int y = 0; y <=1079;y++) {
				luminance+=luminanceConversion(image.getRGB(x,y));
			}
		}
		return luminance/388800;
	}
	protected static double scanRight(BufferedImage image) {
		double luminance = 0;
		// x = 721 is the beginning coordinate	 of the right section of the image
		for (int x = 720;x<=1079;x++) {
			for (int y=0; y<= 1079; y++) {
				luminance+=luminanceConversion(image.getRGB(x,y));
			}
		}
		return luminance/388800;
	}
	
	// asserts there is no object in front of swiftbot
	protected static void checkForObstruction() throws ObstructionFoundException{
		int red[] = {255,0,0};
		double distanceFromObject=swiftBot.useUltrasound();
		if (distanceFromObject<50){
			swiftBot.fillUnderlights(red);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) { }
			
			swiftBot.disableUnderlights();
			
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! OBSTRUCTION PRESENT !!!!!!!!!!!!!!!!!!!!!!!!!!!\n"
					+ "PROGRAM WILL TERMINATE IN 10 SECONDS UNLESS PATH IS CLEARED\n");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.out.println("Error waiting for obstruction to be removed!");
			}
			distanceFromObject = swiftBot.useUltrasound();

			if (distanceFromObject<50) {
				String terminationMessage= "Program terminated due to object present "+distanceFromObject+"cm away.";
				log.setTerminateReason(terminationMessage);
				throw new ObstructionFoundException(terminationMessage);
			}
			else {
				System.out.println("Continuing...");
			}
		}
	}
	
	// used to make swiftbot approach a certain direction
	protected static void approach(String direction) throws ObstructionFoundException {
		switch (direction) {
			case "left"-> {
				turnTowards(direction);
				moveForward();
			}
			case "forward"-> {
				moveForward();
			}
			case "right"-> {
				turnTowards(direction);
				moveForward();
			}
		}
	
	}
	
	// swiftbot rotates a certain direction
	protected static void turnTowards(String direction) {
		if (direction.equals("right")) {
			// turns right
			swiftBot.move(100,-100,115);
			log.appendTravelLog("turn right");
		}
		else {
			//turns left
			swiftBot.move(-100,100,115);
			log.appendTravelLog("turn left");
		}
	}
	
	// swiftbot moves forward
	protected static void moveForward() throws ObstructionFoundException{
		checkForObstruction();
		int greenRGB[] = {0,0,255};
		swiftBot.fillUnderlights(greenRGB);
		swiftBot.move(30,30,500);
		swiftBot.disableUnderlights();
		log.appendTravelLog("moves forward 3.8 cm");
	}

	// chooses left or right randomly, and then turns swiftbot 90 degrees towards that direction
	protected static void probe() {
		int randomInteger= ThreadLocalRandom.current().nextInt(1, 3); // random integer in range 1-2
		if  (randomInteger == 1){
			swiftBot.move(-30,30,1965); // rotates left

			log.appendTravelLog("rotate right");
		}
		else {
			swiftBot.move(28,-28,1965);; // rotates right

			log.appendTravelLog("rotate left");
		}
	}
	
	// used to store direction and luminance value in the same variable
	public static class luminanceResult {
		double luminance;
		String direction;

		public luminanceResult(double luminance, String direction) {
			this.luminance = luminance;
			this.direction = direction;
		}
	}
	
	protected abstract void pathFind() throws ObstructionFoundException;
	
	//sees if luminance values beat threshold value
	protected abstract boolean isFound(double forwardLuminance, double rightLuminance,double leftLuminance);
	
	//sees if luminance values are the highest/lowest recorded in the current execution session
	protected abstract void checkIfHighestValuesFound(double forwardLuminance, double rightLuminance,double leftLuminance);
	
	protected abstract luminanceResult findApexLuminance(double leftLuminance, double forwardLuminance, double rightLuminance);
	
}



































