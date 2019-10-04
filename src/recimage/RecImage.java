/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recimage;

import gui.ManageRecs;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static recimage.RecImage.ImageColor.CONTOUR;

/**
 *
 * @author jhord_000
 */
public class RecImage extends JPanel{

    public static JFrame parent = null;
    public static final RecImage rec = new RecImage();
    
    public static String NAMEFILE = "rec";
    public static FileWriter FILE = null;
    public static boolean toBot = true;
    public static int X=50,Y=180;

    protected static ImageColor imageColor = ImageColor.DOS_COLORES;
    protected static boolean ableConsole=false;
    protected static boolean ablePaint = true;    
    protected static boolean ableRec = false;
    protected static int D=10;
    protected static boolean Rev=true;
    protected static boolean save;
    
    protected final Canvas lienzo;
    protected int MAX_VL_PIXEL = 0, MIN_VL_PIXEL = 255;
    private double[][] cache_img;
    private double B;
    private double G;
    private double R;
    private int width,height;
    private Graphics graphics;
    private boolean loaded;
    private Integer _bw=0;

    public static enum ImageColor {
        DOS_COLORES, ESCALA_GRISES, FULL_COLOR, CONTOUR
    };

    public static void setParams(String []args){

        if (args.length > 1){
            D = args[1].contains("-t-")?Integer.parseInt(args[2]):10;
            save = args[1].contains("-s-"); //save on file
            Rev = !args[1].contains("-rev-");//revertir colores
            ableRec = args[1].contains("-f-");//grabar archivo *.do
            ablePaint = args[1].contains("-p-");//mostrar en pantalla
            toBot = args[1].contains("-b-");
            X = args[1].contains("-X:")?Integer.parseInt(args[1].substring(args[1].lastIndexOf("-X:")+3,args[1].lastIndexOf(":X-"))):X;
            Y = args[1].contains("-Y:")?Integer.parseInt(args[1].substring(args[1].lastIndexOf("-Y:")+3,args[1].lastIndexOf(":Y-"))):Y;
            ableConsole = args[1].contains("-cc-");
            imageColor = args[1].contains("-es-")?//escala_grises
                     ImageColor.ESCALA_GRISES : args[1].contains("-co-")? //contorno
                        ImageColor.CONTOUR : args[1].contains("-fc-")? //full_color
                             ImageColor.FULL_COLOR : ImageColor.DOS_COLORES; //balnco y negro
            if(!args[1].contains("-g-"))            
                NAMEFILE = args[0].substring(args[0].lastIndexOf("\\") + 1, args[0].lastIndexOf("."));  
            
            System.out.println("X: "+X+" Y: "+Y);
            System.out.println(NAMEFILE);
       }    
    }    
    private Color paint(Integer i, Integer j,double c){

        String color = Integer.toHexString((int) c & 0xffffff);
        Integer s = color.length();
        Integer r = s < 2 ? 0 : parse(color.substring(0, 2));
        Integer g = s < 4 ? 0 : parse(color.substring(2, 4));
        Integer b = s < 6 ? 0 : parse(color.substring(4));
        Integer rgb = (int) ((r * 0.25) + (g * 0.36) + (b * 0.39));//(int)(r+g+b)/3;
        switch (imageColor){            
            case CONTOUR:
            case DOS_COLORES:
                if(Rev){
                    MAX_VL_PIXEL = rgb > MAX_VL_PIXEL ? rgb : MAX_VL_PIXEL;
                    MIN_VL_PIXEL = rgb < MIN_VL_PIXEL ? rgb : MIN_VL_PIXEL;
                }                
                Integer bw = ((MAX_VL_PIXEL - MIN_VL_PIXEL) / 1.5) < rgb ? MAX_VL_PIXEL : 0;
                bw = bw < 256 ? bw : 255;//Dos colors
                
                if(imageColor==CONTOUR)  {
                  if(_bw!=bw){
                      _bw=bw;
                      return null;//break;
                    }      
                }          
               _bw=bw; 
                if (ableRec && bw == 0) {
                    write(rec(i, j, bw, bw, bw));
                }
                               
                r=g=b=bw;
                break;

            case ESCALA_GRISES:                
                if (ableRec){
                    write(rec(i, j, rgb, rgb, rgb));
                }

                r=g=b=rgb;
                break;

            case FULL_COLOR:
                if (ableRec){
                    write(rec(i, j, r, g, b));
                }
                break;
            default:
                break;
        }
        Color out = new Color(r, g, b);
       
        return out;
    }

    public static int parse(String in){
        int out = 0;
        for (char in_tmp : in.toCharArray()) 
        {
            switch (in_tmp) 
            {
                case '0':
                    out = out == 0 ? 0 : out * 16;
                    break;
                case '1':
                    out = out == 0 ? 1 : out * 16 + 1;
                    break;
                case '2':
                    out = out == 0 ? 2 : out * 16 + 2;
                    break;
                case '3':
                    out = out == 0 ? 3 : out * 16 + 3;
                    break;
                case '4':
                    out = out == 0 ? 4 : out * 16 + 4;
                    break;
                case '5':
                    out = out == 0 ? 5 : out * 16 + 5;
                    break;
                case '6':
                    out = out == 0 ? 6 : out * 16 + 6;
                    break;
                case '7':
                    out = out == 0 ? 7 : out * 16 + 7;
                    break;
                case '8':
                    out = out == 0 ? 8 : out * 16 + 8;
                    break;
                case '9':
                    out = out == 0 ? 9 : out * 16 + 9;
                    break;

                case 'a':
                    out = out == 0 ? 10 : out * 16 + 10;
                    break;
                case 'b':
                    out = out == 0 ? 11 : out * 16 + 11;
                    break;
                case 'c':
                    out = out == 0 ? 12 : out * 16 + 12;
                    break;
                case 'd':
                    out = out == 0 ? 13 : out * 16 + 13;
                    break;
                case 'e':
                    out = out == 0 ? 14 : out * 16 + 14;
                    break;
                case 'f':
                    out = out == 0 ? 15 : out * 16 + 15;
                    break;
            }
        }
        return out;
    }

