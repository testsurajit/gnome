package ese.gnomemsr2009;
import java.text.DecimalFormat;
import java.io.*;
import java.util.*;

class function_use
    {
        public void call_main()
            {
                double ar[][]={{36,0,36,0},
                            {0,0,0,0},
                            {481,0,481,0},
                            {47,0,47,0}, 
                            {0,0,0,0},
                            {0,0,0,0}};
        /*        int ar[][]={{43,99,0,15},
                            {21,65,1,15},
                            {25,79,3,20},
                            {42,75,4,25}, 
                            {57,87,4,30},
                            {59,81,6,35}};*/
                double res[][]=new double[6][2];
                
                for(int i=0;i<4;i+=2)
                    {
                       for(int j=0;j<6;j++)
                        {
                            res[j][0]=ar[j][i];
                            res[j][1]=ar[j][i+1];
                        }
                       System.out.println(c_calc(res,6));
                    }
            }
        public double c_calc(double arr[][],int totrow)
            {
                //int totrow=6;
                /*double arr[][]={
                                {43,99},{21,65},{25,79},{42,75},{57,87},{59,81}
                                };*/
                double res[][]=new double[totrow+1][3];
                int i,r=0;
                double co_res=0;
                double totx=0,toty=0;
                double sqx=0,sqy=0,sqxsqy=0,tsqx=0,tsqy=0,tsqxsqy=0;
                for(i=0;i<totrow;i++)
                    {
                        sqx=Math.pow(arr[i][0],2);
                        sqy=Math.pow(arr[i][1],2);
                        sqxsqy=sqx*sqy;
                        tsqx+=sqx;
                        tsqy+=sqy;
                        tsqxsqy+=(arr[i][0]*arr[i][1]);
                        res[r][0]=sqx;
                        res[r][1]=sqy;
                        res[r][2]=sqxsqy;
                        r++;
                        totx+=arr[i][0];
                        toty+=arr[i][1];
                    }
                res[r][0]=tsqx;
                res[r][1]=tsqy;
                res[r][2]=tsqxsqy;
                co_res=((totrow*(tsqxsqy))-(totx*toty))/Math.sqrt(((totrow*res[r][0])-Math.pow(totx,2))*((totrow*res[r][1])-Math.pow(toty,2)));
                //System.out.println(totx);
                //System.out.println(toty);
                //System.out.println(res[r][0]);
                //System.out.println(res[r][1]);
                //System.out.println(res[r][2]);
                //System.out.println(co_res);
                //System.out.println(co_res);
//                if(co_res="NaN"
                boolean b1 = Double.isNaN(co_res);
                if (b1)
                    co_res=0.0;
                return co_res;
            }
    }