package ese.gnomemsr2009;
import java.text.DecimalFormat;
import java.io.*;
import java.util.*;

 public class ReadCSVFile_v2{
   public void start_opt16(String s) {
     try {
      System.out.println("path name =>"+s); 
      Scanner sc=new Scanner(System.in);
      System.out.print("Enter Path of product-names.csv file => ");
     // String str=sc.nextLine();
      
      // String csvFile = "D:/surajit1/gnome-21-30/";
      //String csvFile = str+"product-names.csv"; 
      String csvFile = s+"/product-names.csv";
      BufferedReader br = new BufferedReader(new FileReader(csvFile));
      String line = "",line1="";
      StringTokenizer st = null;

      int lineNumber = 0; 
      int tokenNumber = 0;
      int totcsvwriter = 0;
      line = br.readLine();
      while ((line = br.readLine()) != null) 
        {
         st = new StringTokenizer(line, ",");
         String fname=st.nextToken();
         String filename=s+"/"+fname+"/"+fname+"-DCN.net";
         BufferedReader br1 = new BufferedReader
                              (new FileReader(filename));
         String filename_csv=s+"/"+fname+"/"+fname+"-dev-details.csv";
         BufferedReader br2 = new BufferedReader
                              (new FileReader(filename_csv));
         BufferedReader br3 = new BufferedReader
                              (new FileReader(filename_csv));
         String heading1=br3.readLine();
         heading1=heading1.substring(heading1.indexOf(',')+1);
         heading1=heading1+",";

         String output_filename_csv=s+"/"+fname+"/"+fname+"-vertex-pairs.csv";
         FileWriter csvwriter = new FileWriter(output_filename_csv);
         String heading ="vertex.1.id , vertex.2.id , edge.weight , vertex.1.bugs.owned , vertex.2.bugs.owned , vertex.1.bugs.commented , vertex.2.bugs.commented , vertex.1.comment.span , vertex.2.comment.span , vertex.1.comments.on.owned , vertex.2.comments.on.owned , vertex.1.comments.on.nonowned , vertex.2.comments.on.nonowned , vertex.1.noof.activities , vertex.2.noof.activities , vertex.1.average.elapsed.time , vertex.2.average.elapsed.time , vertex.1.median.elapsed.time , vertex.2.median.elapsed.time , vertex.1.average.interest.span , vertex.2.average.interest.span , vertex.1.median.interest.span , vertex.2.median.interest.span \n";
         csvwriter.append(heading);
         
         String output_filename_csv_correlation=s+"/"+fname+"/"+fname+"-vertex-pairs-correlation.csv";
         PrintWriter csvwriter_correlation = new PrintWriter(new BufferedWriter(new FileWriter(output_filename_csv_correlation)));
         int totrecord=0;
         
         String line_DCN_metrics="";
         int int_line_DCN_metrics=0;
         String DCN_metrics=s+"/"+fname+"/"+fname+"-DCN-metrics.csv";
         BufferedReader br5 = new BufferedReader
                              (new FileReader(DCN_metrics));
         line_DCN_metrics=br5.readLine();
         String heading_DCN_metrics=line_DCN_metrics.substring(line_DCN_metrics.indexOf(','));
         heading_DCN_metrics=heading_DCN_metrics.substring(1);
         BufferedReader br6 = new BufferedReader
                              (new FileReader(DCN_metrics));
         line_DCN_metrics=br6.readLine();
         line1=br2.readLine();
         StringTokenizer st3 = new StringTokenizer( line1, "," );
         int totcolumn=st3.countTokens();
         while ((line1 = br2.readLine()) != null) {
             totrecord++;
         }
         String filename_csv_totrecord[][]=new String[totrecord][totcolumn];
         int r1=0,c1=0;
         while ((line1 = br3.readLine()) != null) {
             StringTokenizer st2 = new StringTokenizer( line1, "," );
             int totrec=st2.countTokens();
             for(int i=1;i<=totrec;i++)
                {
                     String s1=st2.nextToken();
                     filename_csv_totrecord[r1][c1++]=s1;
                }
             r1++;
             c1=0;
         }
         int flag=0;
         while ((line1 = br1.readLine()) != null) {
             if(line1.compareToIgnoreCase("*Edges") == 0)
                flag=1;
             if(flag == 1 && (line1.compareToIgnoreCase("*Edges")!=0))
                {
                    StringTokenizer st1 = new StringTokenizer( line1, "\t" );
                    //String[] line2 = line1.split(",");
                    int cell1=Integer.parseInt(st1.nextToken());
                    int cell2=Integer.parseInt(st1.nextToken());
                    String ff3=st1.nextToken();
                    //System.out.println(cell1+"\t"+cell2);
                    String spc="        ";
                    String rec1_l=filename_csv_totrecord[cell1-1][0];
                    String rec1_r=filename_csv_totrecord[cell2-1][0];
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
                    String finl=rec1_l+","+rec1_r+","+ff3+","+rec2_l+","+rec2_r+","+rec3_l+","+rec3_r+","+
                                rec4_l+","+rec4_r+","+rec5_l+","+rec5_r+","+rec6_l+","+rec6_r+","+
                                rec7_l+","+rec7_r+","+rec8_l+","+rec8_r+","+rec9_l+","+rec9_r+","+
                                rec10_l+","+rec10_r+","+rec11_l+","+rec11_r+"\n";
                    totcsvwriter=totcsvwriter+1;
                    csvwriter.append(finl);
                }
            }
          csvwriter.flush();
          csvwriter.close();  
          String filename_csvwriter1=s+"/"+fname+"/"+fname+"-vertex-pairs.csv";
          BufferedReader br4 = new BufferedReader
                              (new FileReader(filename_csvwriter1));
         
         if(totcsvwriter>0)
         {
         double corel_arr[][]=new double[totcsvwriter][20];
         line1=br4.readLine();
         int r2=0;
         int c2=0;
         while ((line1 = br4.readLine()) != null) {
             //System.out.println(line1);
             StringTokenizer st2 = new StringTokenizer( line1, "," );
             int totrec=st2.countTokens();
             //System.out.println("Total column=>"+totrec);
             
             for(int i=1;i<=totrec;i++)
                {
                    if(i>3)
                        {
                            String s1=st2.nextToken();
                            corel_arr[r2][c2]=Double.parseDouble(s1);
                            //System.out.print("+++++++++=>"+corel_arr[r2][c2]+"\t"+c2+"\t");
                            c2++;
                        }
                    else
                        st2.nextToken();
                }
             //System.out.println();
             r2++;
             c2=0;
         }
         //end
         function_use fuse=new function_use();
         //Call function for corelation coefficiant
         double res[][]=new double[totcsvwriter][2];
         for(int i=0;i<20;i+=2)
                    {
                      for(int j=0;j<totcsvwriter;j++)
                        {
                            res[j][0]=corel_arr[j][i];
                            res[j][1]=corel_arr[j][i+1];
                        }
                      String wrd=heading1.substring(0,heading1.indexOf(','));
                      heading1=heading1.substring(heading1.indexOf(',')+1);
                      String numberAsString = String.valueOf(fuse.c_calc(res,totcsvwriter));
                      csvwriter_correlation.write(wrd+","+numberAsString+"\n");
                   }
         //end function
         while ((line_DCN_metrics = br5.readLine()) != null) {
                           int_line_DCN_metrics++;
                        }
         double DCN_metrics_array[][]=new double[int_line_DCN_metrics][6];
         int r_DCN_metrics_array=0,c_DCN_metrics_array=0;
                     
         while ((line_DCN_metrics = br6.readLine()) != null) {
              StringTokenizer st2 = new StringTokenizer( line_DCN_metrics, "," );
              int totrec=st2.countTokens();
              for(int i1=1;i1<=totrec;i1++)
                      {
                      String wrd=st2.nextToken();
                      if(wrd.compareTo("NA") == 0)
                              wrd="0";
                      if(i1>=3)
                           {
                            DCN_metrics_array[r_DCN_metrics_array][c_DCN_metrics_array]=Double.parseDouble(wrd);
                            c_DCN_metrics_array++;
                           }
                      }
              r_DCN_metrics_array++;
              c_DCN_metrics_array=0;
             }
         double corel_DCN_metrics_array[][]=new double[int_line_DCN_metrics][2];
         int r_DCN_metrics=0,c_DCN_metrics=0;
         for(int j1=0;j1<6;j1+=2)
            {
                for(int k1=0;k1<int_line_DCN_metrics;k1++)
                    {
                        corel_DCN_metrics_array[k1][0]=DCN_metrics_array[k1][j1];
                        corel_DCN_metrics_array[k1][1]=DCN_metrics_array[k1][j1+1];
                    }
                String numberAsString1 = String.valueOf(fuse.c_calc(corel_DCN_metrics_array,int_line_DCN_metrics));
                String heading_DCN=heading_DCN_metrics.substring(0,heading_DCN_metrics.indexOf(','));
                heading_DCN_metrics=heading_DCN_metrics.substring(heading_DCN_metrics.indexOf(',')+1);
                heading_DCN=heading_DCN.substring(1,heading_DCN.length()-1);
                csvwriter_correlation.write(heading_DCN+","+numberAsString1+"\n");
               }
          }
         csvwriter_correlation.close();
         totcsvwriter=0;
         
       }
       System.err.println("Record Update.......");
     } catch (Exception e) {
       System.err.println("CSV file cannot be read : " + e);
     }
   }

 }