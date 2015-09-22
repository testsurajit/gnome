package ese.gnomemsr2009;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;





import java.util.ArrayList;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.REngine.REngine;


class TextConsole implements RMainLoopCallbacks
{
    public void rWriteConsole(Rengine re, String text, int oType) {
        System.out.print(text);
    }
    
    public void rBusy(Rengine re, int which) {
        System.out.println("rBusy("+which+")");
    }
    
    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
        System.out.print(prompt);
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String s=br.readLine();
            return (s==null||s.length()==0)?s:s+"\n";
        } catch (Exception e) {
            System.out.println("jriReadConsole exception: "+e.getMessage());
        }
        return null;
    }
    
    public void rShowMessage(Rengine re, String message) {
        System.out.println("rShowMessage \""+message+"\"");
    }
    
    public String rChooseFile(Rengine re, int newFile) {
    FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
    fd.show();
    String res=null;
    if (fd.getDirectory()!=null) res=fd.getDirectory();
    if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
    return res;
    }
    
    public void   rFlushConsole (Rengine re) {
    }
    
    public void   rLoadHistory  (Rengine re, String filename) {
    }            
    
    public void   rSaveHistory  (Rengine re, String filename) {
    }            
}
public class RFunctions 
{
    String textToAppend;
    Rengine re;
    TextConsole tc;
    
    private ArrayList<String> transformedVars = new ArrayList<String>();
    private ArrayList<String> transformedRVars= new ArrayList<String>();
    private ArrayList<String> colNames           = new ArrayList<String>();
    private boolean boo = false;
    
    public RFunctions()
    {
        textToAppend = "";
    }
    
    public String getTextToAppend()
    {
        return textToAppend;
    }
    
    public void setTextToAppend(String s)
    {
        textToAppend = s;
    }
    
    public ArrayList<String> nwParameters(String dirName, String productName, ArrayList<String> devEmail, boolean danOrDCN) throws Exception
    {
        re.eval("if(\"blockmodeling\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"blockmodeling\")}");
        re.eval("if(\"igraph\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"igraph\")}");
        re.eval("if(\"Matrix\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"Matrix\")}");
        re.eval("library('blockmodeling')");
        re.eval("library('igraph')");
        re.eval("library('Matrix')"); 
        
        dirName = dirName.replaceAll("\\\\", "/");
        ArrayList<String> networkParameters = new ArrayList<String>();
        
        if(danOrDCN) re.eval("dcn = loadnetwork(\""+dirName+"/"+productName+"/"+productName+"-DCN.net\")");
        if(!danOrDCN) re.eval("dcn = loadnetwork(\""+dirName+"/"+productName+"/"+productName+"-DAN.net\")");
        
        re.eval("dcnGraphWeighted = 0");
        re.eval("dcnGraph         = 0");
        re.eval("Degree = 0");
        re.eval("Betweenness = 0");
        re.eval("Clustcoef = 0");
        re.eval("Closeness = 0");
        re.eval("Eigencentrality = 0");
        re.eval("Pagerank = 0");
        
        re.eval("dcnGraphWeighted = graph.adjacency(dcn, mode=c(\"undirected\"), weighted=TRUE)");
        re.eval("dcnGraph         = graph.adjacency(dcn, mode=c(\"undirected\"))");
        re.eval("Degree = as.data.frame(degree(dcnGraph))");
        re.eval("Betweenness = as.data.frame(betweenness(dcnGraph))");
        re.eval("Clustcoef = as.data.frame(transitivity(dcnGraph, type=c(\"local\"), isolates=c(\"zero\")))");
        re.eval("Closeness = as.data.frame(closeness(dcnGraph, mode=c(\"all\"), normalized=TRUE))");
        re.eval("Eigencentrality = as.data.frame(evcent(dcnGraph)$vector)");
        re.eval("Pagerank = as.data.frame(page.rank(dcnGraph, directed=FALSE)$vector)");
        
        re.eval("Closeness[, c(\"Clustcoef\")] = Clustcoef");
        
        re.eval("bnd = merge(Degree, Betweenness, by=\"row.names\")");
        re.eval("bnd = merge(bnd, Closeness, by.x=\"Row.names\", by.y=\"row.names\")");
        re.eval("bnd = merge(bnd, Eigencentrality, by.x=\"Row.names\", by.y=\"row.names\")");
        re.eval("bnd = merge(bnd, Pagerank, by.x=\"Row.names\", by.y=\"row.names\")");
        
        re.eval("colnames(bnd) <- c(\"Developers\", \"Degree\", \"Betweenness\", \"Closeness\", \"Clustcoef\", \"Eigencentrality\", \"Pagerank\")");
        
        for(int j = 0; j < devEmail.size(); j++)
        {
            re.eval("devInf <- data.frame(a=0, b=0, c=0, d=0, e=0, f=0, g=0)");
            re.eval("if(Pagerank != 0) devInf <- subset(bnd, bnd$Developers == '"+devEmail.get(j)+"')");
        
            //re.eval("devInf <- subset(bnd, bnd$Developers == '"+devEmail+"')");
            //re.eval("write.csv(bnd, file=\"db-metrics.csv\")");
        
            String b = "";
            
            REXP x = re.eval("devInf[, 2:7]");
            RList vl = x.asList();
            String[] k = vl.keys();
            String[] m = new String[k.length];
            
            if (k!=null) {
                int i=0; while (i<k.length) m[i] = "" + vl.at(k[i++]);
            }    
            
            if (m!=null)
            {
                int i=0;
                while(i<m.length)
                {
                    String a = "";
                    float f = 0.0f;
                    
                    
                    a = m[i].substring(8, m[i].length()-2);
                    
                    if(a.isEmpty())
                        a = "0";
                    
                    f = Float.parseFloat(a);
                    
                    if(i==5)
                    {
                        b = b + f;
                    } else
                    {
                        b = b + f + ", ";
                    }
                    
                    i++;
                }
            }
            networkParameters.add(b);
        }
        
        
        
        return networkParameters;
    }
    
