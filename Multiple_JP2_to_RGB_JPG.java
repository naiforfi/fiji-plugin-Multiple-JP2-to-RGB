import ij.*;
import ij.process.*;
import ij.gui.*;
import java.io.File;
import java.io.FileFilter;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import javax.swing.*;
import java.awt.Panel;
import java.awt.event.*;
import java.util.*;
import loci.plugins.in.ImporterOptions;
import loci.plugins.BF;
import java.io.IOException;
import loci.formats.FormatException;
import java.util.ArrayList;


 
public class Multiple_JP2_to_RGB_JPG  implements PlugIn {
    Panel panel = new Panel();
    Panel panel2 = new Panel();
    JLabel describtion;
    JFrame frame ;
    JButton selectInput;
    JButton selectOutput;
    JFileChooser inputChooser;
    JFileChooser outputChooser;
    JTextField inputText;
    JTextField outputText;
    JLabel inputLabel;
    JLabel outputLabel;
    String output = "";
    
    String inputPath ;
    String outputPath ;

	// summary
    int totalImages = 0;

    ArrayList<String> imagesError = new ArrayList<String>();

    
    public Multiple_JP2_to_RGB_JPG(){
    	// 1st Row
    	
    	inputLabel = new JLabel();
	inputLabel.setText("Find All JP2 from : ");
	panel.add(inputLabel);
    	
    	inputText = new JTextField(12);
        inputText.setText("No Selection");
        inputText.setEditable(false);
        panel.add(inputText);
        
        selectInput = new JButton("Select");
        selectInput.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
     			 inputChooser = new JFileChooser();
        
       			 inputChooser.setDialogTitle("Select the folder contains the jp2 images");
       			 inputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
       			 if (inputChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
       		             inputText.setText(inputChooser.getSelectedFile().toString());
      			  } else {
       			     inputText.setText("No Selection");
      			  }

  		}
  	});
        panel.add(selectInput);

	// 2nd Row
        inputLabel = new JLabel();
	inputLabel.setText("Save as JPG to : ");
	panel.add(inputLabel);

	outputText = new JTextField(12);
        outputText.setText("No Selection");
        outputText.setEditable(false);
        panel.add(outputText);

        selectOutput = new JButton("Select");
        
        selectOutput.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
     			 inputChooser = new JFileChooser();
        
       			 inputChooser.setDialogTitle("Select the folder contains the jp2 files");
       			 inputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
       			 if (inputChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
       		             outputText.setText(inputChooser.getSelectedFile().toString());
      			  } else {
       			     outputText.setText("No Selection");
      			  }

  		}
  	});
  	
        panel.add(selectOutput);
        panel.setLayout(new GridLayout(2,3));
        
	describtion = new JLabel();
	describtion.setText("All images will be saved in the same structure of the jp2 folder");
	panel2.add(describtion);
	
    	
    }

	


    
    public void run(String arg) {
      GenericDialog gd = new GenericDialog("Multiple JP2 RGB and Composite v3.0");
      gd.addPanel(panel);
      gd.addPanel(panel2);
      gd.setOKLabel("Start");
      gd.showDialog();
      if (gd.wasCanceled()) return;
      IJ.log("\\Clear");
      IJ.log("Multiple JP2 RGB and Composite v3.0 - for MacOSX ");
      IJ.log("Developed by Naif Alorfi");
      IJ.log("Convert multiple jp2 images to 3 splited channels RGB then Merge and save them to jpg formate");
      IJ.log("*************************************************************************************");
      
      
	String in = inputText.getText();
	String out = outputText.getText();
        output = "" ;
        if (gd.wasOKed() && !out.equals("No Selection")){
	        try {
	        	
	       		 readDirectory(in,out);

	             }
	    	catch (NullPointerException e)
	    	{
	    		IJ.log("Can't find any jp2 images in the spacified folder.");
	    		
	    	}
	    	IJ.beep();
		IJ.log("--------------------");
		IJ.log("Process Completed ");
	    	IJ.log("Total Image Processed: " +  String.valueOf(totalImages));
	        
        } 
        else
        {
        	IJ.log("Error: Please choose the location to save the jpg files");
        	IJ.beep();
		IJ.log("--------------------");
	        IJ.log("Process Stopped ");
        }
        
        

    }



    public  void readDirectory(String inputDirectory, String outputDirectory) {
    	IJ.log("Starting ..") ;
            ArrayList<File> fileList = listf(inputDirectory,inputDirectory,outputDirectory, new ArrayList<File>());
            if (fileList.size() == 0){
            	IJ.log("Can't find any jp2 images in the spacified folder.");
            }
            else {
            	
	            for (File img : fileList) {
	            	 
	                String input = img.getPath();
			IJ.log("Processing image: " + img.getName());
	                output = outputDirectory + img.getPath().substring(inputDirectory.length()) + "/";
	                 new File(output).mkdirs();
	                 
			if (output.equals("")){
				output = outputDirectory;
			}
			try {
		               doTasks(img.getPath(), img.getName());
		               IJ.log("Done");
			} catch (Exception e)
			{
				IJ.log("Problem in Image : " + img.getPath() );
				
			}
	               
	            }
            }
    }
    // reading through the input directory to find jp2 files
    private  ArrayList<File> listf(String initoutout,String theFile,String outputDirectory, ArrayList<File> files) {
        File directory = new File(theFile);
        
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile() && file.getName().endsWith("jp2") && !file.isHidden()) {
                files.add(file);
                
            } else if (file.isDirectory()) {
                listf(initoutout,file.getPath(),outputDirectory, files);
            }
        }
        return files;

    }

    public void doTasks(String imgPath, String imgName) throws IOException, FormatException {
    	// implement bio-formate to be windowsless
    	ImporterOptions options = new ImporterOptions();
    	options.setId(imgPath);
    	options.setSplitChannels(true);
    	options.setAutoscale(true);
    	options.setColorMode("Default");
    	options.setStackFormat("Hyperstack");
    	
        ImagePlus imp[] =  BF.openImagePlus(options);
	imp[0].show();
	imp[1].show();
	imp[2].show();
	
        IJ.selectWindow(imgName + " - C=2");
        IJ.run("32-bit");     
        IJ.run("Blue", "");
        //IJ.selectWindow(imgName + " - C=2");
        IJ.saveAs("Jpeg", output + imgName + " - C=2.jpg");
	
	
        IJ.selectWindow(imgName + " - C=1");
        IJ.run("32-bit");
        IJ.run( "Green", "");
        //IJ.selectWindow(imgName + " - C=1");
        IJ.saveAs( "Jpeg", output + imgName + " - C=1.jpg");

        IJ.selectWindow(imgName + " - C=0");
        IJ.run("32-bit");
        IJ.run("Red", "");
        //IJ.selectWindow(imgName + " - C=0");
        IJ.saveAs("Jpeg", output + imgName + " - C=0.jpg");
	
	
        IJ.run("Merge Channels...", "c1=[" + imgName + " - C=0] c2=[" + imgName + " - C=1] c3=[" + imgName + " - C=2] create");
        IJ.saveAs("Jpeg", output + "Composite.jpg");
	
        IJ.selectWindow(1);
        IJ.run("Close");
        totalImages += 1;
	
    }
}
