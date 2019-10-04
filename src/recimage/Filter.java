/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recimage;

import static recimage.RecImage.rec;
import java.awt.Dimension;
import java.io.File;

/**
 *
 * @author jhord_000
 */
public class Filter{

    private int WIDTH,HEIGHT,PP=1;
    private int pos_fit=0;
    private double pixel_fit=0; 
    private Integer [][] points;
    public Filter(double [][]img,int pp,int t)
    {fit(img,pp,t);}
    public Filter(int pp)
    {make(0,0,pp);}
    public Filter(int w,int h,int pp)
    {make(w,h,pp);}
    private void make(int w,int h,int pp)
    {        
        WIDTH=w;HEIGHT=h;PP=pp;
        points=new Integer [pp][2];
        for(int y=0;y<points.length;y++)
        {points[y][0]=WIDTH/pp*y;points[y][1]=HEIGHT/pp*y;} 
    }
    public void fit(double [][]img)
    {fit(img,PP,0);}
    public void fit(double [][]img,int pp,int t)
    {
        make(img.length,img[0].length,pp);
         for (int i = 0; i < HEIGHT; i++) 
          for (int j = 0; j < WIDTH; j++)
              if(pos_fit<pp)fit(j,i,img[j][i],t);
              else return;
    }
    private int bw(double p)
    {        
        String color = Integer.toHexString((int) p & 0xffffff);
        Integer s = color.length();
        Integer r = s < 2 ? 0 : RecImage.parse(color.substring(0, 2));
        Integer g = s < 4 ? 0 : RecImage.parse(color.substring(2, 4));
        Integer b = s < 6 ? 0 : RecImage.parse(color.substring(4));
        Integer rgb= (int) ((r * 0.25) + (g * 0.36) + (b * 0.39));
        Integer bw = (255 / 1.5) < rgb ? 255 : 0;
        return bw < 256 ? bw : 255;
    }
    private void fit(int j,int i,double p, int t)
    {
        if(pixel_fit<=p+t&&pixel_fit>=p-t)
            return;
        
        pixel_fit=bw(p);
        Integer pos[]=points[pos_fit++];
        pos[0]=j;pos[1]=i;
     }
    public Integer[][] getPoints(){return points;}
    public void print()
    {
        for (int i = 0; i < pos_fit; i++) 
            System.out.println(points[i][0]+" "+points[i][1]);        
    }
    public static void main(String []args)
    {
        RecImage.setParams(args);
        rec.loadImage(new File(args[0]));
        Filter f=new Filter(rec.getCache(),2000000,90000);
        Dimension d = rec.getSize();
        rec.setSize(0, 0);
        rec.setSize(d);
        rec.filter(f.getPoints(), 10E0);       
    }
}
