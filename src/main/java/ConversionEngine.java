import java.util.regex.*;
import java.io.*;
import java.text.*;
import java.sql.*;
import org.apache.commons.lang3.StringUtils;

public class ConversionEngine {

	private static String dashSpace;
	private static String dashsequence;

	private static Pattern pattern;
	private static Matcher matcher;
	private static Matcher matcher2;
	private static Matcher matcher3;

	private static int endspace;
	private static boolean found;
	public static int colcount;
	public static String[] ColumnNamesFormatted;
	public static String[] ColumnNames;
	public static int[] ColStartArray;
	public static int[] ColEndArray;
	private static BufferedWriter out;

	private static String url, user_id, password, datatypename;

	public static void IsolateDashes(String location) {

		// Isolate the second line of the script results text file
		// and identify all instances of a space followed by a dash (" -"
		// Based on the positions of the space-dash, we can determine
		// how to correctly parse each line of the file.

		try {

			File scriptIn = new File(location);
			FileReader fr = new FileReader(scriptIn);
			BufferedReader br = new BufferedReader(fr);
			
			for (int ix = 0; ix < 1; ix++) // read the first line and do nothing
			{
				br.readLine();
			}

						
			dashsequence = br.readLine(); // read the line of dashes
			dashSpace = " -";

			pattern = Pattern.compile(dashSpace);
			matcher = pattern.matcher(dashsequence);
			matcher2 = pattern.matcher(dashsequence);
			matcher3 = pattern.matcher(dashsequence);

			// set the endspace variable, which is used to determine if the script results
			// contain an empty space at the end of each line.
			endspace = 0;
			if (dashsequence.endsWith(" ")) 			
			endspace = 1;			
			else endspace = 0;
		}

		catch (FileNotFoundException fnfe) {
			Conversion.createMessage("ERROR:  ",
					"Unable to locate script results file " + location);
		} catch (IOException ioe) {
			Conversion.createMessage("ERROR:  " + ioe, "");
		}

	}

	public static int setColumnCount() {

		// set the colcount variable, which represents the number of columns in
		// the table as determined by the space-dash instances

		colcount = 1;
		while (matcher.find()) {
			colcount++;
			found = true;
		}

		if (!found) {
			Conversion
					.createMessage("ERROR:  ",
							"Problem locating column separators in line two.  Please check file format.");
		}
		return colcount;
	}

	public static void createFieldStartArray() {

		ColStartArray = new int[colcount]; // create an array of integers

		int count1;
		count1 = 0;

		ColStartArray[count1] = 0;
		count1++;

		while (count1 < colcount) {
			while (matcher2.find()) {
				ColStartArray[count1] = matcher2.start();
				// System.out.println("Column number " + (count1 + 1) +
				// " starts at position " + ColStartArray[count1]);
				count1++;
				found = true;
			}
		}
	}

	public static void createFieldEndArray() {

		ColEndArray = new int[colcount]; // create an array of integers

		int count;
		count = 0;

		while (count < colcount - 1) {
			while (matcher3.find()) {
				ColEndArray[count] = matcher3.end() - 1;
				// System.out.println("Column number " + (count + 1) +
				// " ends at position " + ColEndArray[count]);
				count++;
				found = true;
			}
		}

		// Set the end position of the last column
		// This is where the space at the end of each line is removed.
		if (endspace == 1) 
            ColEndArray[colcount - 1] = dashsequence.length() - 1;
		else ColEndArray[colcount - 1] = dashsequence.length();		     
	}

	public static String[] getColumnNames(String location2) {

		try {
			ColumnNames = new String[colcount]; // create an array of Strings
			File scriptIn = new File(location2);

			FileReader fr = new FileReader(scriptIn);
			BufferedReader br2 = new BufferedReader(fr);

			String thisLine;
			thisLine = br2.readLine();
			thisLine.trim();

			// Get every column name except the last
			for (int i = 0; i < colcount - 1; i++) {
				ColumnNames[i] = thisLine.substring(ColStartArray[i],
						ColEndArray[i]);
				ColumnNames[i] = ColumnNames[i].trim().replace(" ", "_");
			}
			// The last column name will have its final character truncated
			// unless we do this
			ColumnNames[colcount - 1] = thisLine.substring(
					ColStartArray[colcount - 1], ColEndArray[colcount - 1]);
			ColumnNames[colcount - 1] = ColumnNames[colcount - 1].trim()
					.replace(" ", "_");

			ColumnNamesFormatted = new String[colcount];

			for (int u = 0; u <= colcount - 1; u++) {
				if (ColumnNames[u].length() <= 27) {
					int padding;
					padding = 27 - ColumnNames[u].length();
					ColumnNamesFormatted[u] = StringUtils.repeat(" ", padding)
							+ ColumnNames[u];
				} else if (ColumnNames[u].length() > 26) {
					ColumnNamesFormatted[u] = ColumnNames[u].substring(0, 25)
							+ "...";
					System.out.println("Formatted: " + ColumnNamesFormatted[u]);
				} else
					ColumnNamesFormatted[u] = ColumnNames[u];

			}

		} catch (FileNotFoundException fnfe) {
			// System.out.println("Cannot locate input file! "+fnfe.getMessage());
			Conversion.createMessage("ERROR:  " + fnfe, "");
			// System.exit(0);
		} catch (IOException e) {
			Conversion.createMessage("ERROR:  " + e, "");
		}
		return ColumnNames;
	}

