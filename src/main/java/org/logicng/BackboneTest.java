package org.logicng;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.io.File;

/**
 * For testing purposes of class Backbone only.
 */
public class BackboneTest {

    public static void main(String[] args) {

        FormulaFactory f = new FormulaFactory();

        String path = System.getProperty("user.dir") + "/tests/backbone/";
        File[] files = new File(path).listFiles();

        for (File file : files) {
            System.out.println("File: " + file.getName());
            Formula phi = null;
            try{
                phi = Backbone.readCNF(file, f);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Formula: " + phi);
//            Backbone b1 = new Backbone(f, phi, 1); // Test algorithm 1 -- exit code 137.
//            Backbone b2 = new Backbone(f, phi, 2); // Test algorithm 2 -- exit code 137.
            Backbone b3 = new Backbone(f, phi, 3); // Test algorithm 3
//            Backbone b4 = new Backbone(f, phi, 4); // Test algorithm 4 -- exit code 137.
            Backbone b5 = new Backbone(f, phi, 5); // Test algorithm 5
//            System.out.println(b1);
//            System.out.println(b2);
            System.out.println(b3);
//            System.out.println(b4);
            System.out.println(b5);

        }
    }
}
