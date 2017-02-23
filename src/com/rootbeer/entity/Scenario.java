package com.rootbeer.entity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Scenario {

  public static List<int[]> Slices = new ArrayList<int[]>();
 // public static String SliceStr = "";
  
  private Scenario() {
  }

  public void calculate() throws Exception {

  }

  public static Scenario parseFromFile(String filename) throws IOException {
    Scenario scenario = new Scenario();


    List<String> lines = Files.readAllLines(Paths.get(filename));

    Iterator<String> linesIt = lines.iterator();

    //String foo = linesIt.next();
    //...
    
	String[] elements = linesIt.next().split(" ");

	int R = Integer.parseInt(elements[0]);
	int C = Integer.parseInt(elements[1]);
	int L = Integer.parseInt(elements[2]); //min ingr. / slice
	int H = Integer.parseInt(elements[3]); //max # cells / slice

	int[][] Pizza = new int[R][C];

	String line;
	
	for (int i = 0; i < R; i++) {
		//elements = linesIt.next().split(" ");
		line = linesIt.next();
		for (int j = 0; j < C; j++) {
			if (line.substring(j,j+1).equals("T")) {
				Pizza[i][j] = 1;
			} else {
				Pizza[i][j] = 0;
			}
			//Pizza[i][j] = Integer.parseInt(elements[j]);
		}		
	}
	
	for (int row = 0; row < R; row++) {		
		int currpos = 0;
		int[] num = new int[2]; 
		for (int i = 0; i < C; i++) {
			num[Pizza[row][i]]++;
			if (Math.min(num[0],num[1])>=L) {
				if (i-currpos<H) {
//					Slices.add(new int[4]{row,currpos,row,i});
					Slices.add(new int[]{row,currpos,row,i});
//					SliceStr = SliceStr + 
				}
				currpos = i+1;
				num = new int[]{0,0};
			}
		}
	}

    return scenario;
  }

  public void writeToFile(String fileName) throws IOException {
    PrintStream ps = new PrintStream(new FileOutputStream(fileName));

    int S = Slices.size();
    String output = String.valueOf(S);
    
    int[] slc = null;
    
    for (int i=0; i<S; i++) {
    	slc = Slices.get(i);
    	String line = "";// = String.valueOf(slc[0]) 
        		
		for (int j=0; j<4; j++) {
    		line = line + String.valueOf(slc[j]) + " ";   		
    	}
    	output = output + "\r\n" + line;
    }
    
    // GET COMMAND COUNT
    ps.println(output);

    ps.flush();
    ps.close();
  }
}