	// SQLOutput starts here
	public static void SQLOutputEngine(String location2, String servername,
			final String dbname, String login, String pword,
			String newtablename, String dbtablename) throws SQLException {

		try {

			DateFormat fmt = DateFormat.getTimeInstance(DateFormat.SHORT);
			java.util.Date today = new java.util.Date();
			String userid = new String();			
			com.sun.security.auth.module.NTSystem NTSystem = new
			com.sun.security.auth.module.NTSystem();
			//System.out.println(NTSystem.getName());
			userid = (NTSystem.getName());
			
			String the_date = fmt.format(today);
			the_date = the_date.replaceAll(" ", "");
			the_date = the_date.replaceAll(":", "");
			
			String inputFileLocationName = location2;
			String outputDirName = "c:\\Users\\" + userid + "\\My Documents\\";
			String outputFileName = (newtablename + "_" + the_date + ".txt");

			File scriptIn = new File(location2);
			File scriptOut = new File(outputDirName, outputFileName);

			FileReader fr = new FileReader(scriptIn);
			BufferedReader br2 = new BufferedReader(fr);

			// Create the database connection
			// Using Sybase JCONNECT 5.5
			Class.forName("com.sybase.jdbc2.jdbc.SybDriver");
			url = ("jdbc:sybase:Tds:" + servername + ":5000/" + dbname + "?CHARSET=iso_1");
			user_id = login;
			password = pword;
			Connection conn = DriverManager.getConnection(url, user_id,
					password);
			Statement stmt = conn.createStatement();
			String query = ("select * from " + dbtablename + " where 1 = 0");
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			if (rsmd.getColumnCount() != colcount) {
				Conversion
						.createMessage(
								"ERROR:  ",
								"Table mismatch.  Column count in file does not equal column count in database table.  Stopping conversion.");
				br2.close();
				out.close();
			}
			
			String intString = "int";
			String tinyintString = "tinyint";
			String charString = "char";
			String varcharString = "varchar";
			String smallintString = "smallint";
			String datetimeString = "datetime";
			String floatString = "double precision";
			String textString = "text";
			String decimalString = "decimal";
			String imageString = "image";
			String realString = "real";
			
			for (int ix = 0; ix < 2; ix++) {
				br2.readLine();
			}

			String thisLine;
			String outputChunk;
			out = new BufferedWriter(new FileWriter(scriptOut, true));

			out.write("SELECT * INTO " + newtablename + "\n");
			out.write("FROM " + dbtablename + "\n");
			out.write("WHERE 1 = 0" + "\n");
			out.write("go" + "\n" + "\n");

			Conversion
					.createMessage("Beginning file parse, please wait...", "");

			// This counter writes a  after 4000 rows
			// to allow user to copy large result sets in batches
			// this avoiding a limitation of Interactive SQL
			int counter = 0;
			
			while ((thisLine = br2.readLine()) != null)

			{

				try {

					if (counter < 4000) 
					counter++;
					
					if (thisLine.equals("") || 
							(thisLine.trim().startsWith("Execution time:") && 
							thisLine.trim().endsWith("seconds")) ||
							
							(thisLine.trim().startsWith("(") &&
							thisLine.trim().endsWith("rows)")) ||
							
							(thisLine.trim().startsWith("(")) &&
							thisLine.trim().endsWith("seconds")) 
					{
						br2.close();
						out.close();
						//OpenScript of = new OpenScript();
						Conversion.createMessage("File created:  ",
								outputDirName + outputFileName);
						//of.OpenScript(outputDirName + outputFileName);

					}

					out.write("INSERT INTO " + newtablename + " VALUES (");

					// create counter for rows.  After 4000, write out a 
					// this helps the user copy rows in batches to avoid
					// limitations of Interactive SQL
					
					for (int i = 0; i <= colcount - 2; i++) {

						thisLine.trim();
						outputChunk = thisLine.substring(ColStartArray[i],
								ColEndArray[i]);
						outputChunk = outputChunk.replaceAll("\\(NULL\\)",
								"NULL  ");
						//outputChunk = outputChuck.replaceAll("(NULL)", "NULL  ";)
						outputChunk = outputChunk.replaceAll("\"", "\"\"");
						datatypename = rsmd.getColumnTypeName(i + 1);
						if ((datatypename.equals(intString) //intString
								|| datatypename.equals(smallintString)
								|| datatypename.equals(floatString)
								|| datatypename.equals(decimalString)
								|| datatypename.equals(realString)
								|| datatypename.equals(imageString)
								|| datatypename.equals(tinyintString))
								&& outputChunk.trim().equals(""))
							out.write("NULL, ");
						else if (datatypename.equals(intString)
								|| datatypename.equals(smallintString)
								|| datatypename.equals(floatString)
								|| datatypename.equals(decimalString)
								|| datatypename.equals(realString)
								|| datatypename.equals(imageString)
								|| datatypename.equals(tinyintString))
								out.write(outputChunk.trim() + ", ");
						else if ((datatypename.equals(varcharString) && outputChunk
								.trim().equals("NULL"))
								|| (datatypename.equals(datetimeString) && outputChunk
										.trim().equals("NULL"))
								|| (datatypename.equals(charString) && outputChunk
										.trim().equals("NULL"))
								|| (datatypename.equals(textString) && outputChunk
										.trim().equals("NULL")))
							out.write(outputChunk.trim() + ", ");
						else if ((datatypename.equals(varcharString) && !(outputChunk
								.trim()).equals("NULL"))
								|| (datatypename.equals(datetimeString) && !(outputChunk
										.trim()).equals("NULL"))
								|| (datatypename.equals(charString) && !(outputChunk
										.trim()).equals("NULL"))
								|| (datatypename.equals(textString) && !(outputChunk
										.trim()).equals("NULL")))
							out.write("\"" + outputChunk.trim() + "\", ");
							}

					outputChunk = thisLine.substring(
							ColStartArray[colcount - 1],
							ColEndArray[colcount - 1]);
					outputChunk = outputChunk.replaceAll("\\(NULL\\)",
							"NULL  ");
					datatypename = rsmd.getColumnTypeName(colcount);
					if ((datatypename.equals(intString)
							|| datatypename.equals(smallintString)
							|| datatypename.equals(floatString)
							|| datatypename.equals(decimalString)
							|| datatypename.equals(realString)
							|| datatypename.equals(imageString)
							|| datatypename.equals(tinyintString))
							&& outputChunk.trim().equals(""))
						out.write("NULL");
					else if (datatypename.equals(intString)
							|| datatypename.equals(smallintString)
							|| datatypename.equals(floatString)
							|| datatypename.equals(decimalString)
							|| datatypename.equals(realString)
							|| datatypename.equals(imageString)
							|| datatypename.equals(tinyintString))
						out.write(outputChunk.trim());
					else if ((datatypename.equals(varcharString) && outputChunk
							.trim().equals("NULL"))
							|| (datatypename.equals(datetimeString) && outputChunk
									.trim().equals("NULL"))
							|| (datatypename.equals(charString) && outputChunk
									.trim().equals("NULL"))
							|| (datatypename.equals(textString) && outputChunk
									.trim().equals("NULL")))
						out.write(outputChunk.trim());
					else if ((datatypename.equals(varcharString) && !(outputChunk
							.trim()).equals("NULL"))
							|| (datatypename.equals(datetimeString) && !(outputChunk
									.trim()).equals("NULL"))
							|| (datatypename.equals(charString) && !(outputChunk
									.trim()).equals("NULL"))
							|| (datatypename.equals(textString) && !(outputChunk
									.trim()).equals("NULL")))
						out.write("\"" + outputChunk.trim() + "\"");
					out.write(")" + '\n' + "go" + '\n');

					if (counter == 4000) {
						out.write("\n" + "\n" + "\n" + "\n" + "\n" + "/* SECTION DIVIDER */" + "\n" );
						out.write("\n" + "\n" + "\n" + "\n" + "\n");
						counter = 0;
					}
				}

				catch (SQLException g) {
					Conversion.createMessage("ERROR:  " + g, "");
				} catch (EOFException e) {
					Conversion.createMessage("ERROR:  " + e, "");
				}

				catch (FileNotFoundException fnfe) {
					// System.out.println("Cannot locate input file! "+fnfe.getMessage());
					Conversion.createMessage("ERROR:  " + fnfe, "");
					// System.exit(0);
				}

			}

			// br2.close(); removing this got rid of the "Stream closed"
			// exception
			// extra line in input file appears to be possible cause of this.
			out.close();

			//OpenScript of = new OpenScript();
			Conversion.createMessage("File created:  ", outputDirName 
					+ outputFileName);
			//of.OpenScript(outputDirName + outputFileName);

		}

		catch (Exception e)

		{
			Conversion.createMessage("ERROR:  " + e, "");
		}

	}

}
