## Solar Position Calculator

#### Background
I made this program in 2015 to generate test data for a solar tracker I was designing.
The solar tracker was an Arduino based project that tracks the sun using MPPT algorithm.

#### Instructions
- Download the `Solar.Position.Calculator.7z` file and extract the files.
- The default information displayed on the right pane is based on the given GPS coordinates and your system date & time.
- Set the GPS coordinates in the `settings.ini` file or in the program. This is usually the GPS coordinates where your solar tracker will be physically installed.
- Time Zone is automatically set based on your system's time zone.
- To do a one-time calculation, input the year, month, day, hour, and minute in **numeric format** and click `Calculate`. (As shown in screenshot.PNG.)
- `Save Today's Data to File` will use the given GPS coordinates and your system date & time to calculate your `CURRENT DAY`'s solar position data throughout the day
(in 5 minutes increment from sunrise to sunset) and save it to a `.txt` file in the same directory as the `jar` file.
- `Save Desired Day's Data to File` will calculate the given day's solar position data to file.

#### Future of This Project
- I'm in the middle of re-creating the project in C#.
- This Java project will no longer be updated.

