package org.logicng.functions;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;


/**
 * Unit tests for {@link BackboneFunction}.
 * @author Eva Charlotte Mayer
 * @version
 * @since
 */
public class BackboneTest {

    @Test
    public void test() {

        FormulaFactory f = new FormulaFactory();

        String path = System.getProperty("user.dir") + "/tests/backbone/";
        File[] files = new File(path).listFiles();

        for (File file : files) {

            Formula phi = null;
            try {
                phi = readCNF(file, f);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /* Test algorithm 2 */
            BackboneFunction bf2 = new BackboneFunction(f, phi, BackboneFunction.Algorithm.ITERATIVE_TWO_TESTS);
            System.out.println(phi.apply(bf2));
            /* Test algorithm 3 */
            BackboneFunction bf3 = new BackboneFunction(f, phi, BackboneFunction.Algorithm.ITERATIVE_ONE_TEST);
            System.out.println(phi.apply(bf3));
            /* Test algorithm 4 */
            BackboneFunction bf4 = new BackboneFunction(f, phi, BackboneFunction.Algorithm.ITERATIVE_COMPLEMENT);
            System.out.println(phi.apply(bf4));
            /* Test algorithm 5 */
            BackboneFunction bf5 = new BackboneFunction(f, phi, BackboneFunction.Algorithm.CHUNKING, 20);
            System.out.println(phi.apply(bf5));
        }
    }

    /**
     * Helper function.
     * Reads formula from a DIMACS CNF file.
     * @param file  File to be read from.
     * @param ff    FormulaFactory for returned formula.
     * @return      Formula object according to formula from file.
     */
    public static Formula readCNF(final File file, final FormulaFactory ff) throws IOException {
        LinkedList<Formula> clauses = new LinkedList<>();
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            final String line = reader.readLine();
            if (line.startsWith("p cnf"))
                break;
        }
        String[] tokens;
        final List<Literal> literals = new ArrayList<>();
        while (reader.ready()) {
            tokens = reader.readLine().split("\\s+");
            if (tokens.length >= 2) {
                assert "0".equals(tokens[tokens.length - 1]);
                literals.clear();
                for (int i = 0; i < tokens.length - 1; i++) {
                    if (!tokens[i].isEmpty()) {
                        int parsedLit = Integer.parseInt(tokens[i]);
                        String var = "v" + Math.abs(parsedLit);
                        literals.add(parsedLit > 0 ? ff.literal(var, true) : ff.literal(var, false));
                    }
                }
                if (!literals.isEmpty())
                    clauses.add(ff.or(literals));
            }
        }
        return ff.and(clauses);
    }
}
