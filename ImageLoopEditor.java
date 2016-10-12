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
// Lab Section:      LAB ###
///////////////////////////////////////////////////////////////////////////////

public class ImageLoopEditor {
	private static LinkedLoop<Image> imageLoop;
	
	public static void main(String[] args) {
		// Stores a flag used to terminate the program once finished
		boolean done = false;
		// Used to read in user input
		Scanner stdin = new Scanner(System.in);
		// Stores the images in a loop structure 
		imageLoop = new LinkedLoop<Image>();
		// Main loop for user input
		while (!done) { 
			System.out.print("enter command (? for help)>");
			done = userInput(stdin.nextLine());
		}
		stdin.close();
	}
	
	private static boolean userInput(String input) {
		boolean shouldQuit = false;
		
		char command = input.charAt(0);
		String argument = "";
		if (input.length() > 1) { argument = input.substring(1).trim(); }
		
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
	
	private static void optionPrintCommands() {
		System.out.println(
			"s (save)      l (load)       d (display)        p (picture)\n" +
			"f (forward)   b (backward)   j (jump)           t (test)\n" +
			"r (remove)    a (add after)  i (insert before)  e (retitle)\n" +
			"c (contains)  u (update)     x (exit)");
		return;
	}
	
	// s filename
	private static void optionSaveLoop(String fileName) {
		validateFilename(fileName);
		
		// Attempt to open the file for writing
		PrintStream fileWriter = null;
		try {
			File fileOut = new File(fileName);
			if (fileOut.exists()) {
				System.out.println("warning: file already exists, " +
						"will be overwritten");
			}
			fileWriter = new PrintStream(fileOut);

			Iterator<Image> imageIter = imageLoop.iterator();
			while (imageIter.hasNext()) {
				Image imageToWrite = imageIter.next();
				String fileLine = imageToWrite.getFile() + " " +
						imageToWrite.getDuration() + " \"" +
						imageToWrite.getTitle() + "\"";
				fileWriter.println(fileLine);
			}
		}
		catch (IOException e) {
			System.out.println("unable to save");
		}
		finally {
			if (fileWriter != null) { fileWriter.close(); }
		}
		return;
	}
	
	// l filename
	private static void optionLoadLoop(String fileName) {
		validateFilename(fileName);
		
		// Attempt to open the file for reading
		Scanner fileReader = null;
		try {
			File fileIn = new File(fileName);
			fileReader = new Scanner(fileIn);
			int numImagesLoaded = 0;
			
			while (fileReader.hasNextLine()) {
				String fileLine = fileReader.nextLine();
				Scanner lineReader = new Scanner(fileLine);
				try {
					String imageFileName = lineReader.next();
					validateImageFileExists(imageFileName);
					int imageDuration = lineReader.nextInt();
					String imageTitle = removeStringQuotes(
							lineReader.nextLine());
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

	// d
	private static void optionPrintImageList() {
		Iterator<Image> imageIter = imageLoop.iterator();
		while (imageIter.hasNext()) {
			Image imageToWrite = imageIter.next();
			System.out.println(generateImageString(imageToWrite));
		}
		return;
	}
	
	// p
	private static void optionDisplayImage() throws EmptyLoopException {
		validateImageLoopNotEmpty();
		try {
			imageLoop.getCurrent().displayImage();
		}
		// If interrupted, just carry on
		catch (InterruptedException e) {}
		return;
	}
	
	// t
	private static void optionTestImageLoop() {
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
	
	// f
	private static void optionMoveForward() throws EmptyLoopException {
		validateImageLoopNotEmpty();
		imageLoop.next();
		displayImageContext();
		return;
	}
	
	// b
	private static void optionMoveBackward() throws EmptyLoopException {
		validateImageLoopNotEmpty();
		imageLoop.previous();
		displayImageContext();
		return;
	}
	
	// j numToJump
	private static void optionJumpForward(int numToJump)
			throws EmptyLoopException {
		validatePositiveInt(numToJump);
		validateImageLoopNotEmpty();
		for (int i = 1; i <= numToJump; i++) { imageLoop.next(); }
		displayImageContext();
		return;
	}
	
	// r
	private static void optionRemoveImage() throws EmptyLoopException {
		imageLoop.removeCurrent();
		displayImageContext();
		return;
	}
	
	// a filename
	// Should never throw EmptyLoopException since we will only attempt to
	// display the context if we successfully added a new node
	private static void optionAddImageAfter(String fileName)
			throws ImageFileNotFoundException, EmptyLoopException {
		validateFilename(fileName);
		validateImageFileExists(fileName);
		imageLoopAddAfter(new Image(fileName));
		displayImageContext();
		return;
	}
	
	private static void imageLoopAddAfter(Image newImage) {
		imageLoop.next();
		imageLoop.add(newImage);
		return;
	}

	// i filename
	// Should never throw EmptyLoopException since we will only attempt to
	// display the context if we successfully added a new node
	private static void optionAddImageBefore(String fileName) 
			throws ImageFileNotFoundException, EmptyLoopException {
		validateFilename(fileName);
		validateImageFileExists(fileName);
		imageLoop.add(new Image(fileName));
		displayImageContext();
		return;
	}
		
	// c searchStr
	private static void optionSearchImageTitle(String searchStr) {
		searchStr = removeStringQuotes(searchStr);
		return;
	}
	
	// u time
	private static void optionUpdateDuration(int time) 
			throws EmptyLoopException {
		validatePositiveInt(time);
		imageLoop.getCurrent().setDuration(time);
		return;
	}
	
	// e title
	private static void optionEditTitle(String newTitle)
			throws EmptyLoopException {
		newTitle = removeStringQuotes(newTitle);
		imageLoop.getCurrent().setTitle(newTitle);
		return;
	}
	
	private static void displayImageContext() throws EmptyLoopException {
		int loopSize = imageLoop.size();
		
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
		}
		return;
	}
	
	private static String generateImageString(Image imageToWrite) {
		String returnString = "";
		if (imageToWrite != null) {
			returnString += imageToWrite.getTitle() + " [";
			returnString += imageToWrite.getFile() + ", ";
			returnString += imageToWrite.getDuration() + "]";
		}
		return returnString;
	}
	
	private static String removeStringQuotes(String argument) {
		String returnString = argument;
		if (argument.startsWith("\"") &&
			argument.endsWith("\"")) {
			returnString = argument.substring(1, argument.length() - 2);
		}
		return returnString;
	}
	
	/*
	 * Validate using a regex to ensure there are no illegal characters.
	 * String can only contain:
	 * 	a-z, A-Z, 0-9, _, ., /, -
	 * 	The entire string must only use those characters ( ^[regex]$ )
	 *  Need to have one or more of the characters ( +$ )
	 */
	private static void validateFilename(String fileName) {
		if (!fileName.matches("^[a-zA-Z0-9_./-]+$")) {
			throw new IllegalArgumentException();
		}
		return;
	}

	private static void validatePositiveInt(int numToCheck) {
		if (numToCheck < 0) { throw new IllegalArgumentException(); }
	}

	private static void validateImageLoopNotEmpty() throws EmptyLoopException {
		if (imageLoop.isEmpty()) { throw new EmptyLoopException(); }
		return;
	}
	
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
