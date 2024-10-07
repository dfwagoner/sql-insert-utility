import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class ConversionEngineWindow extends Frame implements ActionListener {
	MenuBar menuBar = new MenuBar();
	MenuItem item;
	Menu extrasMenu, helpMenu;
	public TextField FileLocationField, TableNameField, DBTableNameField,
			ServerNameField, DBNameField, UseridField, PasswordField;
	public static TextArea errorArea;
	Label label, label2, label3;
	Button savebutton, exitbutton, browsebutton;
	FlowLayout flow;
	BufferedWriter out;
	Checkbox checky;
	public boolean checkstate;

	public ConversionEngineWindow(String title) {
		super(title);
		Panel buttonPane = new Panel();
		GridBagLayout gbLayout = new GridBagLayout();
		buttonPane.setLayout(gbLayout);

		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = 1;

		savebutton = new Button(" Create SQL  ");
		gbLayout.setConstraints(savebutton, constraints);
		buttonPane.add(savebutton);
		savebutton.addActionListener(this);
		exitbutton = new Button(" Quit  ");
		gbLayout.setConstraints(exitbutton, constraints);
		buttonPane.add(exitbutton);
		exitbutton.addActionListener(this);

		final Panel dataPane = new Panel(); // added final to test addition of
											// custom table name field
		gbLayout = new GridBagLayout();
		dataPane.setLayout(gbLayout);
		constraints.insets = new Insets(3, 3, 3, 3);
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;

		label = new Label("    Location Of Script Results to Convert:");
		gbLayout.setConstraints(label, constraints);
		dataPane.add(label);

		browsebutton = new Button("Browse");
		gbLayout.setConstraints(browsebutton, constraints);
		dataPane.add(browsebutton);
		browsebutton.addActionListener(this);

		final JFileChooser fc = new JFileChooser();

		browsebutton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				fc.setApproveButtonText("Choose File");
				int returnVal = fc.showOpenDialog(ConversionEngineWindow.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String FileLocation;
					FileLocation = fc.getSelectedFile().getPath();
					FileLocationField.setText(FileLocation);
				}
			}
		});

		FileLocationField = new TextField("C:\\", 19);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(FileLocationField, constraints);
		dataPane.add(FileLocationField);

		label2 = new Label(
				"                                        New table name:");
		constraints.gridwidth = 1;
		gbLayout.setConstraints(label2, constraints);
		dataPane.add(label2);
		TableNameField = new TextField("", 28);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(TableNameField, constraints);
		dataPane.add(TableNameField);

		label3 = new Label("             Corresponding database table:");
		constraints.gridwidth = 1;
		gbLayout.setConstraints(label2, constraints);
		dataPane.add(label3);
		DBTableNameField = new TextField("", 28);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(DBTableNameField, constraints);
		dataPane.add(DBTableNameField);

		label3 = new Label(
				"                Check for Custom Table Mode:");
		constraints.gridwidth = 1;
		gbLayout.setConstraints(label3, constraints);
		dataPane.add(label3);
		checky = new Checkbox(null, false);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(checky, constraints);
		dataPane.add(checky);

		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				checkstate = checky.getState();
				// writeMessage("", "Checked..." + checkstate + ":  " +
				// itemEvent);
				DBTableNameField
						.setEditable(itemEvent.getStateChange() == ItemEvent.DESELECTED);
				DBTableNameField.setText("<custom table>");
				TableNameField
						.setEditable(itemEvent.getStateChange() == ItemEvent.DESELECTED);
				TableNameField.setText("");
				if (itemEvent.getStateChange() == ItemEvent.DESELECTED)
					DBTableNameField.setText("");
				TableNameField.setText("");
				ServerNameField.setText("");
				DBNameField.setText("");
				UseridField.setText("");
				PasswordField.setText("");
				ServerNameField
				.setEditable(itemEvent.getStateChange() == ItemEvent.DESELECTED);
				DBNameField
				.setEditable(itemEvent.getStateChange() == ItemEvent.DESELECTED);
				UseridField
				.setEditable(itemEvent.getStateChange() == ItemEvent.DESELECTED);
				ServerNameField
				.setEditable(itemEvent.getStateChange() == ItemEvent.DESELECTED);
				PasswordField
				.setEditable(itemEvent.getStateChange() == ItemEvent.DESELECTED);
			}
		};
		checky.addItemListener(itemListener);

		label3 = new Label(
				"                                               Server name:");
		constraints.gridwidth = 1;
		gbLayout.setConstraints(label3, constraints);
		dataPane.add(label3);
		ServerNameField = new TextField("", 28);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(ServerNameField, constraints);
		dataPane.add(ServerNameField);

		label3 = new Label(
				"                                        Database name:");
		constraints.gridwidth = 1;
		gbLayout.setConstraints(label3, constraints);
		dataPane.add(label3);
		DBNameField = new TextField("", 28);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(DBNameField, constraints);
		dataPane.add(DBNameField);

		label3 = new Label(
				"                                                         Userid:");
		constraints.gridwidth = 1;
		gbLayout.setConstraints(label3, constraints);
		dataPane.add(label3);
		UseridField = new TextField("sa", 28);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(UseridField, constraints);
		dataPane.add(UseridField);

		label3 = new Label(
				"                                                   Password:");
		constraints.gridwidth = 1;
		gbLayout.setConstraints(label3, constraints);
		dataPane.add(label3);
		PasswordField = new TextField("", 28);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(PasswordField, constraints);
		dataPane.add(PasswordField);
		setMenuBar(menuBar);

		Panel errorPanel = new Panel();
		errorPanel.setLayout(gbLayout);
		errorArea = new TextArea("", 6, 64);
		constraints.gridwidth = 1;
		gbLayout.setConstraints(errorArea, constraints);
		constraints.gridwidth = GridBagConstraints.NONE;
		gbLayout.setConstraints(errorArea, constraints);
		errorPanel.add(errorArea);

		helpMenu = new Menu("Help");

		helpMenu.add(item = new MenuItem("SQLInsertUtility version 2.0"));

		menuBar.add(helpMenu);

		this.add(errorPanel, BorderLayout.SOUTH);
		this.add(buttonPane, BorderLayout.CENTER);
		this.add(dataPane, BorderLayout.NORTH);
	}

	public void actionPerformed(ActionEvent e) {
		char c = e.getActionCommand().charAt(1);
		if (c == 'C')
			save();
		// if (c == 'C' && checkstate == true) save();
		// if (c == 'C' && checkstate == false) writeMessage("",
		// "checkstate is false");

		else if (c == 'Q')
			exit();

	}

	public void save() {
		try {
			if (checkstate == false) {
				writeMessage("", "Conversion started...");
				ConversionEngine MyEngine = new ConversionEngine();

				MyEngine.IsolateDashes(FileLocationField.getText());
				MyEngine.setColumnCount();
				// System.out.println("colcount is " +
				// MyEngine.setColumnCount());
				MyEngine.createFieldStartArray();
				MyEngine.createFieldEndArray();
				MyEngine.SQLOutputEngine(FileLocationField.getText(),
						ServerNameField.getText(), DBNameField.getText(),
						UseridField.getText(), PasswordField.getText(),
						TableNameField.getText(), DBTableNameField.getText());
			} else if (checkstate == true) {
				writeMessage("", "Conversion started...");
				CustomConversionEngine MyEngine = new CustomConversionEngine();
				MyEngine.IsolateDashes(FileLocationField.getText());
				int colcount;
				colcount = MyEngine.setColumnCount();
				// MyEngine.setColumnCount();
				// System.out.println("colcount is " +
				// MyEngine.setColumnCount());
				MyEngine.createFieldStartArray();
				MyEngine.createFieldEndArray();
				MyEngine.getColumnNames(FileLocationField.getText());
				MyEngine.DataChooserWindow(FileLocationField.getText(),
						ServerNameField.getText(), DBNameField.getText(),
						UseridField.getText(), PasswordField.getText(),
						colcount);

			}

		}

		catch (Exception f) {
			Conversion.createMessage("ERROR:  " + f, "");
		}
	}

	public void writeMessage(String event, String explan) {

		// suppress 'Stream closed' exception caused by extra line in input file
		if (event.contains("Stream closed")) { // do nothing
		} else if (event.contains("keyword 'where'")) {
			errorArea.append("Connection Error:  "
					+ "Please check: a) Valid server name? or " + "\n"
					+ " b) possible " + "blank table name?" + "\n");
		} else
			errorArea.append(event + explan + "\n");
	}

	public void exit() {
		this.dispose();
		System.exit(0);
	}

}