    /*
     * Input: Directory to product-names.csv
     * Output: network metrics to be appended to project summary
     */
    public String summaryMetrics(String dirName, String productName, boolean dcnOrDAN)
    {
        re.eval("if(\"blockmodeling\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"blockmodeling\")}");
        re.eval("if(\"igraph\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"igraph\")}");
        re.eval("if(\"Matrix\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"Matrix\")}");
        re.eval("library('blockmodeling')");
        re.eval("library('igraph')");
        re.eval("library('Matrix')");
        
        dirName = dirName.replaceAll("\\\\", "/");
        
        re.eval("cent.Degree = 0");
        
        //test
        System.out.println("===="+re.eval("cent.Degree = 0"));
        //test
                
        
        re.eval("cent.Betweenness = 0");
        re.eval("cent.closeness = 0");
        re.eval("cent.EVcent = 0");
        re.eval("transitivity.global = 0");
        re.eval("assortativity = 0");
        re.eval("diameter = 0");
        re.eval("density = 0");
        re.eval("modularity = 0");
        re.eval("avg.PathLength = 0");
        re.eval("avg.Degree = 0");
        
        if(dcnOrDAN) 
        {
            System.out.println("dcn or dan");
            re.eval("dcn = loadnetwork(\""+dirName+"/"+productName+"/"+productName+"-DCN.net\")");
        }
        else if(!dcnOrDAN) 
            //below line is set as remark but originally is not remark
            //if(dcnOrDAN)
                {
                System.out.println("not dcn or dan");
                re.eval("dcn = loadnetwork(\""+dirName+"/"+productName+"/"+productName+"-DAN.net\")");
                }
        
        re.eval("dcnGraph= graph.adjacency(dcn, mode=c(\"undirected\"))");
        
        re.eval("cent.Degree = centralization.degree(dcnGraph)$centralization");
        re.eval("cent.Betweenness = centralization.betweenness(dcnGraph)$centralization");
        re.eval("cent.closeness = centralization.closeness(dcnGraph)$centralization");
        re.eval("cent.EVcent = centralization.evcent(dcnGraph)$centralization");
        re.eval("transitivity.global = transitivity(dcnGraph, type=c('global'))");
        re.eval("assortativity = assortativity.degree(dcnGraph)");
        re.eval("diameter = diameter(dcnGraph)");
        re.eval("density = graph.density(dcnGraph, loop=TRUE)");
        re.eval("modularity = modularity(walktrap.community(dcnGraph))");
        re.eval("avg.PathLength = average.path.length(dcnGraph)");
        re.eval("avg.Degree = (2 * ecount(dcnGraph))/vcount(dcnGraph)");
        
        re.eval("rm(dcn)");
        re.eval("rm(dcnGraph)");
        
        
        re.eval("productSum = data.frame(cent.Degree, cent.Betweenness, cent.closeness, cent.EVcent, transitivity.global, assortativity, diameter, density, modularity, avg.PathLength, avg.Degree)");
        
        String b = "";
        
        REXP x = re.eval("productSum[, 1:11]");
        RList vl = x.asList();
        String[] k = vl.keys();
        String[] m = new String[k.length];
        
        if (k!=null) {
            int i=0; while (i<k.length) m[i] = "" + vl.at(k[i++]);
        }    
        
        if (m!=null)
        {
            int i=0;
            while(i<m.length)
            {
                String a = "";
                float f = 0.0f;
                
                
                a = m[i].substring(8, m[i].length()-2);
                
                if(a.isEmpty())
                    a = "0";
                
                f = Float.parseFloat(a);
                
                if(i==10)
                {
                    b = b + f;
                } else
                {
                    b = b + f + ", ";
                }
                
                i++;
            }
        }
        //test
        System.out.println("b=>"+b);
        //test
        return b;
        
    }
    
    /*Input: Directory to csv files and product name
     *Output: DCN-metrics.csv for each product I.E Degree and betweenness
     */
    public void DCNMetrics(String s, String prodName)
    {
        re.eval("if(\"blockmodeling\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"blockmodeling\")}");
        re.eval("if(\"igraph\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"igraph\")}");
        re.eval("if(\"Matrix\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"Matrix\")}");
        re.eval("library('blockmodeling')");
        re.eval("library('igraph')");
        re.eval("library('Matrix')");
        
        s=s.replaceAll("\\\\", "/");
        
        re.eval("dcn = loadnetwork(\""+s+"/"+prodName+"/"+prodName+"-DCN.net\")");
        
        re.eval("dcnGraphWeighted = graph.adjacency(dcn, mode=c(\"undirected\"), weighted=TRUE)");
        re.eval("dcnGraph         = graph.adjacency(dcn, mode=c(\"undirected\"))");
        re.eval("Degree = as.data.frame(degree(dcnGraph))");
        re.eval("Betweenness = as.data.frame(betweenness(dcnGraph))");
        
        re.eval("Clustcoef = as.data.frame(transitivity(dcnGraph, type=c(\"local\")))");
        re.eval("Closeness = as.data.frame(closeness(dcnGraph, mode=c(\"all\"), normalized=TRUE))");
        re.eval("Eigencentrality = as.data.frame(evcent(dcnGraph)$vector)");
        re.eval("Pagerank = as.data.frame(page.rank(dcnGraph, directed=FALSE)$vector)");
        
        re.eval("Closeness[, c(\"Clustcoef\")] = Clustcoef");
        
        re.eval("bnd = merge(Degree, Betweenness, by=\"row.names\")");
        re.eval("bnd = merge(bnd, Closeness, by.x=\"Row.names\", by.y=\"row.names\")");
        re.eval("bnd = merge(bnd, Eigencentrality, by.x=\"Row.names\", by.y=\"row.names\")");
        re.eval("bnd = merge(bnd, Pagerank, by.x=\"Row.names\", by.y=\"row.names\")");
        
        re.eval("colnames(bnd) <- c(\"Developers\", \"dcn.degree\", \"dcn.betweenness\", \"dcn.closeness\", \"dcn.clustcoeff\", \"dcn.eigencentrality\", \"dcn.pagerank\")");
        re.eval("write.csv(bnd, file=\""+s+"/"+prodName+"/"+prodName+"-DCN-metrics.csv\")");
    }
    
