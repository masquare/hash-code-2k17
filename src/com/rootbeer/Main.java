package com.rootbeer;

import com.rootbeer.entity.Scenario;

public class Main {

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: executable.jar <inputfile> <outputfile>");
      return;
    }

    Scenario scenario = new Scenario(args[0]);
    scenario.calculate();
    scenario.writeToFile(args[1]);

    System.out.println("Done");
  }
}
