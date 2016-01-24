package org.logicng;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.Backbone; // Not needed a.t.m. since both classes are in the same folder

import java.util.*;

/**
 * For testing purposes of class Backbone only.
 */
public class BackboneTest {

    public static void main(String[] args) {

        FormulaFactory f = new FormulaFactory();
        Literal x = f.literal("x", true);
        Literal y = f.literal("y", true);
        Literal z = f.literal("z", true);
        Literal w = f.literal("w", true);

        Formula phi1 = f.and(f.and(f.or(x.negative(), y.negative()), x), f.or(z, w));
        System.out.printf("Formula: %s\n", phi1);
        Backbone b1 = new Backbone(f, phi1, 1); // Test algorithm 1
        Backbone b11 = new Backbone(f, phi1, 2); // Test algorithm 2
        Backbone b111 = new Backbone(f, phi1, 3); // Test algorithm 3
        Backbone b1111 = new Backbone(f, phi1, 3); // Test algorithm 4
        System.out.println(b1);
        System.out.println(b11);
        System.out.println(b111);
        System.out.println(b1111);

        Formula phi2 = f.and(x, x.negative());
        System.out.printf("Formula: %s\n", phi2);
        Backbone b2 = new Backbone(f, phi2, 1);
        Backbone b22 = new Backbone(f, phi2, 2);
        Backbone b222 = new Backbone(f, phi2, 3);
        Backbone b2222 = new Backbone(f, phi2, 4);
        System.out.println(b2);
        System.out.println(b22);
        System.out.println(b222);
        System.out.println(b2222);

        Formula phi3 = f.or(x, y);
        System.out.printf("Formula: %s\n", phi3);
        Backbone b3 = new Backbone(f, phi3, 1);
        Backbone b33 = new Backbone(f, phi3, 2);
        Backbone b333 = new Backbone(f, phi3, 3);
        Backbone b3333 = new Backbone(f, phi3, 4);
        System.out.println(b3);
        System.out.println(b33);
        System.out.println(b333);
        System.out.println(b3333);

        Formula phi4 = f.and(f.or(x.negative(), y.negative()), y);
        System.out.printf("Formula: %s\n", phi4);
        Backbone b4 = new Backbone(f, phi4, 1);
        Backbone b44 = new Backbone(f, phi4, 2);
        Backbone b444 = new Backbone(f, phi4, 3);
        Backbone b4444 = new Backbone(f, phi4, 4);
        System.out.println(b4);
        System.out.println(b44);
        System.out.println(b444);
        System.out.println(b4444);

        Formula phi5 = f.or(f.and(f.or(x, y.negative()), y), x);
        System.out.printf("Formula: %s\n", phi5);
        Backbone b5 = new Backbone(f, phi5, 1);
        Backbone b55 = new Backbone(f, phi5, 2);
        Backbone b555 = new Backbone(f, phi5, 3);
        Backbone b5555 = new Backbone(f, phi5, 4);
        System.out.println(b5);
        System.out.println(b55);
        System.out.println(b555);
        System.out.println(b5555);

    }
}
