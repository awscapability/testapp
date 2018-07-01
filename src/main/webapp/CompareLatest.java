package validation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;


public class CompareLatest {
	
	public static String strResPath = null;
	public static String strResFldr = null;
	public static String relativePath = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException {
		
        BufferedReader srcFile = null;
        BufferedReader trgtFile = null;
        
        String sCurrentLine;
        Map<Integer,String> srcList = new HashMap<Integer,String>();
        Map<Integer,String> trgList = new HashMap<Integer,String>();
        Map<Integer,String> tmpList = new HashMap<Integer,String>();
        Map<Integer,String> mDSECol = new HashMap<Integer,String>();
        
        List<String> source = new ArrayList<String>();
        List<String> target = new ArrayList<String>();
                
        //Get the timestamp for the log files' folder
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());        
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(timestamp);
        timeStamp = timeStamp.replace(".", "_");
        
        //Create the Result folder path
 	    relativePath = System.getProperty("user.dir");
 	    System.out.println("Current working directory : " + relativePath);
 	    
 	    //Create the result folder with time stamp. This folder is going to contain the log files for each type of file
 	    createResultFolder(timeStamp);
 	    
        //Check for the existence of files in the folder
		File srcfolder = new File(relativePath + "\\Desktop");
		File trgFolder = new File(relativePath + "\\CloudBridge");
		File[] srcFiles = srcfolder.listFiles();
		
