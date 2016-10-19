import java.io.FileNotFoundException;

///////////////////////////////////////////////////////////////////////////////
// Title:            Prog2-ImageLoop
// Files:            ImageFileNotFoundException.java
// Semester:         Fall 2016
//
// Author:           Alex McClain, gamcclain@wisc.edu
// CS Login:         gamcclain@wisc.edu
// Lecturer's Name:  Charles Fischer
///////////////////////////////////////////////////////////////////////////////

/**
 * This exception is an extension of the FileNotFoundException specific to
 * image files from the \images folder.
 * @author Alex McClain
 */
public class ImageFileNotFoundException extends FileNotFoundException {

	/**
	 * Constructs an exception with the given error message.
	 * @param errMsg Error message for this exception.
	 */
	public ImageFileNotFoundException(String errMsg) { super(errMsg); }
}