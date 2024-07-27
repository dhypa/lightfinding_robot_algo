import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;

public class ExecutionLog {
	private static ExecutionLog instance;

	private double highestLuminanceLeft;
	private double highestLuminanceForward;
	private double highestLuminanceRight;
	private int lightDetectedCount;

	private double lowestLuminanceLeft;
	private double lowestLuminanceForward;
	private double lowestLuminanceRight;
	private int darkDetectedCount;

	private ArrayList<String> travelLog;
	private double distanceTravelled;
	private long durationOfExecution;
	private String terminateReason;
	private String searchingFor;
	private String location;
	private String timeOfExecution; 

	private ExecutionLog() {
		// initialize all variables here as empty
		highestLuminanceLeft = -1;
		highestLuminanceForward= -1;
		highestLuminanceRight=-1;
		lightDetectedCount=0;
		lowestLuminanceLeft=-1;
		lowestLuminanceForward=-1;
		lowestLuminanceRight=-1;
		darkDetectedCount=0;
		this.travelLog = new ArrayList<>();
		distanceTravelled=0;
		durationOfExecution=0;
		terminateReason="";
		searchingFor="";
		location="";
		timeOfExecution="";
	}

	public static ExecutionLog getInstance() {
		if (instance == null) {
			instance = new ExecutionLog();
		}
		return instance;
	}

	/*         setters and getters from this point        */
	
	public void setSearchingFor(String searchingFor) {
		this.searchingFor=searchingFor;
	}
	public String getSearchingFor() {
		return this.searchingFor;
	}

	public void setLocation(String location) {
		this.location=location;
	}

	public String getLocation() {
		return this.location;
	}

	public String getTimeOfExecution() {
		return this.timeOfExecution;
	}
	public void setTimeOfExecution(String timeOfExecution) {
		this.timeOfExecution= timeOfExecution;
	}

	public void incrementLightDetectionCount() {
		this.lightDetectedCount++;
	}
	public int getLightDetectedCount() {
		return this.lightDetectedCount;
	}
	public void setHighestLuminanceLeft(double luminance) {
		this.highestLuminanceLeft=luminance;
	}
	public void setHighestLuminanceForward(double luminance) {
		this.highestLuminanceForward=luminance;
	}
	public void setHighestLuminanceRight(double luminance) {
		this.highestLuminanceRight=luminance;
	}
	public double getHighestLuminanceLeft() {
		return this.highestLuminanceLeft;
	}
	public double getHighestLuminanceRight() {
		return this.highestLuminanceRight;
	}
	public double getHighestLuminanceForward() {
		return this.highestLuminanceForward;
	}

	public void incrementDarkDetectionCount() {
		this.darkDetectedCount++;
	}
	public int getDarkDetectionCount() {
		return this.darkDetectedCount;
	}

	public void setLowestLuminanceLeft(double luminance) {
		this.lowestLuminanceLeft=luminance;
	}
	public void setLowestLuminanceForward(double luminance) {
		this.lowestLuminanceForward=luminance;
	}
	public void setLowestLuminanceRight(double luminance) {
		this.lowestLuminanceRight=luminance;
	}
	public double getLowestLuminanceLeft() {
		return this.lowestLuminanceLeft;
	}
	public double getLowestLuminanceRight() {
		return this.lowestLuminanceRight;
	}
	public double getLowestLuminanceForward() {
		return this.lowestLuminanceForward;
	}
	public void setTerminateReason(String reason) {
		this.terminateReason=reason;
	}
	public String getTerminateReason() {
		return this.terminateReason;
	}

	public void setDurationOfExecution(long durationOfExecution) {
		this.durationOfExecution=durationOfExecution;
	}
	public long getDurationOfExecution() {
		return this.durationOfExecution;
	}

	public void appendTravelLog(String action) {
		if (action.equals("moves forward 3.8 cm")) this.distanceTravelled+=3.8 ;
		this.travelLog.add(action);
	}
	public ArrayList<String> getTravelLog(){
		return this.travelLog;
	}
	
	
	
	/*   Non getters and setters    */
	
