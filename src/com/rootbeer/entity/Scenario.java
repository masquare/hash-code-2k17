package com.rootbeer.entity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Scenario {


  private Scenario() {
  }

  public void calculate() throws Exception {

  }

  public static Scenario parseFromFile(String filename) throws IOException {
    Scenario scenario = new Scenario();

    List<String> lines = Files.readAllLines(Paths.get(filename));

    Iterator<String> linesIt = lines.iterator();

    String foo = linesIt.next();
    //...

    return scenario;
  }

  public void writeToFile(String fileName) throws IOException {
    PrintStream ps = new PrintStream(new FileOutputStream(fileName));

    // GET COMMAND COUNT
    ps.println("Hello World");

    ps.flush();
    ps.close();
  }
}
