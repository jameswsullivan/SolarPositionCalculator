import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

public class presets {
	
	//Resource Declaration
	Reader reader;
	BufferedReader read;
	FileInputStream input;
	Writer writer;
	BufferedWriter write;
	FileOutputStream output;
	
	File settingsFile = new File("settings.ini");
	public String presetLatitude, presetLongitude;
	String s = "";
	//End of Resource Declaration
	
	//Get preset settings from file "settings.ini"
	public void getSettings() throws IOException
	{
		input = new FileInputStream(settingsFile);
		reader = new InputStreamReader(input);
		read = new BufferedReader(reader);
		s = read.readLine();
		while (s != null)
		{
			if (s.equals("[Latitude]")) presetLatitude = read.readLine();
			if (s.equals("[Longitude]")) presetLongitude = read.readLine();
			s = read.readLine();
		}
		read.close();
	}
	//End of getting preset from settings.ini file
	
	public boolean fileExists() { if (settingsFile.isFile()) return true;	else return false; } //Check if settings.ini exists.
}