    /*Input: Directory to csv files and product name
     *Output: DAN-metrics.csv for each product I.E Degree and betweenness
     */
    public void DANMetrics(String s, String prodName)
    {
        re.eval("if(\"blockmodeling\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"blockmodeling\")}");
        re.eval("if(\"igraph\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"igraph\")}");
        re.eval("if(\"Matrix\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"Matrix\")}");
        re.eval("library('blockmodeling')");
        re.eval("library('igraph')");
        re.eval("library('Matrix')");
        
        s=s.replaceAll("\\\\", "/");
        
        re.eval("dcn = loadnetwork(\""+s+"/"+prodName+"/"+prodName+"-DAN.net\")");
        
        re.eval("dcnGraphWeighted = graph.adjacency(dcn, mode=c(\"undirected\"), weighted=TRUE)");
        re.eval("dcnGraph         = graph.adjacency(dcn, mode=c(\"undirected\"))");
        re.eval("Degree = as.data.frame(degree(dcnGraph))");
        re.eval("Betweenness = as.data.frame(betweenness(dcnGraph))");
        
        re.eval("Clustcoef = as.data.frame(transitivity(dcnGraph, type=c(\"local\")))");
        re.eval("Closeness = as.data.frame(closeness(dcnGraph, mode=c(\"all\"), normalized=TRUE))");
        re.eval("Eigencentrality = as.data.frame(evcent(dcnGraph)$vector)");
        re.eval("Pagerank = as.data.frame(page.rank(dcnGraph, directed=FALSE)$vector)");
        
        re.eval("Closeness[, c(\"Clustcoef\")] = Clustcoef");
        
        re.eval("bnd = merge(Degree, Betweenness, by=\"row.names\")");
        re.eval("bnd = merge(bnd, Closeness, by.x=\"Row.names\", by.y=\"row.names\")");
        re.eval("bnd = merge(bnd, Eigencentrality, by.x=\"Row.names\", by.y=\"row.names\")");
        re.eval("bnd = merge(bnd, Pagerank, by.x=\"Row.names\", by.y=\"row.names\")");
        
        re.eval("colnames(bnd) <- c(\"Developers\", \"dan.degree\", \"dan.betweenness\", \"dan.closeness\", \"dan.clustcoeff\", \"dan.eigencentrality\", \"dan.pagerank\")");
        re.eval("write.csv(bnd, file=\""+s+"/"+prodName+"/"+prodName+"-DAN-metrics.csv\")");
    }
    