	// reduces size of the travel log and gives it a human readable format for when toString() is called on it
	public void condenseTravelLog() {

		if (this.travelLog.isEmpty()||this.travelLog.size()==1) {
			return;
		}
		Set<String> movementSet = Set.of("turn right", "turn left", "rotate right", "rotate left");
		ArrayList<String> condensedLog = new ArrayList<>();
		String currentMovement;
		double distance=0;
		for (int i = 0;i < this.travelLog.size();i++) {
			currentMovement= this.travelLog.get(i);
			if (movementSet.contains(currentMovement)) { 
				condensedLog.add(currentMovement);
			}
			else {
				distance+=3.8;
				try {
					if (this.travelLog.get(i+1).equals("moves forward 3.8 cm")) {
						continue;
					}
					else {
						condensedLog.add("moves forward "+distance+" cm");
						distance = 0;
					}
				}
				catch (IndexOutOfBoundsException e) {
					condensedLog.add("moves forward "+distance+" cm");
				}
			}
		}
		this.travelLog=condensedLog;
	}

	// prints the execution log
	public void display() {
		if (this.searchingFor.equals("light")) {
			System.out.println(
					"""
					
					##########################################################################
					######################         EXECUTION LOG      ########################
					""");
					System.out.println("highestLuminanceLeft = "+this.highestLuminanceLeft);
					System.out.println("highestLuminanceForward = "+this.highestLuminanceForward);
					System.out.println("highestLuminanceRight = "+this.highestLuminanceRight );
					System.out.println("lightDetectedCount = "+this.lightDetectedCount );
		}

		else {
			System.out.println("lowestLuminanceLeft = "+this.highestLuminanceLeft);
			System.out.println("lowestLuminanceForward = "+this.highestLuminanceForward);
			System.out.println("lowestLuminanceRight = "+this.highestLuminanceRight );
			System.out.println("darkDetectedCount = "+this.lightDetectedCount );
		}

		System.out.println("distanceTravelled = "+this.distanceTravelled+"cm");

		System.out.println("durationOfExecution = "+this.durationOfExecution );
		System.out.println("terminateReason = "+this.terminateReason );
		System.out.println("searchingFor = "+this.searchingFor );
		System.out.println("location = "+this.location );
		System.out.println("timeOfExecution = "+this.timeOfExecution+" seconds");


		System.out.println("\n"+"travel log = "+this.travelLog.toString());   //travelLog is printed at the end as it may be very long

	}
	
	// self explanatory function
	public void saveToFile() {
		LocalTime currentTime = LocalTime.now();
		int hours = currentTime.getHour();
		int minutes = currentTime.getMinute();
		int seconds = currentTime.getSecond();
		String fileName = ("execution_log"+this.getTimeOfExecution()+"_"+hours+minutes+"_"+seconds+".txt");
		try {
			File file = new File(fileName);
			if (!file.createNewFile()) {
				System.out.println("Please wait");
				try {
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
					System.out.println("Creating the save file has failed!");
					System.exit(2);
				}
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Creating the save file has failed!");
			System.exit(2);

		}

		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
			br.write("ExecutionLog for Search for Light");
			br.newLine();
			br.newLine();
			
			// only saves variables related to light or to dark
			if (this.searchingFor.equals("light")) {
				br.write("highestLuminanceLeft = "+this.highestLuminanceLeft);
				br.newLine();
				br.write("highestLuminanceForward = "+this.highestLuminanceForward);
				br.newLine();
				br.write("highestLuminanceRight = "+this.highestLuminanceRight );
				br.newLine();
				br.write("lightDetectedCount = "+this.lightDetectedCount );
				br.newLine();
			}
			else {
				br.write("lowestLuminanceLeft = "+this.highestLuminanceLeft);
				br.newLine();
				br.write("lowestLuminanceForward = "+this.highestLuminanceForward);
				br.newLine();
				br.write("lowestLuminanceRight = "+this.highestLuminanceRight );
				br.newLine();
				br.write("darkDetectedCount = "+this.lightDetectedCount );
				br.newLine();
			}
			br.write("distanceTravelled = "+this.distanceTravelled+"cm" );
			br.newLine();
			br.write("durationOfExecution = "+this.durationOfExecution+" seconds");
			br.newLine();
			br.write("terminateReason = "+this.terminateReason );
			br.newLine();
			br.write("searchingFor = "+this.searchingFor );
			br.newLine();
			br.write("location = "+this.location );
			br.newLine();
			br.write("timeOfExecution = "+this.timeOfExecution );
			br.newLine();
			br.newLine();
			br.write("\n"+"travelLog = "+this.travelLog.toString());   //travelLog is written at the end of the file as it may be very long

			br.close();
			System.out.println("\nExecution log has been saved as: "+"["+fileName+"]");
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Saving the file has failed!");
			System.exit(2);
		}

	}
}