import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Scanner;

///////////////////////////////////////////////////////////////////////////////
// Title:            Prog2-ImageLoop
// Files:            ImageLoopEditor.java
// Semester:         Fall 2016
//
// Author:           Alex McClain, gamcclain@wisc.edu
// CS Login:         gamcclain@wisc.edu
// Lecturer's Name:  Charles Fischer
///////////////////////////////////////////////////////////////////////////////

/**
 * Main driver for the image loop interface. Accepts user input and manipulates
 * the loop of images.
 * @author Alex McClain
 */
public class ImageLoopEditor {
	// Stores the loop of images
	private static LinkedLoop<Image> imageLoop;
	
	/**
	 * Main method that initiates the user input cycle.
	 * @param args May contain an input file of commands to execute.
	 */
	public static void main(String[] args) {
		// Stores a flag used to terminate the program once finished
		boolean done = false;
		// Stores whether the input is coming from a file so it can be printed
		boolean inputFromFile = false;
		// Scanner for reading in commands
		Scanner in = null;

		try {
			// Check for an input file
			if (args.length > 1) {
				System.out.println("invalid command-line arguments");
				System.exit(1);
			}
			else if (args.length == 1) {
				File inFile = new File(args[0]);
				if (!inFile.exists() || !inFile.canRead()) {
				    throw new IOException();
				 }
				else {
					in = new Scanner(inFile);
					inputFromFile = true;
				}
			}
			else {
				in = new Scanner(System.in);
			}
		}
		catch (IOException e) { 
			System.out.println("Problem with input file!");
			System.exit(1);
		}
		
		// Stores the images in a loop structure 
		imageLoop = new LinkedLoop<Image>();
		
		// Main loop for user input
		while (!done) { 
			System.out.print("Enter command (? for help)> ");
			String programInput = "";
			if (!in.hasNextLine()) {done = true; }
			else {
				programInput = in.nextLine();
				if (inputFromFile) { System.out.println(programInput); }
				done = userInput(programInput);
			}
			
		}
		in.close();
	}
	