		if(srcFiles.length <= 0){	
			//Exception Logic to be implemented here in an Exception.txt file
			System.out.println("======================================================================");
			System.out.println("The source folder is empty, please place required files for validation");
			System.out.println("======================================================================");
		}else{
			System.out.println("=====================================================================");
			System.out.println("Total Number of Files to be validated : " + srcFiles.length);
			System.out.println("=====================================================================");

			for (int i = 0; i < srcFiles.length; i++){
				
				//Get the source file into a list before getting into the details of validation
				srcList = createContentList(srcFiles[i]);
				
				//Log File for DSE
				String strComtLog = strResFldr + "\\Comt_Log.txt";
				
				if(srcFiles[i].getName().contains("ComtLog")){
					
					String[] dseSrc = srcFiles[i].getName().split("_");
					
					//Get the Columns
					mDSECol = getColName(dseSrc[0]);
					
					//Check for the existence of the DSE Schedule file for the store 
					FilenameFilter textFilter = new FilenameFilter()
			        {
			         public boolean accept(File directory, String filename) {
			              return filename.contains(dseSrc[0] + "_" + dseSrc[1] + "_" + dseSrc[2]);
			              
			          }
			        };
			        
			        addDetails(strComtLog,"================ Validation of " + dseSrc[0] + " for Store No. : " + dseSrc[1] + " ==================");
			        addDetails(strComtLog,"");
					addDetails(strComtLog,"Source File name : " + srcFiles[i].getName());
					List lDiscard = new ArrayList();
			        File[] files = trgFolder.listFiles(textFilter);
			        if(files.length == 1) {
			        	addDetails(strComtLog,"Target File name : " + files[0].getName());
			        	
			        	//Check if the name of the Target File is exactly the same as that of the Source File or not
			        	if(!files[0].getName().equalsIgnoreCase(srcFiles[i].getName())) {
			        		addDetails(strComtLog,"**** Warning !! - Target file name different from Source File name ****");
			        	}
			        	
			        	trgList = createContentList(files[0]);
			        	
			        	Collection<String> translations = trgList.values();
			        	Set<String> dupeSet = new HashSet<>();

			        	for (String t : translations) {
			        	    if (Collections.frequency(translations, t) > 1) {
			        	        dupeSet.add(t);
			        	    }
			        	}
			        	
			        	//Print out the duplicates in the Map
			        	if(dupeSet.size() > 0) {
			        		addDetails(strComtLog," ");
			        		addDetails(strComtLog,"**** Warning !! - " + dupeSet.size() + " Duplicates in the Target file from CB ****");
			        		addDetails(strComtLog,"     Following are the lines - ");
			        		Iterator itr = dupeSet.iterator();
			                while(itr.hasNext())
			                {
			                	addDetails(strComtLog,"   " + itr.next());
			                }
			        	}
			        	addDetails(strComtLog," ");			        	
			        	
			        	//Check for the number of entries in the file
			        	if(trgList.size() == srcList.size()) {
			        		addDetails(strComtLog,"The number of entries in both the files match");
			        		addDetails(strComtLog,"  -- No. of entries in Source : " + srcList.size());
			        		addDetails(strComtLog,"  -- No. of entries in Target : " + trgList.size());
			        	}else {
			        		addDetails(strComtLog,"**** Warning !! - The number of entries in both the files do not match ****");
			        		addDetails(strComtLog,"  -- No. of entries in Source : " + srcList.size());
			        		addDetails(strComtLog,"  -- No. of entries in Target : " + trgList.size());
			        	}
			        				        	
			        	addDetails(strComtLog,"");
			        	
			        	int iSrcCount = 1,iTrgCount=1;
			        	
			        	while(srcList.get(iSrcCount) != null){
			            	//String arrSrc[] = srcList.get(iSrcCount).split(",");
			            	int iLeast=0,num=0, iLineNumber=0, iMatchLine=0, iMismatch=0, iTemp=0, iNearMatch=0;
			            	boolean bFlag = true;
			            	
			            	String[] arrTrg = null;		            	
		            		List lFinalDiff = new ArrayList();
		            		
			            	while(trgList.get(iTrgCount) != null) {
		            			
			            		//Compare the two arrays
		            			
		            			//iFinal=srcList.get(iSrcCount).length();
		            			List lDiff = new ArrayList();
		            			iMismatch = 0;
			            		for(int iCount = 0; iCount<srcList.get(iSrcCount).length();iCount++) {
			            			
			            			if(srcList.get(iSrcCount).length() == trgList.get(iTrgCount).length()) {
				            			if(srcList.get(iSrcCount).charAt(iCount) != trgList.get(iTrgCount).charAt(iCount)) {
				            				iMismatch = iMismatch + 1;
				            				lDiff.add(iCount + 1);
				            				//iTemp = iMismatch;
				            				bFlag = false;
				            			}
			            			}
			            		}
			            		if(iMismatch == 0) {
			            			iLineNumber = iTrgCount;
			            			//iFinal=srcList.get(iSrcCount).length();
			            			iTrgCount = 1;
			            			break;
			            				            			
			            		}else if(iMismatch < iLeast) {
			            			iLeast = iMismatch;
			            			iLineNumber = iTrgCount;
			            			iNearMatch = iTrgCount;
			            			lFinalDiff = lDiff;
			            			bFlag = true;
			            			iMismatch = 0;
			            		
			            		}else if((iMismatch > iLeast) && iTrgCount <=2 ) {			            			
			            			iLeast = iMismatch;
			            			//iLineNumber = iTrgCount;
			            			//lFinalDiff = lDiff;
			            			bFlag = true;
			            			iMismatch = 0;
			            			
			            		}else if((iMismatch < iLeast) && iTrgCount == trgList.size()) {
			            			iLeast = iMismatch;
			            			iLineNumber = iTrgCount;
			            			iNearMatch = iTrgCount;
			            			lFinalDiff = lDiff;
			            			bFlag = true;
			            			iMismatch = 0;
			            			break;
			            		}
			            		
			            		iTrgCount++;			            		
			            	}
			            	
			            	
			            	if(iLeast > 0 && iLineNumber == trgList.size()-1) {
			            		addDetails(strComtLog,"");
			            		addDetails(strComtLog," **** ERROR - Line No. : " + iSrcCount + " does not have a corresponding entry in Target file from CB **** ");
			            		addDetails(strComtLog," " + srcList.get(iSrcCount));
			            		addDetails(strComtLog,"  Nearest Possible Match - ");
			            		addDetails(strComtLog," " + trgList.get(iNearMatch));
			            		addDetails(strComtLog,"");
			            	}else if(iMismatch == 0) {
			            		
			            		
			            		addDetails(strComtLog,"Line No. : " + iSrcCount + " has been correctly populated in the Target file from CB at Line No. : " + iLineNumber);
			            		addDetails(strComtLog," - Source - " + srcList.get(iSrcCount));
			            		addDetails(strComtLog," - Target - " + trgList.get(iLineNumber));
			            		addDetails(strComtLog,"");
			            		
			            	}else if(iLeast >0 && iLineNumber < srcList.get(iSrcCount).length()) {
			            		
			            		addDetails(strComtLog," **** Mismatch Found between the following two lines : ");
			            		addDetails(strComtLog," - Source Line No. : " + iSrcCount + " - " + srcList.get(iSrcCount));
			            		addDetails(strComtLog," - Target Line No. : " + (iLineNumber + 1) + " - " + trgList.get(iLineNumber));
			            		addDetails(strComtLog,"   Following are the Details - ");
			            		//arrTrg = trgList.get(iLineNumber).split(",");
			            		for(int iCnt = 0; iCnt<lFinalDiff.size(); iCnt++) {
			            			//addDetails(strComtLog,"   ** The value in Column : " + lFinalDiff.get(iCnt)) + " should be " + arrSrc[(int) lFinalDiff.get(iCnt)-1] + 
			            					//" instead of " + arrTrg[(int) lFinalDiff.get(iCnt)-1]);
			            		}
			            		addDetails(strComtLog,"");
			            	}/*else if(iMismatch == srcList.get(iSrcCount).length()) {
			            		addDetails(strComtLog," **** LINE MISSING - Line No. : " + iSrcCount + " is not present in the file from CB");
			            	}*/
			            	
			            	iSrcCount++;           	
			            	
			        	}
			        	
			        }else{
			        	addDetails(strComtLog,"**** WARNING!! - Target File not present in the CloudBridge folder; Please Check for the CloudBridge File ****");
			        	System.out.println("=================================================================================");
			        }										

					
				}
				//addDetails("=====================================================================");
				System.out.println("=====================================================================");
			}
			
		}	    
	}
	
	/*************************************************************************************************
	//Method Name: createContentList()
	//Usage: creates list with the contents
	//Created By: Cognizant Technology Solutions
	//Date of Creation: 02/25/2018
	 * @throws FileNotFoundException 
	*************************************************************************************************/	
	public static Map<Integer,String> createContentList(File file) throws FileNotFoundException{
		
		Map<Integer,String> lFileContent = new HashMap<Integer,String>();
		String sCurrentLine = null;
		Integer iCount = 0;
		BufferedReader fileBuff = new BufferedReader(new FileReader(file));
        try {
			while ((sCurrentLine = fileBuff.readLine()) != null) {
				/*if(getCharCount(sCurrentLine) > 24){
					srcList.add(sCurrentLine.substring(0, 24) + sCurrentLine.substring(26));
				}else{*/
				lFileContent.put(iCount,sCurrentLine);
				iCount = iCount + 1;
				//}			            
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lFileContent;
	}
	
	/*************************************************************************************************
	//Method Name: getCharCount()
	//Usage: get the number of characters from a line
	//Parameter: String strLine - the line in the form of a text
	//Created By: Cognizant Technology Solutions
	//Date of Creation: 02/25/2018
	**************************************************************************************************/	
	public static int getCharCount(String strLine){
		//Getting number of words in currentLine                
        String[] words = strLine.split("  ");

        //Iterating each word
        int iCharCount = 0; 
        for (String word : words)
        {
            //Updating the charCount
        	iCharCount = iCharCount + word.length();
        }
        return iCharCount;
        
	}
	
	/*************************************************************************************************
	//Method Name: createResultFolder()
	//Usage: creates the projects result folder for the run
	//Created By: Cognizant Technology Solutions
	//Date of Creation: 10/17/2013
	//************************************************************************************************/
	public static void createResultFolder(String strTime){
		
		strResFldr = relativePath + "\\executionresults\\Result_" + strTime;
		File theDir = new File(strResFldr);  // Defining Directory/Folder Name   
		try{    
		    if (!theDir.exists()){  // Checks that Directory/Folder Doesn't Exists!   
		     boolean result = theDir.mkdirs();
		     theDir.createNewFile();		   
		     if(result){   
		    	 System.out.println("Result folder created successfully - " + strResFldr);
		     }   
		    }   
		}catch(Exception e){   
		      
		}
		
				
	}
	
	public static Map<Integer,String> getColName(String strKey){
		
		Map<Integer,String> mCol = new HashMap<Integer,String>();
		
		switch(strKey)
		{
		   // case statements
		   // values must be of same type of expression
		   case "DailyActual" :
			   mCol.put(1, "Store ID");
			   mCol.put(2, "Business Date");
			   mCol.put(3, "Period No.");
			   mCol.put(4, "Week No.");
			   mCol.put(5, "Year");
			   mCol.put(6, "Day");
			   mCol.put(7, "Start Time");
			   mCol.put(8, "Index");
			   mCol.put(9, "POS Job Title");
			   mCol.put(10, "Actual Hours");
		      break; // break is optional
		   
		   case "DailySchedule" :
			   mCol.put(1, "Store ID");
			   mCol.put(2, "Business Date");
			   mCol.put(3, "Period No.");
			   mCol.put(4, "Week No.");
			   mCol.put(5, "Year");
			   mCol.put(6, "Day");
			   mCol.put(7, "Start Time");
			   mCol.put(8, "Index");
			   mCol.put(9, "Job Code");
			   mCol.put(10, "Job Type");
			   mCol.put(11, "Forecasted Hours");
			   mCol.put(12, "Scheduled Hours");
			   mCol.put(13, "Ideal Hours");
		      break; // break is optional
		      
		   case "DailyBreak" :
			   mCol.put(1, "Store ID");
			   mCol.put(2, "Business Date");
			   mCol.put(3, "Employee Number");
			   mCol.put(4, "Last 4 SSN");
			   mCol.put(5, "Employee Name");
			   mCol.put(6, "Employee Last Name");
			   mCol.put(7, "Employee Job Title");
			   mCol.put(8, "Job Code");
			   mCol.put(9, "Check In Time");
			   mCol.put(10, "Check OutTime");
			   mCol.put(11, "Is Paid Break");
			   mCol.put(12, "Is Modified");
			   mCol.put(13, "Reason");
		
		   case "DailyExtraPay" :
			   mCol.put(1, "Store ID");
			   mCol.put(2, "Business Date");
			   mCol.put(3, "Employee Number");
			   mCol.put(4, "Last 4 SSN");
			   mCol.put(5, "Employee Name");
			   mCol.put(6, "Employee Last Name");
			   mCol.put(7, "Job Code");
			   mCol.put(8, "Hours");
			   mCol.put(9, "Total");

		   case "DailyPunch" :
			   mCol.put(1, "Store ID");
			   mCol.put(2, "Business Date");
			   mCol.put(3, "Employee Number");
			   mCol.put(4, "Last 4 SSN");
			   mCol.put(5, "Employee Name");
			   mCol.put(6, "Employee Last Name");
			   mCol.put(7, "Is Minor");
			   mCol.put(8, "Employee Job Title");
			   mCol.put(9, "Job Code");
			   mCol.put(10, "Is Extra Pay");
			   mCol.put(11, "Check In Time");
			   mCol.put(12, "Check OutTime");
			   mCol.put(13, "Cash Sales ($)");
			   mCol.put(14, "Total Sales($)");
			   mCol.put(15, "Is Meal Break Waived");
			   mCol.put(16, "Total Hrs");
			   mCol.put(17, "Regular Hrs");
			   mCol.put(18, "OverTime Hrs");
			   mCol.put(19, "DoubleTime Hrs");
			   mCol.put(20, "Regular Rate");
			   mCol.put(21, "OT Rate");
			   mCol.put(22, "DT Rate");
			   mCol.put(23, "Total Pay");
			   mCol.put(24, "Total Break");
			   mCol.put(25, "Total UnPaid Break");
			   mCol.put(26, "Is Modified");
			   mCol.put(27, "Reason");
			   
		   default : 
		      // Statements
		}
		return mCol;
		
	}
	
	/*************************************************************************************************
	//Method Name: addDetails()
	//Usage: adds details to the result file
	//Parameters: 
	//Created By: Cognizant Technology Solutions
	//Date of Creation: 02/25/2018
	//************************************************************************************************/	
	public static void addDetails(String strFilePath, String strContent) throws IOException{
		
		BufferedWriter out = null;
		try  
		{
		    FileWriter fstream = new FileWriter(strFilePath, true);
		    out = new BufferedWriter(fstream);
		    out.write("\n");
		    out.write(strContent);
		}
		catch (IOException e)
		{
		    System.err.println("Error: " + e.getMessage());
		}
		finally
		{
		    if(out != null) {
		        out.close();
		    }
		}
		
	}

}
