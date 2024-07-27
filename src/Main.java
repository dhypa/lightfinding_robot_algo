
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import swiftbot.Button;
import swiftbot.SwiftBotAPI;

public class Main {
	private static SwiftBotAPI swiftBot;
	private static final ExecutionLog log = ExecutionLog.getInstance();
	private static boolean terminate=false,exit=false,run=false; //these are flag variables

	public static void main(String[] args) { 
		swiftBot = new SwiftBotAPI();
		assignButtons();
		TimeKeeper executionDurationTimer = new TimeKeeper();
		
		ExecutionLog.getInstance();
		System.out.println(
				"""
				
                **********************
                PUSH BUTTON 'X' TO EXIT AT ANY POINT
                PUSH BUTTON 'A' TO BEGIN
                **********************
                """);
		
		for (;;) {
			
			// busy waiting for button press from user
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			
			
			if (run) {  // button 'A' pushed
				swiftBot.disableButton(Button.A); // button 'A' has no further use
				
				getPreExecutionInformationFromUser(); // used to set mode and location
				
				Searcher searcher = initaliseSearcher(log.getSearchingFor()); 
				executionDurationTimer.resetTimer();  
				setTimeOfExecution();

				while (run) {
					try {
						searcher.pathFind();
					}	
					catch (ObstructionFoundException e) {
						System.out.println(e.getMessage()+"\n");
						terminate=true;
						break;
					}
				}
			}
			if (terminate) { 
				terminateProgram(executionDurationTimer);
			}
		}
	}

	
	 // Gets user input on whether to search for light or dark (mode)
	 // Gets location of execution from user 	
	private static void getPreExecutionInformationFromUser() {
		String mode;
	
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter the swiftbot's current location");
		log.setLocation(sc.nextLine());

		System.out.println("Would you like to search for light or dark?");

		while (true) {
			mode=sc.nextLine().toLowerCase();
			if (exit) {
				sc.close();
				return;
			}
			
			if (mode.equals("light")) {
				log.setSearchingFor(mode);
				System.out.println("Searching for "+mode+" now....");
				break;
			}
			else if (mode.equals("dark")) {

				log.setSearchingFor(mode);
				System.out.println("Searching for "+mode+" now....");
				break;
			}
			else {
				System.out.println("\nPlease provide a valid input");
			}
		}
		sc.close();
	}

	// finihes pre-termination tasks:
	// writing to execution log
	// saving executionlog to secondary memory
	// displaying executionlog to user
	//
	// And finally, terminating the program
	private static void terminateProgram(TimeKeeper executionDurationTimer) {
		log.setDurationOfExecution(executionDurationTimer.getTimeElapsed()/1000);
		log.condenseTravelLog();
		executionDurationTimer.close();
		swiftBot.disableAllButtons();
		assignExitButtons();

		System.out.println("Would you like to view the execution log?");
		System.out.println("Push Y to view. Push X to to terminate immediatly.");

		while (!exit) { 
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// this loop simply waits for user input, there is no use taking action if sleep does not work
			} 
		}
		log.saveToFile();
		System.exit(1);
	}

/**
 * Used to get an instance of a relevant searcher object
 * 
 * param mode - whether program is to search for light or dark
 * return value - initialised searcher class
 */
	private static Searcher initaliseSearcher(String mode) {
		if (mode.equals("light")) {
			return new LightSearcher(swiftBot,log);
		}
		else {
			return new DarkSearcher(swiftBot,log);
		}
	}


	/**
	 * Assigns functions to buttons that will run on-press.
	 * These buttons are assigned at the start of the program.
	 * Allows the user to start the program and to termination of the program.
	 */
	private static void assignButtons() {
		swiftBot.enableButton(Button.X, () -> { // used to end program
			terminate = true;
			run = false;
			log.setTerminateReason("user exit");
		});
		
		swiftBot.enableButton(Button.A, () -> run = true); // used to begin program
	}
	
	/**
	 * Assigns functions to buttons that will run on-press
	 * Used to fully exit the program and whether ExecutionLog instance should be displayed.
	 */
	private static void assignExitButtons() {
		swiftBot.enableButton(Button.X, () -> exit=true);
		swiftBot.enableButton(Button.Y, () -> {
			log.display();
			exit=true;
		});
	}

	/**
	 * Gets current time, formats it and saves it to execution log instance. 
	 */
	private static void setTimeOfExecution() {
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		String time = dateTime.format(dateFormat);
		log.setTimeOfExecution(time);
	}
}