    /* Input: Model type(Developer/bug), dependent and independent variable(s) and directory and product name
     * Output: Summary of the regression in csv
     */
    public void linRegression(String model, ArrayList<String> variables, ArrayList<String> transform, String s, String prodName)
    {
        int noOfVar = variables.size();
        String indVars = "";
        String transVars = "";
        
        transformVariables(variables, transform);
        
        boolean boo = false;
        
        for(int i = 0; i < noOfVar; i++)
        {
            if(colNames.get(i).equals(variables.get(i).replace("-", ".")))
            {
                boo = boo||false;
            } else
            {
                boo = boo||true;
            }
        }
        
        re.eval("if(\"blockmodeling\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"blockmodeling\")}");
        re.eval("if(\"igraph\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"igraph\")}");
        re.eval("if(\"Matrix\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"Matrix\")}");
        re.eval("if(\"seqinr\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"seqinr\")}");
        re.eval("if(\"dplyr\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"dplyr\")}");
        re.eval("library('blockmodeling')");
        re.eval("library('igraph')");
        re.eval("library('Matrix')");
        re.eval("library('seqinr')");
        re.eval("library('dplyr')");
        
        s=s.replaceAll("\\\\", "/");
        
        for(int i = 1; i < noOfVar; i++)
        {
            if(i < noOfVar-1)
            {
                indVars = indVars + variables.get(i).replace("-", ".") + " + ";
                transVars = transVars + "`" + colNames.get(i) + "` + ";
            }
            if(i == noOfVar-1)
            {
                indVars = indVars + variables.get(i).replace("-", ".");
                transVars = transVars + "`" + colNames.get(i) +"`";
            }
        }
        
        
        if(model.equals("developer"))
        {
            re.eval("deets = read.csv(\""+s+"/"+prodName+"/"+prodName+"-dev-details.csv\")");
            
            re.eval("drops <- c(\"closeness\", \"degree\", \"betweenness\", \"clustcoeff\", \"eigencentrality\", \"pagerank\")");
            re.eval("deets = deets[,!(names(deets) %in% drops)]");
            
            File networkFile;
        
            networkFile = new File(s+"/"+prodName+"/"+prodName+"-DCN.net");
            if(networkFile.exists()) 
            {
                re.eval("dcnMetrics = read.csv(\""+s+"/"+prodName+"/"+prodName+"-DCN-metrics.csv\")");
                System.out.println("\nChecking for NA Values and Changing Them to 0.01");
                re.eval("dcnMetrics[is.na(dcnMetrics)] <- 0.01");
                re.eval("deets = merge(deets, dcnMetrics[ , 2:8], by.x=\"developer\", by.y=\"Developers\")");
            }
            
            networkFile = new File(s+"/"+prodName+"/"+prodName+"-DAN.net");
            if(networkFile.exists()) 
            {
                re.eval("danMetrics = read.csv(\""+s+"/"+prodName+"/"+prodName+"-DAN-metrics.csv\")");
                System.out.println("\nChecking for NA Values and Changing Them to 0.01");
                re.eval("danMetrics[is.na(danMetrics)] <- 0.01");
                re.eval("deets = merge(deets, danMetrics[ , 2:8], by.x=\"developer\", by.y=\"Developers\")");
            }
            
            for(int i = 0; i < noOfVar; i++)
            {
                re.eval("deets[ , c(\""+colNames.get(i)+"\")] <- "+transformedRVars.get(i));
                re.eval("deets[ , c(\""+colNames.get(i)+"\")][deets[ , c(\""+colNames.get(i)+"\")] == -Inf] <- 0.01");
            }
            re.eval("m1 <- lm(`"+colNames.get(0)+"` ~ "+transVars+", data=deets)");
        } else if(model.equals("bug"))
        {
            re.eval("deets = read.csv(\""+s+"/"+prodName+"/"+prodName+"-bug-details.csv\")");
            
            re.eval("drops <- c(\"closeness\", \"degree\", \"betweenness\", \"clustcoeff\", \"eigencentrality\", \"pagerank\")");
            re.eval("deets = deets[,!(names(deets) %in% drops)]");
            
            for(int i = 0; i < noOfVar; i++)
            {
                re.eval("deets[ , c(\""+colNames.get(i)+"\")] <- "+transformedRVars.get(i));
                re.eval("deets[ , c(\""+colNames.get(i)+"\")][deets[ , c(\""+colNames.get(i)+"\")] == -Inf] <- 0.01");
            }
            re.eval("m1 <- lm(`"+colNames.get(0)+"` ~ "+transVars+", data=deets)");
        }
        
        if(boo == true)
        {
            File theDir3 = new File(s+"/results/transformed/regression");
            if(!theDir3.exists()) theDir3.mkdirs();
            File theDir4 = new File(s+"/results/non-transformed/regression");
            if(!theDir4.exists()) theDir4.mkdirs();
            
            //re.eval("capture.output(summary(m1), file=\""+s+"/"+prodName+"/transformed/"+prodName+"-"+model+"-model-output-transformed.txt\")");
            re.eval("capture.output(summary(m1), file=\""+s+"/results/transformed/regression/"+prodName+"-"+model+"-model-output-transformed.txt\")");
            
            if(model.equals("developer"))
            {
                re.eval("deets = read.csv(\""+s+"/"+prodName+"/"+prodName+"-dev-details.csv\")");
                
                re.eval("drops <- c(\"closeness\", \"degree\", \"betweenness\", \"clustcoeff\", \"eigencentrality\", \"pagerank\")");
                re.eval("deets = deets[,!(names(deets) %in% drops)]");
                
                File networkFile;
            
                networkFile = new File(s+"/"+prodName+"/"+prodName+"-DCN.net");
                if(networkFile.exists()) 
                {
                    re.eval("dcnMetrics = read.csv(\""+s+"/"+prodName+"/"+prodName+"-DCN-metrics.csv\")");
                    System.out.println("\nChecking for NA Values and Changing Them to 0.01");
                    re.eval("dcnMetrics[is.na(dcnMetrics)] <- 0.01");
                    re.eval("deets = merge(deets, dcnMetrics[ , 2:8], by.x=\"developer\", by.y=\"Developers\")");
                }
                
                networkFile = new File(s+"/"+prodName+"/"+prodName+"-DAN.net");
                if(networkFile.exists()) 
                {
                    re.eval("danMetrics = read.csv(\""+s+"/"+prodName+"/"+prodName+"-DAN-metrics.csv\")");
                    System.out.println("\nChecking for NA Values and Changing Them to 0.01");
                    re.eval("danMetrics[is.na(danMetrics)] <- 0.01");
                    re.eval("deets = merge(deets, danMetrics[ , 2:8], by.x=\"developer\", by.y=\"Developers\")");
                }
                
                re.eval("m1 <- lm(`"+variables.get(0)+"` ~ "+indVars+", data=deets)");
            } else if(model.equals("bug"))
            {
                re.eval("deets = read.csv(\""+s+"/"+prodName+"/"+prodName+"-bug-details.csv\")");
                
                re.eval("drops <- c(\"closeness\", \"degree\", \"betweenness\", \"clustcoeff\", \"eigencentrality\", \"pagerank\")");
                re.eval("deets = deets[,!(names(deets) %in% drops)]");
                
                re.eval("m1 <- lm(`"+variables.get(0)+"` ~ "+indVars+", data=deets)");
            }
            
            re.eval("capture.output(summary(m1), file=\""+s+"/results/non-transformed/regression/"+prodName+"-"+model+"-model-output.txt\")");
        } else
        {
            File theDir2 = new File(s+"/results/non-transformed/regression");
            if (!theDir2.exists()) theDir2.mkdirs();
            //re.eval("capture.output(summary(m1), file=\""+s+"/"+prodName+"/non-transformed/"+prodName+"-"+model+"-model-output.txt\")");
            re.eval("capture.output(summary(m1), file=\""+s+"/results/non-transformed/regression/"+prodName+"-"+model+"-model-output.txt\")");
        }
        
    }
    
