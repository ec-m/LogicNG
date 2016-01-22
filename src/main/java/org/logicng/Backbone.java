package org.logicng;

import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.*;

/**
 * Backbone.
 * <p>
 * The backbone of a formula is the set of literals
 * that is true in all models of the formula.
 * @author
 * @version
 * @since
 */
public final class Backbone {

    private final FormulaFactory f;
    private final Formula phi;
    private SortedSet<Literal> backbone;

    /**
     * Constructor.
     * @param f        the factory which created this instance
     * @param phi      formula to compute backbone of
     * @param algnum   number of algorithm to use for backbone computation (1 - 7)
     */
    Backbone(final FormulaFactory f, final Formula phi, int algnum) {
        this.f = f;
        this.phi = phi;
        switch (algnum) {
            case 1:
                this.backbone = algorithm1();
                break;
            default:
                System.out.println("No valid number for algorithm to use. Allowed: 1 - 7");
        }
    }

    /**
     * Computes backbone with enumeration-based algorithm.
     * @return the backbone of the formula phi.
     */
    public SortedSet<Literal> algorithm1() {

        /* Backbone estimate */
        SortedSet<Literal> bb = this.phi.literals();
        for ( Literal l : this.phi.literals() ) {
            bb.add(l.negate());
        }

        SATSolver solver = MiniSat.miniSat(this.f);
        solver.add(this.phi);
        while (!bb.isEmpty()) {
            if (solver.sat() == Tristate.FALSE) {
                return bb;
            }
            /* Refine backbone using model returned by sat */
            Assignment model = solver.model();
            bb.retainAll(model.literals());

            /* Get blocking clause */
            Formula bc = model.blockingClause(this.f, null);

            /* Add blocking clause to phi */
            solver.add(bc);
        }

        return bb;
    }

    @Override
    public String toString() {
        return this.backbone.toString();
    }
}
