package Clafer2FaMa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Feature {
   public String name;	
   public ArrayList<Feature> subFeatures;
   public ArrayList<Constraint> cons;
   private ArrayList<Constraint> mainCons;
   
   public int min, max;
   public int optChilds;
   
   Feature(){
	   name ="";
	   subFeatures = new ArrayList<Feature>();
	   cons = new ArrayList<Constraint>();
	   
	   mainCons = new ArrayList<Constraint>();
	   optChilds=0; //No option...
   }

     private void writeFeature(Feature current, PrintWriter pw){
	   //print current feature
        ArrayList<Feature> queue = new ArrayList<Feature>();
        Feature aux = null;
        
        pw.println("%Relationships");
        
        if (current != null){
        	queue.add(current);
        	
        	while (queue.size()!=0){
        		aux = queue.get(0);
        		queue.remove(0);
        		
        		String line = aux.name;
                String lineX = ""; 		

                if (aux.optChilds==1) //xor  
           		   lineX = "[1, 1] {"; 
        		 
        		else if (aux.optChilds==2) //or  
           		   lineX = "[1, " + aux.subFeatures.size() + "] {"; 
        		
        		int i;
        		        			
        		for(i=0; i< aux.subFeatures.size(); i++){
        			if (i==0)
        				line += ": " + lineX;
        			
                    Feature subAux = aux.subFeatures.get(i);
                    
                    if (aux.optChilds==0 && subAux.min==0 && subAux.max==1)
                   	   line += " [" + subAux.name + "]";
                    else
                       line += " " + subAux.name;
                    
        			queue.add(aux.subFeatures.get(i));
        		}        		

        		if (aux.optChilds==1 || aux.optChilds==2) //xor - or?  
            	   line += "}";
        		
        		line +=";";
        		
        		if (i > 0)
        		   pw.println(line);
        	}
        }	   
   }
   
   private void writeConstraint(Feature mFeature, PrintWriter pw){
	   pw.println("");
	   pw.println("%Constraints");
	   
	   for(int i=0; i < mFeature.mainCons.size(); i++){
		   pw.println(mFeature.mainCons.get(i).constraint);
	   }
   }
   
   public void writeFaMa(String aFileName){   
	   try{

		   PrintWriter writer = new PrintWriter(aFileName, "UTF-8");
			    
	       writeFeature(this, writer);
	       writeConstraint(this, writer);
	       
	       writer.close();
	   }	   
	   catch(Exception ex){}
   }

   public void readFile(String aFileName, Charset ENCODING) throws IOException {
	    Path path = Paths.get(aFileName);

	    try (Scanner scanner =  new Scanner(path, ENCODING.name())){
	        if (scanner.hasNextLine()){
	    	    readLine(scanner, "", this);
	        }	            
	    }
   }
	       
   private String readConstraint(Scanner scanner, Feature mFeature){
	   int swCons=0;
	   String lineC="";
	   int i=0;
	   
	   try{
	     while(swCons==0){
	         lineC =  scanner.nextLine(); 
		   
	         if (lineC.contains("[") && lineC.contains("]")){ //Starts Constraint and Ends Constraint at the same line
	      	   int sCons = lineC.lastIndexOf("[") + 1;
	    	   int eCons = lineC.lastIndexOf("]") - 1;
	 		  
	    	   Constraint cons = new Constraint();
	    	   cons.constraint = lineC.substring(sCons, eCons).replaceAll("\\s+","");
	    	   
	    	   if (!cons.isEmpty()){ 
	    		   this.cons.add(cons);
	    	       mFeature.mainCons.add(cons);
	    	   }
	    	   
	    	   swCons = 1;
	         }

	         else if (lineC.contains("[")){ //Starts Constraint 
	    	   int sCons = lineC.lastIndexOf("[")+1;
	    	   
	    	   Constraint cons = new Constraint();
	    	   cons.constraint = lineC.substring(sCons).replaceAll("\\s+","");
	    	   
	    	   if (!cons.isEmpty()){ 
	    		   this.cons.add(cons);
	    	       mFeature.mainCons.add(cons);
	    	   }
	         }
	   
	         else if (lineC.contains("]")){ //Starts Constraint 
	    	   int eCons = lineC.lastIndexOf("]") - 1;
	    	   
	    	   Constraint cons = new Constraint();
	    	   cons.constraint = lineC.substring(0, eCons).replaceAll("\\s+","");
	    	   
	    	   if (!cons.isEmpty()){ 
	    		   this.cons.add(cons);
	    	       mFeature.mainCons.add(cons);
	    	   }
	          
	    	   swCons = 1;
	         }		      
	         else{
	           if (i==0) //No Contraints!!!!
	        	   swCons = 2;
	           else
	             {
	    	       Constraint cons = new Constraint();
	    	       cons.constraint = lineC.replaceAll("\\s+","");
	    	   	    	   
	    	   
	    	      if (!cons.isEmpty()){
	        		  //pos1
	    		      int pos1=0;	    		   
	    		      while ( ((int)lineC.charAt(pos1))==32) pos1++;
	    		   
	    		     //pos2
	    		     int pos2=lineC.length()-1;
	    		     while ( ((int)lineC.charAt(pos2))==32) pos2--;
	    		   
	    		     cons.constraint=lineC.substring(pos1,pos2+1);
	    		   
	    		     this.cons.add(cons);	
	    	         mFeature.mainCons.add(cons);	    	   	
	    	     }   	    	   
	          }
	       }
	       
           i++;
	     }	
	   
	     if (swCons==1)
		   lineC="";
	     }
	   catch(Exception ex){ }
	   
	   return(lineC);
   }
   
   public String readLine(Scanner scanner, String lineAux, Feature mFeature){
       String line;
       
       line = lineAux.length() > 0 ? lineAux: scanner.nextLine(); 
       lineAux="";
       
       /////
       if (line.contains("?")){ //Optional feature 
	        this.min=0; 
	        this.max=1;  

	        featureName1(line); //Name
       }
       
       else{
      	   this.min=1; 
      	   this.max=1;        	 
       }

       ///////// Base Case 1
       if (line.contains("{ }")) //No subfeatures... 
	       featureName2(line); 
              
       /////// Recursive!!!!!
       else if (line.contains("{")){ 
       	   featureName2(line); 
      	   Boolean childFeatures = true;
           
           while(childFeatures){
    	       Feature child = new Feature();
    	       lineAux = child.readLine(scanner, lineAux, mFeature);
    	       this.subFeatures.add(child);
    	              	       
       	       lineAux = (lineAux == "") ? scanner.nextLine() : lineAux; 
      	   
       	       if (lineAux.replaceAll("\\s+","").length()==1 && lineAux.replaceAll("\\s+","").contains("}"))
       	          childFeatures=false;
           }
        }
       
       line = readConstraint(scanner, mFeature);
       
       return(line);
   }
   
   Boolean emptyName(){
	   return this.name.compareTo("")==0 ? true: false; 
   }
   
   private void featureName1(String line){
	   int p1=0;	    		   
	   while ( ((int)line.charAt(p1))==32) p1++;
	
	   int p2 = line.lastIndexOf("?");
	  
	   String namef = line.substring(p1, p2);
	   
	   if (namef.startsWith("xor ")){
		   this.optChilds=1;
		   namef=namef.substring(4);
	   }
	   
	   else if (namef.startsWith("or ")){
		   this.optChilds=2;
		   namef=namef.substring(3);
	   }		   
	   
	   this.name = namef;
   }
   
   private void featureName2(String line){
	   if (this.emptyName()){
		   int p1=0;	    		   
		   while ( ((int)line.charAt(p1))==32) p1++;

		   int p2 = line.lastIndexOf("{");
	   
 	       String namef = line.substring(p1, p2-1);
	      
		   if (namef.startsWith("xor ")){
			   this.optChilds=1;
			   namef=namef.substring(4);
			   this.min=max=1;
		   }
		   
		   else if (namef.startsWith("or ")){
			   this.optChilds=2;
			   namef=namef.substring(3);
			   this.min=max=1;
		   }		   

	      this.name = namef;
	   }
   }
}