    public double [][] getCache(){
        return cache_img;
    }
    public void setCache(double [][]img){
        cache_img=img;
    
        width=img.length;
        height=img[0].length;
        if(parent!=null)
            parent.setSize(width+10, height+150);
    }
    public double[][] loadImage(File file1){
         return loadImage(file1,true); 
    }
    public double[][] loadImage(File file1,boolean paint){        

        BufferedImage img = null;
        try {img = ImageIO.read(file1);} 
        catch (IOException ex){ 
            System.out.println(ex.getMessage());            
            Logger.getLogger(RecImage.class.getName()).log(Level.SEVERE, null, ex);
        }          
       
        width=img.getWidth();
        height=img.getHeight();
        
        cache_img = new double[width][height];
        if(parent!=null)
            parent.setSize(width+10, height+150);
        
        graphics = lienzo.getGraphics();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++){
                cache_img[j][i] = img.getRGB(j, i);
                if(paint&&graphics!=null){
                    graphics.setColor(paint(i,j,cache_img[j][i]));
                    graphics.fillOval(i, j, 1, 1);
                }             
            }
        }
        loaded=true;
        if(save){
            save();
        }
        update();
        //add(lienzo,BorderLayout.CENTER);
        //add(new JLabel("HOOOLA?"),BorderLayout.CENTER);

        return cache_img;
    }

    public void update(){
        lienzo.update(graphics);        
    }

    public String get_color(double r, double g, double b){
        return "[PAINT COLOR]," + r + ",[TAB/]," + g + ",[TAB/]," + b + ",[TAB/],[TAB/],[ENTER/]";
    }

    public String rec(int x, int y, double r, double g, double b){
        String out = "";
        if (r != R || g != G || b != B){
            out += get_color(r, g, b) + ",";
            R = r;
            G = g;
            B = b;
        }
        
        out += "[WAIT "+D+"],[MOUSE M " + (X + x) + " " + (Y + y) + "]"+(toBot?"":",[MOUSE L]");
        return out;
    }
    public void filter(Integer [][]f,double c){
        for (Integer[] f1 : f) {
            //paint(f1[0], f1[1], c);
            if(ablePaint){
                graphics.setColor(paint(f1[0],f1[1],c));
                graphics.fillOval(f1[0], f1[1], 1, 1);
            }
        }

    }
    public void repaint_(){
        if(loaded){
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    //paint(j, i, cache_img[j][i]);  
                    if(ablePaint){
                        graphics.setColor(paint(j,i,cache_img[j][i]));
                        graphics.fillOval(j, i, 1, 1);
                    }
                }
            }
        }
        
        try
        {
            if(ableRec)
             FILE.write("\n::SET\n[MAKE "+NAMEFILE+".set],[SYSTEM RUN],t-"+D+" x-"+X+" y-"+Y+",[/MAKE]\n::SET");
            if(FILE!=null)
                FILE.close();
        }catch(IOException e){}
    }
    protected void repaint_(Graphics g){
        graphics=g;
        repaint_();
    }
    
    private RecImage(){
        lienzo = new Canvas(){
            public final void repaint(Graphics g){
                repaint_(g);
            }
        };
        add(new JScrollPane(lienzo),BorderLayout.CENTER);
    } 

    public String save(){

        try{
            BufferedImage imagen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = imagen.getGraphics();
            System.out.println("width:"+width+" height:"+height);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++){      
                    g.setColor(paint(0,0,cache_img[j][i]));                   
                    g.fillOval(j, i, 10, 10);                                 
                }
            }
            String file = NAMEFILE+"-"+imageColor.name()+".png";
            File outputfile = new File(file);
            ImageIO.write(imagen, "png", outputfile);
            return outputfile.getAbsolutePath();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    public static void write(String d){
         write(d, true);
    }

    public static void write(String d, boolean t){
        if(ableConsole) 
            System.out.println(d);
        
        if(ableRec){
            try{
                if(!t||FILE==null)
                    FILE = new FileWriter(NAMEFILE+".do", t);
               FILE.write(d+'\n');
               FILE.flush();
            } catch(IOException e) {} 
        }
    }
    public static void main(String[] args){
        // TODO code application logic here
        if (args.length <= 0) {
            new ManageRecs();
            return;
        }
        RecImage.setParams(args);    
        
        if(!toBot)
            write("[PAINT OPEN],[WAIT 1000],[WINDOW UP],[WINDOW UP],[PAINT PEN]", false);
        else  write("[SYSTEM RUN],[WAIT 500],[CONTROL],v,[/CONTROL],[ENTER/],[WAIT 1000],[WINDOW UP],[WINDOW UP]", false);
        write("::START");
        rec.loadImage(new File(args[0])); 
        write("::START");
        try{
            if(FILE!=null){
                FILE.close();
            }
        }catch(IOException e){}
    }

}