	/**
	 * Processes a single line of user input.
	 * @param input Both the command and arguments of the user input. 
	 * @return Flag indicating whether the program should quit.
	 */
	private static boolean userInput(String input) {
		boolean shouldQuit = false;
		
		// Piece the input into the command and argument
		char command = ' ';
		if (input.length() > 0) { command = input.charAt(0); }
		String argument = "";
		if (input.length() > 1) { argument = input.substring(1).trim(); }
		
		// Process the command using the relevant mthod
		try {
			switch (command) {
				case '?': // Display prompt options
					optionPrintCommands();
					break;
				case 's': // Save current loop to file
					optionSaveLoop(argument);
					break;
				case 'l': // Load loop file
					optionLoadLoop(argument);
					break;
				case 'd': // Display image list
					optionPrintImageList();
					break;
				case 'p': // Display single image
					optionDisplayImage();
					break;
				case 't': // Test image loop
					optionTestImageLoop();
					break;
				case 'f': // Go forward in loop
					optionMoveForward();
					break;
				case 'b': // Go backwards in loop
					optionMoveBackward();
					break;
				case 'j': // Jump through loop
					optionJumpForward(Integer.valueOf(argument));
					break;
				case 'r': // Remove current image
					optionRemoveImage();
					break;
				case 'a': // Add new image (after current)
					optionAddImageAfter(argument);
					break;
				case 'i': // Add new image (before current)
					optionAddImageBefore(argument);
					break;
				case 'c': // Search image title
					optionSearchImageTitle(argument);
					break;
				case 'u': // Update image display time
					optionUpdateDuration(Integer.valueOf(argument));
					break;
				case 'e': // Edit image title
					optionEditTitle(argument);
					break;
				case 'x': // Exit
					System.out.print("exit");
					shouldQuit = true;
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		catch (IllegalArgumentException e) { 
			System.out.println("invalid command");
		}
		catch (EmptyLoopException e) {
			System.out.println("no images");
		}
		catch (ImageFileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return shouldQuit;
	}
	
	/**
	 * Command: ?
	 * Displays the command options for the user.
	 */
	private static void optionPrintCommands() {
		System.out.println(
			"s (save)      l (load)       d (display)        p (picture)\n" +
			"f (forward)   b (backward)   j (jump)           t (test)\n" +
			"r (remove)    a (add after)  i (insert before)  e (retitle)\n" +
			"c (contains)  u (update)     x (exit)");
		return;
	}
	
	/**
	 * Command: s <filename>
	 * Saves the current loop data to the given file. This method will overwrite
	 * data if there is an existing file.
	 * @param fileName The file path where the data should be saved.
	 */
	private static void optionSaveLoop(String fileName) {
		// Attempt to open the file for writing
		PrintStream fileWriter = null;
		try {
			fileName = validateFilename(fileName);
			validateImageLoopNotEmpty();
			
			// Open the file and output stream
			File fileOut = new File(fileName);
			if (fileOut.exists()) {
				System.out.println("warning: file already exists, " +
						"will be overwritten");
			}
			fileWriter = new PrintStream(fileOut);

			// Loop through the images and write each into the file
			Iterator<Image> imageIter = imageLoop.iterator();
			while (imageIter.hasNext()) {
				Image imageToWrite = imageIter.next();
				String fileLine = imageToWrite.getFile() + " " +
						imageToWrite.getDuration() + " \"" +
						imageToWrite.getTitle() + "\"";
				fileWriter.println(fileLine);
			}
		}
		catch (EmptyLoopException e) {
			System.out.println("no images to save");
		}
		catch (IOException e) {
			System.out.println("unable to save");
		}
		finally {
			if (fileWriter != null) { fileWriter.close(); }
		}
		return;
	}
	
	/**
	 * Command: l <filename>
	 * Loads images from the given file into the loop at the current location.
	 * If the load fails, the loop is cleared to maintain data integrity.
	 * @param fileName The file path where the data is stored.
	 */
	private static void optionLoadLoop(String fileName) {
		fileName = validateFilename(fileName);
		
		// Attempt to open the file for reading
		Scanner fileReader = null;
		try {
			File fileIn = new File(fileName);
			fileReader = new Scanner(fileIn);
			// Track the number of images added from the file
			int numImagesLoaded = 0;
			
			// Read in each line of the file
			while (fileReader.hasNextLine()) {
				String fileLine = fileReader.nextLine();
				// Use a Scanner to parse the line into the individual pieces
				Scanner lineReader = new Scanner(fileLine);
				try {
					String imageFileName = lineReader.next();
					validateImageFileExists(imageFileName);
					int imageDuration = lineReader.nextInt();
					String imageTitle = "";
					if (lineReader.hasNextLine()) {
							imageTitle = removeStringQuotes(
							lineReader.nextLine().trim());
					}
					imageLoopAddAfter(new Image(imageFileName, imageTitle,
							imageDuration));
					numImagesLoaded++;
				}
				catch (ImageFileNotFoundException e) {
						System.out.println(e.getMessage());
				}
				finally { lineReader.close(); }
			}
			// Jump to the first image that was loaded
			for (int i = numImagesLoaded; i > 1; i--) { imageLoop.previous(); }
		}
		catch (IOException e) {
			System.out.println("unable to load");
			// If the load fails, clear out any leftover data
			imageLoop = new LinkedLoop<Image>();
		}
		finally {
			if (fileReader != null) { fileReader.close(); }
		}
		return;
	}

	/**
	 * Command: d
	 * Prints a list of all images currently present in the loop.
	 * @throws EmptyLoopException
	 */
	private static void optionPrintImageList() throws EmptyLoopException {
		validateImageLoopNotEmpty();
		Iterator<Image> imageIter = imageLoop.iterator();
		while (imageIter.hasNext()) {
			Image imageToWrite = imageIter.next();
			System.out.println(generateImageString(imageToWrite));
		}
		return;
	}
	
	/**
	 * Command: p
	 * Displays the current image in a new window.
	 * @throws EmptyLoopException
	 */
	private static void optionDisplayImage() throws EmptyLoopException {
		validateImageLoopNotEmpty();
		try {
			imageLoop.getCurrent().displayImage();
		}
		// If interrupted, just carry on
		catch (InterruptedException e) {}
		return;
	}
	
	/**
	 * Command: t
	 * Tests the entire loop by displaying each image in a new window one after
	 * the next.
	 * @throws EmptyLoopException
	 */
	private static void optionTestImageLoop() throws EmptyLoopException {
		validateImageLoopNotEmpty();
		Iterator<Image> imageIter = imageLoop.iterator();
		while (imageIter.hasNext()) {
			try {
				Image imageToDisplay = imageIter.next();
				imageToDisplay.displayImage();
			}
			// If interrupted, move on to next image
			catch (InterruptedException e) {}
		}
		return;
	}
	
	/**
	 * Command: f
	 * Moves the location in the loop forward one position.
	 * @throws EmptyLoopException
	 */
	private static void optionMoveForward() throws EmptyLoopException {
		validateImageLoopNotEmpty();
		imageLoop.next();
		displayImageContext();
		return;
	}
	
	/**
	 * Command: b
	 * Moves the location in the loop backwards one position.
	 * @throws EmptyLoopException
	 */
	private static void optionMoveBackward() throws EmptyLoopException {
		validateImageLoopNotEmpty();
		imageLoop.previous();
		displayImageContext();
		return;
	}
	
	/**
	 * Command: j <numToJump>
	 * Jumps the location in the loop forwards some number of positions.
	 * @param numToJump Number to skip forward in the loop.
	 * @throws EmptyLoopException
	 */
	private static void optionJumpForward(int numToJump)
			throws EmptyLoopException {
		validateImageLoopNotEmpty();
		if (numToJump >= 0) {
			for (int i = 1; i <= numToJump; i++) { imageLoop.next(); }
		}
		else {
			for (int i = -1; i >= numToJump; i--) { imageLoop.previous(); }
		}
		displayImageContext();
		return;
	}
	
	/**
	 * Command: r
	 * Removes the current image from the loop.
	 * @throws EmptyLoopException
	 */
	private static void optionRemoveImage() throws EmptyLoopException {
		imageLoop.removeCurrent();
		displayImageContext();
		return;
	}
	
	/**
	 * a <filename>
	 * Adds an image to the loop in the position after the current image.
	 * @param fileName File name from the images folder.
	 * @throws ImageFileNotFoundException
	 */
	private static void optionAddImageAfter(String fileName)
			throws ImageFileNotFoundException {
		fileName = validateFilename(fileName);
		validateImageFileExists(fileName);
		imageLoopAddAfter(new Image(fileName));
		displayImageContext();
		return;
	}
	
	/**
	 * Helper method to add an image in the position after the current position.
	 * @param newImage The Image object to be added to the loop.
	 */
	private static void imageLoopAddAfter(Image newImage) {
		imageLoop.next();
		imageLoop.add(newImage);
		return;
	}

	/**
	 * Command: i <filename>
	 * Adds an image to the loop in the position before the current image.
	 * @param fileName File name from the images folder.
	 * @throws ImageFileNotFoundException
	 */
	private static void optionAddImageBefore(String fileName) 
			throws ImageFileNotFoundException {
		fileName = validateFilename(fileName);
		validateImageFileExists(fileName);
		imageLoop.add(new Image(fileName));
		displayImageContext();
		return;
	}
		
	/**
	 * Command: c <searchStr>
	 * Searches through the loop for the next image that contains the search
	 * string within the title. This search begins with the image after the
	 * current position so that successive searches do not return the same image
	 * over and over.
	 * @param searchStr String to search for in image title's.
	 * @throws EmptyLoopException
	 */
	private static void optionSearchImageTitle(String searchStr)
			throws EmptyLoopException {
		validateImageLoopNotEmpty();
		if (searchStr.length() == 0) { return; } // Nothing to search for
		searchStr = removeStringQuotes(searchStr);
		
		int imageJumpCount = 0;
		boolean imageFound = false;
		imageLoop.next(); // Advance one so we don't stick on current image
		Iterator<Image> imageIter = imageLoop.iterator();
		while(imageIter.hasNext() && !imageFound) {
			Image testImage = imageIter.next();
			if (testImage.getTitle().contains(searchStr)) { imageFound = true; }
			else { imageJumpCount++; }
		}
		if (imageFound) { // Jump forward to the relevant image
			optionJumpForward(imageJumpCount);
		}
		else { // Step back one so we are on the same image as when we started
			imageLoop.previous();
			System.out.println("not found");
		}
		return;
	}
	
	/**
	 * Command: u <time>
	 * Updates the display duration for the current image.
	 * @param time Number of seconds that the image should be displayed.
	 * @throws EmptyLoopException
	 */
	private static void optionUpdateDuration(int time) 
			throws EmptyLoopException {
		if (time < 0) { throw new IllegalArgumentException(); }
		imageLoop.getCurrent().setDuration(time);
		displayImageContext();
		return;
	}
	
	/**
	 * Command: e <title>
	 * Edits the title of the current image to be the given string. The title
	 * can be quoted and the quotes will be removed.
	 * @param newTitle New title for the image.
	 * @throws EmptyLoopException
	 */
	private static void optionEditTitle(String newTitle)
			throws EmptyLoopException {
		newTitle = removeStringQuotes(newTitle);
		imageLoop.getCurrent().setTitle(newTitle);
		displayImageContext();
		return;
	}
	
	/**
	 * Displays the information for the current image along with the previous
	 * and next images. 
	 */
	private static void displayImageContext() {
		int loopSize = imageLoop.size();
		
		try {
			// Print previous image
			if (loopSize > 2) {
				imageLoop.previous();
				System.out.println("    " + generateImageString(
						imageLoop.getCurrent()));
				imageLoop.next();
			}
			
			// Print current image
			System.out.println("--> " + generateImageString(
					imageLoop.getCurrent()) + " <--");
			
			// Print next image
			if (loopSize > 1) {
				imageLoop.next();
				System.out.println("    " + generateImageString(
						imageLoop.getCurrent()));
				imageLoop.previous();
			}
		}
		// If there are no nodes, just don't display anything
		catch (EmptyLoopException e) {}
		return;
	}
	
	/**
	 * Generates a display string for the given Image that includes the title,
	 * duration, and file name.
	 * @param imageToWrite Image object whose data should be returned.
	 * @return A formatted string with the Image's information in the form:
	 * Title [FileName, Duration]
	 */
	private static String generateImageString(Image imageToWrite) {
		String returnString = "";
		if (imageToWrite != null) {
			returnString += imageToWrite.getTitle();
			if (returnString.length() > 0) { returnString += " "; }
			returnString += "[" + imageToWrite.getFile() + ", ";
			returnString += imageToWrite.getDuration() + "]";
		}
		return returnString;
	}
	
	/**
	 * Given an input string, removes leading and trailing quotations if they
	 * are present.
	 * @param argument Input string that may or may not be quoted.
	 * @return The original string with leading and trailing quotes removed if
	 * both were present.
	 */
	private static String removeStringQuotes(String argument) {
		String returnString = argument;
		if (argument.length() > 1 &&
			argument.startsWith("\"") &&
			argument.endsWith("\"")) {
			returnString = argument.substring(1, argument.length() - 1);
		}
		return returnString;
	}
	
	/**
	 * Checks a file name to be sure there are no illegal characters and returns
	 * the file name with backslashes correctly formatted so as to prevent
	 * issues with escape characters.
	 * @param fileName The file name or path to be validated.
	 * @return The same file path but with every \ replaced by \\.
	 */
	/*
	 * Validate using a regex to ensure there are no illegal characters.
	 * String can only contain:
	 * 	a-z, A-Z, 0-9, _, ., /, \, -
	 * 	The entire string must only use those characters ( ^[regex]$ )
	 *  Need to have one or more of the characters ( +$ )
	 *  Java uses \ as an escape character, so need \\
	 *  Regex also uses \ as an escape character, so actually need \\\\
	 */
	private static String validateFilename(String fileName) {
		if (!fileName.matches("^[a-zA-Z0-9_./\\\\-]+$")) {
			throw new IllegalArgumentException();
		}
		// Sanitize the file name to remove escape characters
		return fileName.replaceAll("\\\\", "\\\\");
	}

	/**
	 * Verifies that the image loop is not currently empty.
	 * @throws EmptyLoopException
	 */
	private static void validateImageLoopNotEmpty() throws EmptyLoopException {
		if (imageLoop.isEmpty()) { throw new EmptyLoopException(); }
		return;
	}
	
	/**
	 * Verifies that the given file name is present in the images folder.
	 * @param fileName The file name to search for.
	 * @throws ImageFileNotFoundException
	 */
	private static void validateImageFileExists(String fileName)
			throws ImageFileNotFoundException {
		File testFile = new File("images\\" + fileName);
		if (!testFile.exists()) {
			String errMsg = "warning: " + fileName + " is not in images folder";
			throw new ImageFileNotFoundException(errMsg);
		}
		return;
	}
}