    /* Input: Model type(Developer/bug), dependent and independent variable(s) and directory and product name
     * Output: Description and correlation of the variables in csv
     */
    public void varDescAndCor(String model, ArrayList<String> variables, ArrayList<String> transform, String s, String prodName)
    {
        transformVariables(variables, transform);
        
        int noOfVar = variables.size();
        
        boo = false;
        
        for(int i = 0; i < noOfVar; i++)
        {
            if(colNames.get(i).equals(variables.get(i).replace("-", ".")))
            {
                boo = boo||false;
            } else
            {
                boo = boo||true;
            }
        }
        
        
        String indVars = "\""+variables.get(0).replace("-", ".")+"\", ";
        String transVars = "\""+transformedVars.get(0).replace("-", ".")+"\", ";
        String collumn = "\""+colNames.get(0).replace("-", ".")+"\", ";
        
        re.eval("if(\"blockmodeling\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"blockmodeling\")}");
        re.eval("if(\"igraph\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"igraph\")}");
        re.eval("if(\"Matrix\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"Matrix\")}");
        re.eval("if(\"psych\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"psych\")}");
        re.eval("library('blockmodeling')");
        re.eval("library('igraph')");
        re.eval("library('Matrix')");
        re.eval("library('psych')");
        
        s=s.replaceAll("\\\\", "/");
        
        if(model.equals("developer"))
        {
            re.eval("deets = read.csv(\""+s+"/"+prodName+"/"+prodName+"-dev-details.csv\")");
            
            re.eval("drops <- c(\"closeness\", \"degree\", \"betweenness\", \"clustcoeff\", \"eigencentrality\", \"pagerank\")");
            re.eval("deets = deets[,!(names(deets) %in% drops)]");
            
            File networkFile;
        
            networkFile = new File(s+"/"+prodName+"/"+prodName+"-DCN.net");
            if(networkFile.exists()) 
            {
                re.eval("dcnMetrics = read.csv(\""+s+"/"+prodName+"/"+prodName+"-DCN-metrics.csv\")");
                System.out.println("\nChecking for NA Values and Changing Them to 0.01");
                re.eval("dcnMetrics[is.na(dcnMetrics)] <- 0.01");
                re.eval("deets = merge(deets, dcnMetrics[ , 2:8], by.x=\"developer\", by.y=\"Developers\")");
            }
            
            networkFile = new File(s+"/"+prodName+"/"+prodName+"-DAN.net");
            if(networkFile.exists()) 
            {
                re.eval("danMetrics = read.csv(\""+s+"/"+prodName+"/"+prodName+"-DAN-metrics.csv\")");
                System.out.println("\nChecking for NA Values and Changing Them to 0.01");
                re.eval("danMetrics[is.na(danMetrics)] <- 0.01");
                re.eval("deets = merge(deets, danMetrics[ , 2:8], by.x=\"developer\", by.y=\"Developers\")");
            }
        } else if(model.equals("bug"))
        {
            re.eval("deets = read.csv(\""+s+"/"+prodName+"/"+prodName+"-bug-details.csv\")");
            re.eval("drops <- c(\"closeness\", \"degree\", \"betweenness\", \"clustcoeff\", \"eigencentrality\", \"pagerank\")");
            re.eval("deets = deets[,!(names(deets) %in% drops)]");
        }
        
        for(int i = 1; i < noOfVar; i++)
        {
            if(i < noOfVar-1)
            {
                indVars = indVars + "\"" + variables.get(i).replace("-", ".") + "\", ";
                transVars = transVars + "\"" + transformedVars.get(i) + "\", ";
                collumn = collumn + "\"" + colNames.get(i) + "\", ";
            }
            if(i == noOfVar-1)
            {
                indVars = indVars + "\"" + variables.get(i).replace("-", ".") + "\"";
                transVars = transVars + "\"" + transformedVars.get(i) + "\"";
                collumn = collumn + "\"" + colNames.get(i) + "\"";
            }
        }
        
        for(int i = 0; i < noOfVar; i++)
        {
            re.eval("deets[ , c(\""+colNames.get(i)+"\")] <- "+transformedRVars.get(i));
            re.eval("deets[ , c(\""+colNames.get(i)+"\")][deets[ , c(\""+colNames.get(i)+"\")] == -Inf] <- 0.01");
        }
        
        re.eval("deets2 <- deets[ ,c("+collumn+")]");
        
        re.eval("varDesc <- describe(deets2)");
        re.eval("varCor  <- cor(deets2, use=\"pairwise.complete.obs\")");
        
        //re.eval("write.csv(varDesc, file=\""+s+"/"+prodName+"/"+prodName+"-describe-transformed.csv\")");
        //re.eval("write.csv(varCor, file=\""+s+"/"+prodName+"/"+prodName+"-correlations-transformed.csv\")");
        //re.eval("write.csv(deets2, file=\""+s+"/"+prodName+"/"+prodName+"-model-parameters-transformed.csv\")");
        
        if(boo == true)
        {
            File theDir = new File(s+"/results/transformed/descriptives");
            File theDir2 = new File(s+"/results/non-transformed/descriptives");
            if (!theDir.exists()) theDir.mkdirs();
            if (!theDir2.exists()) theDir2.mkdirs();
            
            File theDir3 = new File(s+"/results/transformed/correlations");
            File theDir4 = new File(s+"/results/non-transformed/correlations");
            if (!theDir3.exists()) theDir3.mkdir();
            if (!theDir4.exists()) theDir4.mkdir();
            
            File theDir5 = new File(s+"/results/transformed/model parameters");
            File theDir6 = new File(s+"/results/non-transformed/model parameters");
            if (!theDir5.exists()) theDir5.mkdir();
            if (!theDir6.exists()) theDir6.mkdir();
            
            re.eval("write.csv(varDesc, file=\""+s+"/results/transformed/descriptives/"+prodName+"-describe-transformed.csv\")");
            re.eval("write.csv(varCor, file=\""+s+"/results/transformed/correlations/"+prodName+"-correlations-transformed.csv\")");
            re.eval("write.csv(deets2, file=\""+s+"/results/transformed/model parameters/"+prodName+"-model-parameters-transformed.csv\")");
            
            re.eval("deets3 <- deets[ ,c("+indVars+")]");
            re.eval("varDesc2 <- describe(deets2)");
            re.eval("varCor2  <- cor(deets2, use=\"pairwise.complete.obs\")");
            
            re.eval("write.csv(varDesc2, file=\""+s+"/results/non-transformed/descriptives/"+prodName+"-describe.csv\")");
            re.eval("write.csv(varCor2, file=\""+s+"/results/non-transformed/correlations/"+prodName+"-correlations.csv\")");
            re.eval("write.csv(deets3, file=\""+s+"/results/non-transformed/model parameters/"+prodName+"-model-parameters.csv\")");
            
        }else
        {
            File theDir2 = new File(s+"/results/non-transformed/descriptives");
            if (!theDir2.exists()) theDir2.mkdirs();
            
            File theDir4 = new File(s+"/results/non-transformed/correlations");
            if (!theDir4.exists()) theDir4.mkdir();
            
            File theDir6 = new File(s+"/results/non-transformed/model parameters");
            if (!theDir6.exists()) theDir6.mkdir();
            
            re.eval("write.csv(varDesc, file=\""+s+"/results/non-transformed/descriptives/"+prodName+"-describe.csv\")");
            re.eval("write.csv(varCor, file=\""+s+"/results/non-transformed/correlations/"+prodName+"-correlations.csv\")");
            re.eval("write.csv(deets2, file=\""+s+"/results/non-transformed/model parameters/"+prodName+"-model-parameters.csv\")");
        }
        
    }
    
