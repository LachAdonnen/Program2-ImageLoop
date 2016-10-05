
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


/** 
 * An <tt>Image</tt> object contains the name of the file containing the image,
 * a title (possibly null),
 * and a positive integer indicating the length of time (duration) the image 
 * should be displayed in seconds.
 * 
 * <b>Do not modify anything in this file</b> (except possibly displayPicture() if you know
 * what you are doing)
 */
public class Image {
	private String fileName;  // name of file containing the image
	private String title;     // title (or caption) of the image
	private int duration;     // duration in seconds

	/**
	 * Creates a new <tt>Image</tt> object with specified file name.  
	 * The durations is initially 5; title is null string
	 * @param fileName the name of the file containing the image.
	 */
	public Image(String fileName) {
		this.fileName = fileName;
		this.duration = 5;
		this.title = "";
	}

	/**
	 * Creates a new <tt>Image</tt> object with specified file name,  
	 * durations and title.
	 * @param fileName the name of the file containing the image.
	 * @param title the title (or caption) of  the image.
	 * @param duration the duration of the image's display.
	 */
	public Image(String fileName, String title, int duration) {
		this.fileName = fileName;
		this.duration = duration;
		this.title = title;
	}
	
	/**
	 * Returns the file name associated with this image.
	 * @return the file associated with this image
	 */
	public String getFile() { 
		return fileName; 
	}
	
	/**
	 * Returns the duration for this image (in seconds).
	 * @return the duration for this image (in seconds)
	 */
	public int getDuration() { 
		return duration; 
	}
	
	/**
	 * Returns the title of this image.
	 * @return the title of this image.
	 */
	public String getTitle() { 
		return title; 
	}
	
	/**
	 * Sets the duration for this image to the one given.
	 * @param duration the new duration (in hundreths of seconds) for this photo
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
	/**
	 * Sets the title of this image.
	 * @param title the new title for this photo
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Returns the string representation of this image in the form:
	 * <p><tt><i>title</i> [<i>file_name</i> ,<i>duration</i>]</tt></p>
	 * If title is null we use:
	 * <p><tt><i>file_name</i> [<i>duration</i>]</tt></p>
	 * 
	 * @return The string representation of this photo.
	 */
	public String toString() {
	if (title.length() == 0)
			return "[" + fileName + ", "+duration + "]";
	else 
			return title + " [" + fileName + ", "+duration + "]";
	}
	
	void displayImage() throws InterruptedException{
		   String fileName = "images/"+getFile();
		   JFrame frame = new JFrame(getTitle());
		   ImageIcon icon = new ImageIcon(fileName);
		   JLabel label = new JLabel(icon);
		   frame.add(label);
		   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		   frame.pack();
		   frame.setLocation(100,100);
		   frame.setVisible(true);
		   Thread.sleep(getDuration()*1000);
		   frame.setVisible(false);
	
	}
}
