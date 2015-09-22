package ese.gnomemsr2009;
import java.io.*;
import java.util.*;

import au.com.bytecode.opencsv.CSVReader;

 public class ReadCSVFile_v2{

   public void start_opt16() {
	   

     try { 
    	 System.out.println("Start op16....");     
      // String csvFile = "D:/surajit/product-names.csv";
     // E:\Surajit\gnome_sept
      String csvFile = "D:/surajit/gnome_new_sept/test-files-new/product-names.csv";
       //create BufferedReader to read csv file
       BufferedReader br = new BufferedReader(new FileReader(csvFile));
       String line = "",line1="";
       StringTokenizer st = null;
       
       int lineNumber = 0; 
       int tokenNumber = 0;
       line = br.readLine();
       //read comma separated file line by line
       while ((line = br.readLine()) != null) {
         //use comma as token separator
         st = new StringTokenizer(line, ",");
         
         //display csv values
         //String filename=st.nextToken()+"-DCN.net";
         String fname=st.nextToken();
         String filename="D:/surajit/gnome_new_sept/test-files-new/"+fname+"/"+fname+"-DCN.net";
         BufferedReader br1 = new BufferedReader
                              (new FileReader(filename));
         String filename_csv="D:/surajit/gnome_new_sept/test-files-new/"+fname+"/"+fname+"-dev-details.csv";
         BufferedReader br2 = new BufferedReader
                              (new FileReader(filename_csv));
         BufferedReader br3 = new BufferedReader
                              (new FileReader(filename_csv));
         String output_filename_csv="D:/surajit/gnome_new_sept/test-files-new/"+fname+"/"+fname+"-vertex-pairs.csv";
         FileWriter csvwriter = new FileWriter(output_filename_csv);
         
         int totrecord=0;
         
         line1=br2.readLine();
         StringTokenizer st3 = new StringTokenizer( line1, "," );
         int totcolumn=st3.countTokens();
         while ((line1 = br2.readLine()) != null) {
             totrecord++;
         }
         //totrecord--;
         String filename_csv_totrecord[][]=new String[totrecord][totcolumn];
         int r1=0,c1=0;
         line1=br3.readLine();
         
         while ((line1 = br3.readLine()) != null) {
             StringTokenizer st2 = new StringTokenizer( line1, "," );
             int totrec=st2.countTokens();
             //System.out.println("Total column=>"+totrec);
             for(int i=1;i<=totrec;i++)
                {
                   /* if(i!=1)
                        {*/
                            String s1=st2.nextToken();
                            //System.out.print("=>"+s1+"\t");
                            filename_csv_totrecord[r1][c1++]=s1;
                 /*       }
                    else
                        st2.nextToken();*/
                }
             System.out.println();
             r1++;
             c1=0;
         }
         
         
         System.out.println(filename);
         
         System.out.println(filename_csv);
       //  CSVReader csvfile=new CSVReader(new FileReader(filename_csv));
       //  List contents=csvfile.readAll();
       //  System.out.println("Total record =>"+contents.size());
         System.out.println("Total record =>"+totrecord+"\t"+totcolumn);
         for(int i=0;i<totrecord;i++)
            {
                for(int j=0;j<totcolumn;j++)
                    System.out.print(filename_csv_totrecord[i][j]+"\t");
                System.out.println();
            }
         
         int flag=0;
         
         //List graph = new ArrayList<>();

         while ((line1 = br1.readLine()) != null) {
           //  System.out.println("Test point ..."); 
             if(line1.compareToIgnoreCase("*Edges") == 0)
                flag=1;
             if(flag == 1 && (line1.compareToIgnoreCase("*Edges")!=0))
                {
                    StringTokenizer st1 = new StringTokenizer( line1, "\t" );
                    //String[] line2 = line1.split(",");
                    int cell1=Integer.parseInt(st1.nextToken());
                    int cell2=Integer.parseInt(st1.nextToken());
                    System.out.println(cell1+"\t"+cell2);
                    String spc="        ";
                    String rec1=filename_csv_totrecord[cell1-1][0]+spc+filename_csv_totrecord[cell2-1][0];
                    String rec2_l=filename_csv_totrecord[cell1-1][1];
                    String rec2_r=filename_csv_totrecord[cell2-1][1];
                    String rec3_l=filename_csv_totrecord[cell1-1][2];
                    String rec3_r=filename_csv_totrecord[cell2-1][2];
                    String rec4_l=filename_csv_totrecord[cell1-1][3];
                    String rec4_r=filename_csv_totrecord[cell2-1][3];
                    String rec5_l=filename_csv_totrecord[cell1-1][4];
                    String rec5_r=filename_csv_totrecord[cell2-1][4];
                    String rec6_l=filename_csv_totrecord[cell1-1][5];
                    String rec6_r=filename_csv_totrecord[cell2-1][5];
                    String rec7_l=filename_csv_totrecord[cell1-1][6];
                    String rec7_r=filename_csv_totrecord[cell2-1][6];
                    String rec8_l=filename_csv_totrecord[cell1-1][7];
                    String rec8_r=filename_csv_totrecord[cell2-1][7];
                    String rec9_l=filename_csv_totrecord[cell1-1][8];
                    String rec9_r=filename_csv_totrecord[cell2-1][8];
                    String rec10_l=filename_csv_totrecord[cell1-1][9];
                    String rec10_r=filename_csv_totrecord[cell2-1][9];
                    String rec11_l=filename_csv_totrecord[cell1-1][10];
                    String rec11_r=filename_csv_totrecord[cell2-1][10];
                    
                    String finl=rec1+","+rec2_l+","+rec2_r+","+rec3_l+","+rec3_r+","+
                    			rec4_l+","+rec4_r+","+rec5_l+","+rec5_r+","+rec6_l+","+rec6_r+","+
                    			rec7_l+","+rec7_r+","+rec8_l+","+rec8_r+","+rec9_l+","+rec9_r+","+
                    			rec10_l+","+rec10_r+","+rec11_l+","+rec11_r+"\n";
                    csvwriter.append(finl);
                    //System.out.println(rec1+","+rec2+","+rec3+","+rec4+","+rec5+","+rec6+","+rec7+","+rec8+","+rec9+","+rec10+","+rec11);
                    //graph.add(line2);
                    //System.out.println(line1);
                }
            }
         
         csvwriter.close();  
      /*   for (int i = 0; i < graph.size(); i++) {
             System.out.println(graph.get(i));
            }*/

       }

     } catch (Exception e) {
       System.err.println("CSV file cannot be read : " + e);
     }
   }

 }

