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
        System.out.println(b1);
        System.out.println(b11);

        Formula phi2 = f.and(x, x.negative());
        System.out.printf("Formula: %s\n", phi2);
        Backbone b2 = new Backbone(f, phi2, 1);
        Backbone b22 = new Backbone(f, phi2, 2);
        System.out.println(b2);
        System.out.println(b22);

        Formula phi3 = f.or(x, y);
        System.out.printf("Formula: %s\n", phi3);
        Backbone b3 = new Backbone(f, phi3, 1);
        Backbone b33 = new Backbone(f, phi3, 2);
        System.out.println(b3);
        System.out.println(b33);

        Formula phi4 = f.and(f.or(x.negative(), y.negative()), y);
        System.out.printf("Formula: %s\n", phi4);
        Backbone b4 = new Backbone(f, phi4, 1);
        Backbone b44 = new Backbone(f, phi4, 2);
        System.out.println(b4);
        System.out.println(b44);

        Formula phi5 = f.or(f.and(f.or(x, y.negative()), y), x);
        System.out.printf("Formula: %s\n", phi5);
        Backbone b5 = new Backbone(f, phi5, 1);
        Backbone b55 = new Backbone(f, phi5, 2);
        System.out.println(b5);
        System.out.println(b55);

    }
}
