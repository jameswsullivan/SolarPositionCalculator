import static java.lang.Math.*;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.TimeZone;
import java.awt.Font;

import javax.swing.SwingConstants;


public class solarcal extends JFrame {

	//Variables declaration.
	private JPanel contentPane;
	private JTextField latitudeField;
	private JTextField longitudeField;
	private JTextField timezoneField;
	private JTextField yearField;
	private JTextField monthField;
	private JTextField dayField;
	private JTextField hourField;
	private JTextField minuteField;
	private JTextArea resultArea;
	private JTextArea timeDateArea;
	public static double DTOR = 3.14/180.0;
	public static double RTOD = 180.0/3.14;
	private static boolean haveInput = false;
	
	private int Year, Month, Day, Hour, Minute, Second, inY, inMon, inD, inH, inMin;
	private int SrH, SrM, SsH, SsM;
	private double LATITUDE, LONGITUDE;
	private int deltaGMT;
	private presets presetGeo;
	private String timeZoneString;
	//End of Variables declaration.
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					solarcal frame = new solarcal();
					frame.setVisible(true);
					frame.setResizable(false);
					frame.requestFocusInWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public solarcal() throws IOException {
		presetGeo = new presets();
		presetGeo.getSettings();
		//Read preset Latitude and Longitude from settings.ini file.
		
		//Layout initialization and actionListener initialization.
		setTitle("Solar Position Calculator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 490);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel currentInfo = new JLabel("Date & Time");
		currentInfo.setFont(new Font("Arial", Font.BOLD, 14));
		JLabel latitudeLabel = new JLabel("Latitude (N+/S-)");
		JLabel longitudeLabel = new JLabel("Longitude (E+/W-)");
		JLabel timezoneLabel = new JLabel("Time Zone");
		JLabel yearlabel = new JLabel("Year");
		JLabel monthlabel = new JLabel("Month");
		JLabel daylabel = new JLabel("Day");
		JLabel hourlabel = new JLabel("Hour");
		JLabel minutelabel = new JLabel("Minute");
		
		latitudeField = new JTextField(presetGeo.presetLatitude);
		latitudeField.addFocusListener(new FocusListener (){
			public void focusGained(FocusEvent e) {
				latitudeField.setText("");
		      }
		      public void focusLost(FocusEvent e) {}
		});
		latitudeField.setColumns(10);

		longitudeField = new JTextField(presetGeo.presetLongitude);
		longitudeField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				longitudeField.setText("");
			}
			
			public void focusLost(FocusEvent e) {
				
			}
		});
		longitudeField.setColumns(10);
		
		//Get time zone information.
		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = cal.getTimeZone();
		timeZoneString = "";
		timeZoneString += timeZone.getRawOffset()/3600000;
		timezoneField = new JTextField(timeZoneString);
		timezoneField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) { timezoneField.setText(""); }
			public void focusLost(FocusEvent e) {}
		});
		timezoneField.setColumns(10);
		//End of get timezone information.
		
		yearField = new JTextField();
		yearField.setColumns(10);
		
		monthField = new JTextField();
		monthField.setColumns(10);
		
		dayField = new JTextField();
		dayField.setColumns(10);
		
		hourField = new JTextField();
		hourField.setColumns(10);
		
		minuteField = new JTextField();
		minuteField.setColumns(10);
		
		resultArea = new JTextArea();		
		timeDateArea = new JTextArea();
		timeDateArea.setFont(new Font("Calibri", Font.PLAIN, 14));
		//Layout initialization and actionListener initialization.
		
		//Real-time solar position.
		Thread clock = new Thread() {
			public void run() {
				
				
				String s="";
				if (latitudeField.getText().equals("")) latitudeField.setText(presetGeo.presetLatitude); else {s = latitudeField.getText(); LATITUDE = Double.parseDouble(s);}
				if (longitudeField.getText().equals("")) longitudeField.setText(presetGeo.presetLongitude); else {s = longitudeField.getText(); LONGITUDE = Double.parseDouble(s);}
				if (timezoneField.getText().equals("")) timezoneField.setText(timeZoneString); else {s = timezoneField.getText(); deltaGMT = Integer.parseInt(s);}
				while (!haveInput)
				{
					getCurrentTime();
					showSunPos(LATITUDE, LONGITUDE, deltaGMT, Year, Month, Day, Hour, Minute, Second);
					s = String.format("  Date: %2d/%2d/%4d  Time: %d:%d:%d\n  Sunrise: %d:%d  Sunset: %d:%d", Month, Day, Year, Hour, Minute, Second, SrH, SrM, SsH, SsM);
					timeDateArea.setText(s);
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		clock.start();
		
		//Calculate solar position based on desired info.
		JButton calculateButton = new JButton("Calculate");
		calculateButton.setHorizontalAlignment(SwingConstants.LEFT);
		calculateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String s = "";
				if ( latitudeField.getText().equals("") || longitudeField.getText().equals("") || timezoneField.getText().equals("") 
						|| yearField.getText().equals("") || monthField.getText().equals("") || dayField.getText().equals("") 
						|| hourField.getText().equals("") || minuteField.getText().equals("") ) 
					JOptionPane.showMessageDialog(null, "Please Input All Fields.");
				else 
				{
					s = latitudeField.getText();  LATITUDE = Double.parseDouble(s);
					s = longitudeField.getText();  LONGITUDE = Double.parseDouble(s);
					s = timezoneField.getText();  deltaGMT = Integer.parseInt(s);
					s = yearField.getText();  inY = Integer.parseInt(s);
					s = monthField.getText();  inMon = Integer.parseInt(s);
					s = dayField.getText();  inD = Integer.parseInt(s);
					s = hourField.getText();  inH = Integer.parseInt(s);
					s = minuteField.getText();  inMin = Integer.parseInt(s);
					haveInput = true;
					showSunPos(LATITUDE, LONGITUDE, deltaGMT, inY, inMon, inD, inH, inMin, 0);
					
					Thread clock = new Thread() {
						public void run() {
							String s = "";							
							s = latitudeField.getText(); LATITUDE = Double.parseDouble(s);
							s = longitudeField.getText(); LONGITUDE = Double.parseDouble(s);
							s = timezoneField.getText(); deltaGMT = Integer.parseInt(s);
							while (true)
							{
								getCurrentTime();
								s = String.format("  Date: %2d/%2d/%4d  Time: %d:%d:%d\n  Sunrise: %d:%d  Sunset: %d:%d", Month, Day, Year, Hour, Minute, Second, SrH, SrM, SsH, SsM);
								timeDateArea.setText(s);
								try {
									sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					};
					clock.start();
				}
			}
		});
		
		//Save today's data to file.
		JButton outputToFile = new JButton("Save Today's Data to File");
		outputToFile.setHorizontalAlignment(SwingConstants.LEFT);
		outputToFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputSunPos(LATITUDE, LONGITUDE, deltaGMT, Year, Month, Day);
			}
		});
		
		//Save desired day's data to file.
		JButton btnSaveDesiredDays = new JButton("Save Desired Day's Data to File");
		btnSaveDesiredDays.setHorizontalAlignment(SwingConstants.LEFT);
		btnSaveDesiredDays.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String s = "";				
				if ( yearField.getText().equals("") || monthField.getText().equals("") || dayField.getText().equals("") )
					JOptionPane.showMessageDialog(null, "Please Input Full Date.");
				else 
				{
					s = latitudeField.getText();  LATITUDE = Double.parseDouble(s);
					s = longitudeField.getText();  LONGITUDE = Double.parseDouble(s);
					s = timezoneField.getText();  deltaGMT = Integer.parseInt(s);
					s = yearField.getText();  inY = Integer.parseInt(s);
					s = monthField.getText();  inMon = Integer.parseInt(s);
					s = dayField.getText();  inD = Integer.parseInt(s);
					outputSunPos(LATITUDE, LONGITUDE, deltaGMT, inY, inMon, inD);
				}
			}
		});
		
		//Reset results to now.
		JButton resetButton = new JButton("Reset Results to Now");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s = "";							
				haveInput = false;
				latitudeField.setText(presetGeo.presetLatitude);
				longitudeField.setText(presetGeo.presetLongitude);
				timezoneField.setText(timeZoneString);
				yearField.setText(""); monthField.setText(""); dayField.setText(""); hourField.setText(""); minuteField.setText("");
				Thread clock = new Thread() {
					public void run() {
						while (!haveInput)
						{
							getCurrentTime();
							showSunPos(LATITUDE, LONGITUDE, deltaGMT, Year, Month, Day, Hour, Minute, Second);
							String s = "";
							s = String.format("  Date: %2d/%2d/%4d  Time: %d:%d:%d\n  Sunrise: %d:%d  Sunset: %d:%d", Month, Day, Year, Hour, Minute, Second, SrH, SrM, SsH, SsM);
							timeDateArea.setText(s);
							try {
								sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				};
				clock.start();
			}
		});
		resetButton.setHorizontalAlignment(SwingConstants.LEFT);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(resetButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnSaveDesiredDays, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(outputToFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(calculateButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(latitudeLabel)
								.addComponent(longitudeLabel)
								.addComponent(timezoneLabel)
								.addComponent(yearlabel)
								.addComponent(monthlabel)
								.addComponent(daylabel)
								.addComponent(hourlabel)
								.addComponent(minutelabel))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(latitudeField, 100, 100, Short.MAX_VALUE)
								.addComponent(hourField)
								.addComponent(dayField)
								.addComponent(monthField)
								.addComponent(yearField)
								.addComponent(timezoneField)
								.addComponent(longitudeField, 100, 100, Short.MAX_VALUE)
								.addComponent(minuteField))))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(resultArea, GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
						.addComponent(currentInfo)
						.addComponent(timeDateArea, GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(currentInfo)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(timeDateArea, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(resultArea))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(46)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(latitudeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(latitudeLabel))
							.addGap(7)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(longitudeLabel)
								.addComponent(longitudeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(timezoneLabel)
								.addComponent(timezoneField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(yearlabel)
								.addComponent(yearField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(monthlabel)
								.addComponent(monthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(daylabel)
								.addComponent(dayField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(hourlabel)
								.addComponent(hourField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(minutelabel)
								.addComponent(minuteField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addComponent(calculateButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(outputToFile)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSaveDesiredDays)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(resetButton)))
					.addGap(117))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	private void getCurrentTime()
	{
		Calendar cal = Calendar.getInstance();
		Year = cal.get(Calendar.YEAR);
		Month = cal.get(Calendar.MONTH)+1;
		Day = cal.get(Calendar.DAY_OF_MONTH);
		Hour = cal.get(Calendar.HOUR_OF_DAY);
		Minute = cal.get(Calendar.MINUTE);
		Second = cal.get(Calendar.SECOND);
	}
	
	private long getDayOfYear(int year, int month, int day)
	{
	  int feb;
	  double otherMonth;
	  otherMonth = 30.6;
	  if ( (year%100 == 0 && year%400 == 0) || year%4 == 0 ) feb = 29;
	  else feb = 28;
	  
	  if ( month == 1) return day;
	  else if ( month == 2) return (31+day);
	  else return (round((month-3)*otherMonth + 31 + feb + day));  
	}

	private double timeConvert(int hour, int minute, int second)
	{
	  return hour + minute/60.0 + second/3600.0;
	}

	private void showSunPos(double latitude, double longitude, int timezone, int year, int month, int day, int hour, int minute, int second)
	{
		double LSTM, B, EoT, TCF, LST, LHA, Declination, Elevation, Azi, Azimuth, sunrise, sunset;
		int inSrH, inSrM, inSsH, inSsM;
		String s = "";
		
		LSTM = 15 * deltaGMT;
		B = 360.0/365.0 * (getDayOfYear(year, month, day) - 81);
		EoT = 9.87*sin(2*B*DTOR) - 7.53*cos(B*DTOR) - 1.5*sin(B*DTOR);
		TCF = 4*(longitude - LSTM) + EoT;
		LST = timeConvert(hour, minute, second) + TCF/60.0;
		LHA = 15.0*(LST-12.0);
		Declination = 23.45*sin(B*DTOR);
		Elevation = asin( sin(Declination*DTOR)*sin(latitude*DTOR) + cos(Declination*DTOR)*cos(latitude*DTOR)*cos(LHA*DTOR) ) * RTOD;
		Azi = acos( (sin(Declination*DTOR)*cos(latitude*DTOR) - cos(Declination*DTOR)*sin(latitude*DTOR)*cos(LHA*DTOR))/cos(Elevation*DTOR) ) * RTOD;
		if (LST > 12) Azimuth = 360 - Azi; else Azimuth = Azi;
		sunrise = 12 - acos(-tan(latitude*DTOR)*tan(Declination*DTOR)) * RTOD / 15.0 - TCF/60.0;
		sunset = 12 + acos(-tan(latitude*DTOR)*tan(Declination*DTOR)) * RTOD / 15.0 - TCF/60.0;
		
		if (haveInput)
		{
			inSrH = (int)sunrise;
			inSrM = (int)round((sunrise-inSrH) * 60);
			inSsH = (int)sunset;
			inSsM = (int)round((sunset-inSsH) * 60);
			s = String.format("\n  Solar position based on INPUT:\n\n  Day of Year: %d\n  Latitude: %.2f"
					   + "\n  Longitude: %.2f\n  Time Zone: UTC %c %d\n  EoT: %.2f\n  Time Correction: %.2f"
					   + "\n  Declination: %.2f\n  Sunrise: %d:%d\n  Sunset: %d:%d\n\n  Local Hour Angle: %.2f\n  Elevation: %.2f"
					   + "\n  Azimuth: %.2f", getDayOfYear(year, month, day), latitude, longitude, (deltaGMT>0)?'+':'-', abs(deltaGMT), EoT, TCF, Declination, inSrH, inSrM, inSsH, inSsM, LHA, Elevation, Azimuth);
			resultArea.setText(s);
		}
		else if (!haveInput)
		{
			SrH = (int)sunrise;
			SrM = (int)round((sunrise-SrH) * 60);
			SsH = (int)sunset;
			SsM = (int)round((sunset-SsH) * 60);
			s = String.format("\n  Real time data based on CURRENT LOCAL information:\n\n  Day of Year: %d\n  Latitude: %.2f"
							   + "\n  Longitude: %.2f\n  Time Zone: UTC %c %d\n  EoT: %.2f\n  Time Correction: %.2f"
							   + "\n  Declination: %.2f\n\n  Local Hour Angle: %.2f\n  Elevation: %.2f"
							   + "\n  Azimuth: %.2f", getDayOfYear(year, month, day), latitude, longitude, (deltaGMT>0)?'+':'-', abs(deltaGMT), EoT, TCF, Declination, LHA, Elevation, Azimuth);
			resultArea.setText(s);
		}
		
	}
	
	private void outputSunPos(double latitude, double longitude, int timezone, int year, int month, int day)
	{
		double LSTM, B, EoT, TCF, LST, LHA, Declination, Elevation, Azi, Azimuth, sunrise, sunset;
		int SrH, SrM, SsH, SsM, h, min;
		String s, fileName;
		
		LSTM = 15 * deltaGMT;
		B = 360.0/365.0 * (getDayOfYear(year, month, day) - 81);
		EoT = 9.87*sin(2*B*DTOR) - 7.53*cos(B*DTOR) - 1.5*sin(B*DTOR);
		TCF = 4*(longitude - LSTM) + EoT;
		Declination = 23.45*sin(B*DTOR);
		sunrise = 12 - acos(-tan(latitude*DTOR)*tan(Declination*DTOR)) * RTOD / 15.0 - TCF/60.0;
		sunset = 12 + acos(-tan(latitude*DTOR)*tan(Declination*DTOR)) * RTOD / 15.0 - TCF/60.0;
		SrH = (int)sunrise;
		SrM = (int)round((sunrise-SrH) * 60);
		SsH = (int)sunset;
		SsM = (int)round((sunset-SsH) * 60);
		
		h = SrH; min = SrM;
		s = ""; fileName = year + "-" + month + "-" + day + "-" + "SolarPositionData.txt";
		
		try
		{
			Writer writer = new OutputStreamWriter(new FileOutputStream(new File(fileName), false), "UTF-8"); 
			BufferedWriter out = new BufferedWriter(writer);
			s = String.format("Latitude: %.2f\t\tLongitude: %.2f\t\tDate: %d/%d/%d\t\tSunrise: %d:%d\t\tSunset: %d:%d", latitude, longitude, month, day, year, SrH, SrM, SsH, SsM);
			out.write(s); out.newLine();
			s = String.format("DayOfYear: %d\t\tEoT: %.2f\t\tTime Correction: %.2f\t\tDeclination: %.2f", getDayOfYear(year, month, day), EoT, TCF, Declination);
			out.append(s); out.newLine(); out.newLine();
			s = "Time\tLHA\tEle\tAzi";
			out.append(s); out.newLine();
			
			while (h < SsH+1)
			{
				LST = timeConvert(h, min, 0) + TCF/60.0;
				LHA = 15.0*(LST-12.0);		
				Elevation = asin( sin(Declination*DTOR)*sin(latitude*DTOR) + cos(Declination*DTOR)*cos(latitude*DTOR)*cos(LHA*DTOR) ) * RTOD;
				Azi = acos( (sin(Declination*DTOR)*cos(latitude*DTOR) - cos(Declination*DTOR)*sin(latitude*DTOR)*cos(LHA*DTOR))/cos(Elevation*DTOR) ) * RTOD;
				if (LST > 12) Azimuth = 360 - Azi; else Azimuth = Azi;
				
				s = String.format("%d:%d\t%.2f\t%.2f\t%.2f", h, min, LHA, Elevation, Azimuth);
				out.append(s); out.newLine();
				if (h == SsH && min >= SsM) break;
				else if (min+5 > 60) { min = 0; h++; }
				else if (h == SsH && (min+5 > SsM) && min != SsM)  min = SsM;
				else min +=5;
			}
			out.close();
		}
		catch (IOException err)
		{
			err.printStackTrace();
		}
		
	}
}
