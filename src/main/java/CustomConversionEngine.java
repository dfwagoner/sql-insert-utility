import java.text.DateFormat;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class CustomConversionEngine extends ConversionEngine {

	void DataChooserWindow(final String location2, final String servername,
			final String dbname, final String login, final String pword,
			final int colcount) {

		final String[] dataTypesChosen = new String[colcount];
		final Frame CustomTable;
		CustomTable = new Frame("Datatype Chooser for " + colcount + " Columns");

		CustomTable.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				CustomTable.dispose();
			}
		});

		FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);

		Toolkit Kit = CustomTable.getToolkit();
		Dimension wndSize = Kit.getScreenSize();

		
			if (colcount < 28) {
			CustomTable.setBounds(wndSize.width / 2, wndSize.height / 12, 360,
					(((30 * colcount) - 1) + 45) + 70);
		} else if (colcount < 46 && colcount > 27) {
			//CustomTable.setBounds(wndSize.width / 2, wndSize.height / 12, 360,
				//	(colcount * 50) - 20); // was 475 and 230 was 350 and 37
			CustomTable.setBounds(200, wndSize.height / 16, 720, colcount * 18); 
		} else if (colcount >= 16) {
			CustomTable.setBounds(200, wndSize.height / 16, 720, (colcount * 60) - 600);
		}
		
		CustomTable.setVisible(true);
		CustomTable.requestFocus();
		
		//System.out.println("bounds are " + CustomTable.getBounds());
		Button continuebutton, exitbutton;
		Label tableLabel;
		Label fillerLabel;
		final TextField customTableName;
		Panel testPanel = new Panel();
		CustomTable.setLayout(flowLayout);
		testPanel.setLayout(flowLayout);
		Panel testPanel2 = new Panel();
		testPanel2.setLayout(flowLayout);

		final String[] datatypes = { "int", "char", "varchar", "smallint",
				"datetime", "double precision", "text", "decimal", "image" };

		final String[] datatypes2 = { "int", "char(1)", "char(3)", "varchar(10)",
				"varchar(30)", "varchar(40)", "varchar(65)", "varchar(255)",
				"smallint", "datetime", "double precision", "text", "decimal",
				"image" };
		
		final Choice[] typeChooserList = new Choice[colcount];
		Label[] columnLabel = new Label[colcount];
		Font myFont = new Font("Courier", Font.PLAIN, 12);

		for (int i = 0; i < colcount; i++) {
			typeChooserList[i] = new Choice();
			columnLabel[i] = new Label(ColumnNamesFormatted[i] + ':');
			columnLabel[i].setFont(myFont);
			CustomTable.add(columnLabel[i]);
			CustomTable.add(typeChooserList[i]);

			for (int j = 0; j < datatypes2.length; j++) {
				typeChooserList[i].addItem(datatypes2[j]);
			}
		}

		for (int i = 0; i < colcount; i++) {
			typeChooserList[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
				}
			});

		}

		fillerLabel = new Label(
				"                                                                                 ");
		testPanel.add(fillerLabel);
		continuebutton = new Button(" Continue  ");
		testPanel.add(continuebutton);

		exitbutton = new Button(" Quit  ");
		testPanel.add(exitbutton);
		exitbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CustomTable.dispose();
			}
		});
		tableLabel = new Label("New table name:");
		testPanel2.add(tableLabel);
		customTableName = new TextField("", 25);
		testPanel2.add(customTableName);
		CustomTable.add(testPanel);
		CustomTable.add(testPanel2);

		continuebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// populate dataTypesChosen array with datatypes from drop downs
				for (int i = 0; i < colcount; i++) {
					dataTypesChosen[i] = typeChooserList[i].getSelectedItem();
				}
				SQLOutputEngineCustom(location2, servername, dbname, login,
						pword, customTableName.getText(), ColumnNames,
						dataTypesChosen, colcount);
				CustomTable.dispose();
			}
		});
	}

	public void SQLOutputEngineCustom(String location2, String servername,
			String dbname, String login, String pword, String newtablename,
			String[] columnNames, String[] dataTypes, int colcount) {

		final String url, user_id, password, datatypename;
		final BufferedWriter out;

		try {

			// new table name cannot have spaces
			newtablename = newtablename.replace(" ", "_");

			DateFormat fmt = DateFormat.getTimeInstance(DateFormat.SHORT);
			java.util.Date today = new java.util.Date();
			String userid = new String();			
			//security restrictions require us to get userid and create file
			//in My Documents instead of C drive
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
			/*
			 * Class.forName("com.sybase.jdbc2.jdbc.SybDriver"); url =
			 * ("jdbc:sybase:Tds:" + servername + ":5000/" + dbname +
			 * "?CHARSET=iso_1"); user_id = login; password = pword; Connection
			 * conn = DriverManager.getConnection(url, user_id, password);
			 */

			// this is where the file parsing begins

			for (int ix = 0; ix < 2; ix++) {
				br2.readLine();
			}

			String thisLine;
			String outputChunk;
			out = new BufferedWriter(new FileWriter(scriptOut, true));

			// write the code to create the custom table
			out.write("CREATE TABLE " + newtablename + " (" + "\n");

			for (int i = 0; i < colcount - 1; i++) {
				out.write(columnNames[i] + " " + dataTypes[i] + " NULL," + "\n");
			}
			out.write(columnNames[colcount - 1] + " " + dataTypes[colcount - 1]
					+ " NULL");
			out.write(") " + "\n" + "go" + "\n" + "\n");

			// classify data types as either requiring quotes or not
			for (int i = 0; i <= colcount - 1; i++) {
				if (dataTypes[i].startsWith("varchar")
						|| dataTypes[i].startsWith("char")
						|| dataTypes[i].startsWith("datetime")
						|| dataTypes[i].startsWith("text")) {
					dataTypes[i] = "QUOTES";
				} else
					dataTypes[i] = "NO_QUOTES";
			}

			Conversion
					.createMessage("Beginning file parse, please wait...", "");

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

					for (int i = 0; i <= colcount - 2; i++) {

						thisLine.trim();
						outputChunk = thisLine.substring(ColStartArray[i],
								ColEndArray[i]);
						outputChunk = outputChunk.replaceAll("\\(NULL\\)",
								"NULL  ");
						outputChunk = outputChunk.replaceAll("\"", "\"\"");
						if ((dataTypes[i].equals("NO_QUOTES") && outputChunk
								.trim().equals(""))) //still need at end of this loop and for super class
							out.write("NULL, ");  
						else if (dataTypes[i].equals("NO_QUOTES"))
							out.write(outputChunk.trim() + ", ");
						else if ((dataTypes[i].equals("QUOTES") && outputChunk
								.trim().equals("NULL")))
							out.write(outputChunk.trim() + ", ");
						else if ((dataTypes[i].equals("QUOTES") && !(outputChunk
								.trim()).equals("NULL")))
							out.write("\"" + outputChunk.trim() + "\", ");
					}
					// this is outside the loop and is for the end column which cannot be done 
					// inside the loop due to sql syntax requirements.
					outputChunk = thisLine.substring(
							ColStartArray[colcount - 1],
							ColEndArray[colcount - 1]);
					outputChunk = outputChunk
							.replaceAll("\\(NULL\\)", "NULL  ");
					if ((dataTypes[colcount - 1].equals("NO_QUOTES") && outputChunk
							.trim().equals(""))) //still need for super class
						out.write("NULL");  
					else if (dataTypes[colcount - 1].equals("NO_QUOTES"))
						out.write(outputChunk.trim());
					else if ((dataTypes[colcount - 1].equals("QUOTES") && outputChunk
							.trim().equals("NULL")))
						out.write(outputChunk.trim());
					else if ((dataTypes[colcount - 1].equals("QUOTES") && !(outputChunk
							.trim()).equals("NULL")))
						out.write("\"" + outputChunk.trim() + "\"");
					out.write(")" + '\n' + "go" + '\n');
					
					if (counter == 4000) {
						out.write("\n" + "\n" + "\n" + "\n" + "\n" + "/* SECTION DIVIDER */" + "\n" );
						out.write("\n" + "\n" + "\n" + "\n" + "\n");
						counter = 0;
					}
				}

				catch (EOFException e) {
					// System.out.println(
					// "End of stream encountered");
					Conversion.createMessage("ERROR:  " + e, "");
				}

				catch (FileNotFoundException fnfe) {
					// System.out.println("Cannot locate input file! "+fnfe.getMessage());
					Conversion.createMessage("ERROR:  " + fnfe, "");
					// System.exit(0);
				}

			}

			// The following code does not seem necessary. Commented out, still
			// runs same.
			br2.close();
			out.close();
			//OpenScript of = new OpenScript();
			Conversion.createMessage("File created:  ", outputDirName
					+ "\\" + outputFileName);
			//of.OpenScript(outputDirName + outputFileName);
		}

		catch (Exception e)

		{
			// Conversion.createMessage("ERROR:  " + e, "");
		}

	}

}