    /* Input: Model type(Developer/bug), variables, parameters and directory and product name
     * Output: Factor analysis, eigen values and vectors, descriptives and correlations and correlation of the variables in csv
     */
    public void eigenVal(String model, ArrayList<String> variables, ArrayList<String> transform, ArrayList<String> parameters, String s, String prodName)
    {    
        transformVariables(variables, transform);
        
        int noOfVar = variables.size();
        
        boo = false;
        
        for(int i = 0; i < noOfVar; i++)
        {
            if(colNames.get(i).equals(variables.get(i).replace("-", ".")))
            {
                boo = boo||false;
            } else
            {
                boo = boo||true;
            }
        }
        
        
        String indVars = "\""+variables.get(0).replace("-", ".")+"\", ";
        String transVars = "\""+transformedVars.get(0).replace("-", ".")+"\", ";
        String collumn = "\""+colNames.get(0).replace("-", ".")+"\", ";
        
        re.eval("if(\"blockmodeling\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"blockmodeling\")}");
        re.eval("if(\"igraph\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"igraph\")}");
        re.eval("if(\"Matrix\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"Matrix\")}");
        re.eval("if(\"psych\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"psych\")}");
        re.eval("if(\"psych\" %in% rownames(installed.packages()) == FALSE) {install.packages(\"nFactors\")}");
        re.eval("library('blockmodeling')");
        re.eval("library('igraph')");
        re.eval("library('Matrix')");
        re.eval("library('psych')");
        re.eval("require('nFactors')");
        
        s=s.replaceAll("\\\\", "/");
        
        if(model.equals("developer"))
        {
            re.eval("deets = read.csv(\""+s+"/"+prodName+"/"+prodName+"-dev-details.csv\")");
            
            re.eval("drops <- c(\"closeness\", \"degree\", \"betweenness\", \"clustcoeff\", \"eigencentrality\", \"pagerank\")");
            re.eval("deets = deets[,!(names(deets) %in% drops)]");
            
            File networkFile;
        
            networkFile = new File(s+"/"+prodName+"/"+prodName+"-DCN.net");
            if(networkFile.exists()) 
            {
                re.eval("dcnMetrics = read.csv(\""+s+"/"+prodName+"/"+prodName+"-DCN-metrics.csv\")");
                System.out.println("\nChecking for NA Values and Changing Them to 0.01");
                re.eval("dcnMetrics[is.na(dcnMetrics)] <- 0.01");
                re.eval("deets = merge(deets, dcnMetrics[ , 2:8], by.x=\"developer\", by.y=\"Developers\")");
            }
            
            networkFile = new File(s+"/"+prodName+"/"+prodName+"-DAN.net");
            if(networkFile.exists()) 
            {
                re.eval("danMetrics = read.csv(\""+s+"/"+prodName+"/"+prodName+"-DAN-metrics.csv\")");
                System.out.println("\nChecking for NA Values and Changing Them to 0.01");
                re.eval("danMetrics[is.na(danMetrics)] <- 0.01");
                re.eval("deets = merge(deets, danMetrics[ , 2:8], by.x=\"developer\", by.y=\"Developers\")");
            }
            
            for(int i = 0; i < noOfVar; i++)
            {
                re.eval("deets[ , c(\""+colNames.get(i)+"\")] <- "+transformedRVars.get(i));
                re.eval("deets[ , c(\""+colNames.get(i)+"\")][deets[ , c(\""+colNames.get(i)+"\")] == -Inf] <- 0.01");
            }
        } else if(model.equals("bug"))
        {
            re.eval("deets = read.csv(\""+s+"/"+prodName+"/"+prodName+"-bug-details.csv\")");
            
            re.eval("drops <- c(\"closeness\", \"degree\", \"betweenness\", \"clustcoeff\", \"eigencentrality\", \"pagerank\")");
            re.eval("deets = deets[,!(names(deets) %in% drops)]");
        }
        
        for(int i = 1; i < noOfVar; i++)
        {
            if(i < noOfVar-1)
            {
                indVars = indVars + "\"" + variables.get(i).replace("-", ".") + "\", ";
                transVars = transVars + "\"" + transformedVars.get(i) + "\", ";
                collumn = collumn + "\"" + colNames.get(i) + "\", ";
            }
            if(i == noOfVar-1)
            {
                indVars = indVars + "\"" + variables.get(i).replace("-", ".") + "\"";
                transVars = transVars + "\"" + transformedVars.get(i) + "\"";
                collumn = collumn + "\"" + colNames.get(i) + "\"";
            }
        }
        
        for(int i = 0; i < noOfVar; i++)
        {
            re.eval("deets[ , c(\""+colNames.get(i)+"\")] <- "+transformedRVars.get(i));
            re.eval("deets[ , c(\""+colNames.get(i)+"\")][deets[ , c(\""+colNames.get(i)+"\")] == -Inf] <- 0.01");
        }
        
        re.eval("deets2 <- deets[ ,c("+collumn+")]");
        
        re.eval("varDesc <- describe(deets2)");
        re.eval("varCor  <- cor(deets2, use=\"pairwise.complete.obs\")");
        
        //re.eval("write.csv(varDesc, file=\""+s+"/"+prodName+"/"+prodName+"-describe-transformed.csv\")");
        //re.eval("write.csv(varCor, file=\""+s+"/"+prodName+"/"+prodName+"-correlations-transformed.csv\")");
        //re.eval("write.csv(deets2, file=\""+s+"/"+prodName+"/"+prodName+"-model-parameters-transformed.csv\")");
        
        File theDir8 = new File(s+"/results/non-transformed/factor analysis output");
        if (!theDir8.exists()) theDir8.mkdir();
        File theDir10 = new File(s+"/results/non-transformed/eigen values");
        if (!theDir10.exists()) theDir10.mkdir();
        File theDir6 = new File(s+"/results/non-transformed/eigen vectors");
        if (!theDir6.exists()) theDir6.mkdir();
        File theDir4 = new File(s+"/results/non-transformed/correlations");
        if (!theDir4.exists()) theDir4.mkdir();
        File theDir2 = new File(s+"/results/non-transformed/descriptives");
        if (!theDir2.exists()) theDir2.mkdirs();
        
        if(boo == true)
        {
            File theDir = new File(s+"/results/transformed/descriptives");
            if (!theDir.exists()) theDir.mkdirs();
            File theDir3 = new File(s+"/results/transformed/correlations");
            if (!theDir3.exists()) theDir3.mkdir();
            File theDir5 = new File(s+"/results/transformed/eigen vectors");
            if (!theDir5.exists()) theDir5.mkdir();
            File theDir7 = new File(s+"/results/transformed/factor analysis output");
            if (!theDir7.exists()) theDir7.mkdir();
            File theDir9 = new File(s+"/results/transformed/eigen values");
            if (!theDir9.exists()) theDir9.mkdir();
            
            
            re.eval("deets2.ev  <- eigen(varCor)");
            re.eval("deets2.faresults  <- factanal(deets2, "+parameters.get(0)+", scores=c(\"Bartlett\"), rotation=\""+parameters.get(1)+"\")");
            
            re.eval("write.csv(deets2.ev$values, file=\""+s+"/results/transformed/eigen values/"+prodName+"-eigenvalues-transformed.csv\")");
            re.eval("write.csv(deets2.ev$vectors, file=\""+s+"/results/transformed/eigen vectors/"+prodName+"-eigenvectors-transformed.csv\")");
            re.eval("write.csv(varDesc, file=\""+s+"/results/transformed/descriptives/"+prodName+"-describe-transformed.csv\")");
            re.eval("write.csv(varCor, file=\""+s+"/results/transformed/correlations/"+prodName+"-correlations-transformed.csv\")");
            //re.eval("write.csv(capture.output(print(deets2.faresults, digits=3, cutoff=0, sort=FALSE)), file=\""+s+"/"+prodName+"/transformed/"+prodName+"-faresults-transformed.csv\")");
            re.eval("capture.output(print(deets2.faresults, digits=3, cutoff=0, sort=FALSE), file=\""+s+"/results/transformed/factor analysis output/"+prodName+"-faresults.txt\")");
            
            //File theDir3 = new File(s+"/"+prodName+"/transformed/"+prodName+"-faresults-transformed.csv");
            //if(!theDir3.exists())
                //re.eval("capture.output(factanal(deets2, "+parameters.get(0)+", rotation=\""+parameters.get(1)+"\"), file=\""+s+"/"+prodName+"/transformed/"+prodName+"-factanal-error.txt\")");
            
            re.eval("deets3 <- deets[ ,c("+indVars+")]");
            re.eval("varDesc2 <- describe(deets3)");
            re.eval("varCor2  <- cor(deets3, use=\"pairwise.complete.obs\")");
            
            re.eval("deets3.ev  <- eigen(varCor2)");
            re.eval("deets3.faresults  <- factanal(deets3, "+parameters.get(0)+",scores=c(\"Bartlett\"), rotation=\""+parameters.get(1)+"\")");
            
            re.eval("write.csv(varDesc2, file=\""+s+"/results/non-transformed/descriptives/"+prodName+"-describe.csv\")");
            re.eval("write.csv(varCor2, file=\""+s+"/results/non-transformed/correlations/"+prodName+"-correlations.csv\")");
            re.eval("write.csv(deets3.ev$values, file=\""+s+"/results/non-transformed/eigen values/"+prodName+"-eigenvalues.csv\")");
            re.eval("write.csv(deets3.ev$vectors, file=\""+s+"/results/non-transformed/eigen vectors/"+prodName+"-eigenvectors.csv\")");
            //re.eval("write.csv(capture.output(print(deets3.faresults, digits=3, cutoff=0, sort=FALSE)), file=\""+s+"/"+prodName+"/non-transformed/"+prodName+"-faresults.csv\")");
            
            re.eval("capture.output(print(deets3.faresults, digits=3, cutoff=0, sort=FALSE), file=\""+s+"/results/non-transformed/factor analysis output/"+prodName+"-faresults.txt\")");
        }else
        {    
            re.eval("deets2.ev  <- eigen(varCor)");
            re.eval("deets2.faresults  <- factanal(deets2, "+parameters.get(0)+", scores=c(\"Bartlett\"), rotation=\""+parameters.get(1)+"\")");
            
            re.eval("write.csv(deets2.ev$values, file=\""+s+"/results/non-transformed/eigen values/"+prodName+"-eigen values.csv\")");
            re.eval("write.csv(deets2.ev$vectors, file=\""+s+"/results/non-transformed/eigen vectors/"+prodName+"-eigen vectors.csv\")");
            re.eval("write.csv(varDesc, file=\""+s+"/results/non-transformed/descriptives/"+prodName+"-describe.csv\")");
            re.eval("write.csv(varCor, file=\""+s+"/results/non-transformed/correlations/"+prodName+"-correlations.csv\")");
            //re.eval("write.csv(capture.output(print(deets2.faresults, digits=3, cutoff=0, sort=FALSE)), file=\""+s+"/"+prodName+"/non-transformed/"+prodName+"-faresults.csv\")");
            re.eval("capture.output(print(deets2.faresults, digits=3, cutoff=0, sort=FALSE), file=\""+s+"/results/non-transformed/factor analysis output/"+prodName+"-faresults.txt\")");
        }
        
        
    }
    /*
     * Input: Two arraylists, the list of independent vars, and the transformation
     * Output: arraylist of the variables after it is transformed
     * Function: transforms the variables if specified (square root, inverse, square, natural log, etc).
     */
    public void transformVariables(ArrayList<String> indVars, ArrayList<String> transform)
    {
        int varSize = indVars.size();
        
        
        for(int i = 0; i < varSize; i++)
        {
            
            if(transform.get(i).equalsIgnoreCase("ln"))
            {
                transformedVars.add("log("+indVars.get(i).replace("-", ".")+")");
                transformedRVars.add("log(deets$"+indVars.get(i).replace("-", ".")+")");
                colNames.add("LN("+indVars.get(i).replace("-", ".")+")");
            } else if(transform.get(i).equalsIgnoreCase("invminus"))
            {
                transformedVars.add("-(1/"+indVars.get(i).replace("-", ".")+")");
                transformedRVars.add("-(1/deets$"+indVars.get(i).replace("-", ".")+")");
                colNames.add("INVMINUS("+indVars.get(i).replace("-", ".")+")");
            } else if(transform.get(i).equalsIgnoreCase("frthroot"))
            {
                transformedVars.add(indVars.get(i).replace("-", ".")+"^0.25");
                transformedRVars.add("deets$"+indVars.get(i).replace("-", ".")+"^0.25");
                colNames.add("FRTHROOT("+indVars.get(i).replace("-", ".")+")");
            } else if(transform.get(i).equalsIgnoreCase("sqroot"))
            {
                transformedVars.add(indVars.get(i).replace("-", ".")+"^0.5");
                transformedRVars.add("deets$"+indVars.get(i).replace("-", ".")+"^0.5");
                colNames.add("SQROOT("+indVars.get(i).replace("-", ".")+")");
            } else if(transform.get(i).equalsIgnoreCase("square"))
            {
                transformedVars.add(indVars.get(i).replace("-", ".")+"^2");
                transformedRVars.add("deets$"+indVars.get(i).replace("-", ".")+"^2");
                colNames.add("SQUARE("+indVars.get(i).replace("-", ".")+")");
            } else if(transform.get(i).equalsIgnoreCase("cube"))
            {
                transformedVars.add(indVars.get(i).replace("-", ".")+"^3");
                transformedRVars.add("deets$"+indVars.get(i).replace("-", ".")+"^3");
                colNames.add("CUBE("+indVars.get(i).replace("-", ".")+")");
            } else if(transform.get(i).equalsIgnoreCase("none"))
            {
                transformedVars.add(indVars.get(i).replace("-", "."));
                transformedRVars.add("deets$"+indVars.get(i).replace("-", "."));
                colNames.add(indVars.get(i).replace("-", "."));
            }
            //System.out.println("" + v.get(i));
        }
        
    }
    
    /* Method Name: startRengine
     * INPUT: NONE
     * OUTPUT: NONE
     * Function: Create an REngine object so we can send R commands
     */
    
    @SuppressWarnings("static-access")
    public void startRengine()
    {
        /*if (!Rengine.versionCheck()) {
        System.err.println("** Version mismatch - Java files don't match library version.");
        System.exit(1);
        }*/
        String[] args = null;
        tc = new TextConsole();
        
        re = new Rengine(args, false, null);
        //re.DEBUG = 100;
        if (!re.waitForR()) 
        {
            System.out.println("Cannot load R");
            return;
           }
    }
    
    public void closeRengine()
    {
        re.end();
    }
}

