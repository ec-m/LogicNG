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
 * References to where algorithms originate?
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
            case 2:
                this.backbone = algorithm2();
                break;
            case 3:
                this.backbone = algorithm3();
                break;
            default:
                throw new IllegalStateException("No valid number for algorithm to use. Allowed: 1 - 7");
        }
    }

    /**
     * Computes backbone with enumeration-based algorithm.
     * @return the backbone of the formula phi.
     */
    public SortedSet<Literal> algorithm1() {

        /* Backbone estimate */
        SortedSet<Literal> bb = this.phi.literals();
        for (Literal l : this.phi.literals()) {
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

    /**
     * Computes backbone with iterative algorithm.
     * @return the backbone of the formula phi.
     */
    public SortedSet<Literal> algorithm2() {
        /* Initialize backbone as empy set */
        SortedSet<Literal> bb = new TreeSet<>();

        SATSolver solver = MiniSat.miniSat(this.f);
        solver.add(this.phi);

        /* Check each literal whether it is in the backbone */
        for (Literal l : this.phi.variables()) {
            Tristate out1 = solver.sat(Arrays.asList(l));
            Tristate out0 = solver.sat(Arrays.asList(l.negative()));
            if (out1 == Tristate.FALSE & out0 == Tristate.FALSE) {
                return Collections.emptySortedSet();
            }
            if (out1 == Tristate.FALSE) {
                bb.add(l.negative());
                solver.add(l.negative());
            }
            if (out0 == Tristate.FALSE) {
                bb.add(l);
                solver.add(l);
            }
        }
        return bb;
    }

    /**
     * Computes backbone with iterative algorithm (one test per variable).
     * @return backbone of formula phi.
     */
    public SortedSet<Literal> algorithm3() {
        /* Initialization */
        SATSolver solver = MiniSat.miniSat(this.f);
        solver.add(this.phi);
        if(solver.sat() == Tristate.FALSE) {
            return Collections.emptySortedSet();
        }
        SortedSet<Literal> lambda = solver.model().literals();
        SortedSet<Literal> bb = new TreeSet<>();

        while(!lambda.isEmpty()) {
            Literal l = lambda.first();
            Tristate out = solver.sat(Arrays.asList(l.negate()));
            if(out == Tristate.FALSE) {
                /* Backbone identified */
                bb.add(l);
                lambda.remove(l);
                solver.add(l);
            } else {
                /* Refine set of literals still to be
                 * tested using the model returned by SAT.
                 */
                lambda.retainAll(solver.model().literals());
            }
        }

        return bb;
    }

    @Override
    public String toString() {
        return this.backbone.toString();
    }
}
