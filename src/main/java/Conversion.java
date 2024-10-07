import java.awt.*;

public class Conversion {

    static Conversion theApp;
    static ConversionEngineWindow MyConversion;
 
  public static void main(String args[])
    {
      theApp = new Conversion(); 
      theApp.init();
    };

  public void init() 
    {
      MyConversion = new ConversionEngineWindow("SQLInsertUtility 2.0");
      Toolkit Kitty = MyConversion.getToolkit();
      Dimension wndSize = Kitty.getScreenSize();
      MyConversion.setBounds(wndSize.width/4, wndSize.height/4,
    		  475, 440); //475, 
//values were 460, 415 before checkbox was added, 475, 430
      //      MyConversion.addWindowListener(new WindowHandler());
      MyConversion.setVisible(true);
      MyConversion.requestFocus();
    } 

  public static void createMessage(String event, String explan)
  {
    MyConversion.writeMessage(event, explan);
  }
 
}



 