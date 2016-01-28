package org.logicng.functions;

import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.FormulaFunction;
import org.logicng.formulas.Literal;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;
import org.logicng.solvers.SolverState;

import java.util.*;

/**
 * Computes the backbone of a given formula.
 * <p>
 * The backbone of a formula is the set of literals
 * that is true in all models of the formula.
 * @author Eva Charlotte Mayer
 * @version
 * @since
 * References to where algorithms originate?
 */
public class BackboneFunction implements FormulaFunction<Collection<Literal>> {

    public enum Algorithm {
        ENUMERATION,
        ITERATIVE_TWO_TESTS,
        ITERATIVE_ONE_TEST,
        ITERATIVE_COMPLEMENT,
        CHUNKING
    }

    private final FormulaFactory f;
    private final Formula phi;
    Algorithm algorithm;
    int chunkSize;

    /**
     * Constructor.
     * @param f           the factory which created this instance
     * @param phi         formula to compute backbone of
     * @param algorithm   number of algorithm to use for backbone computation
     *                    Here only algorithms 1 - 4 allowed, since chunk size missing
     */

    /* Is phi even needed here as parameter? Or maybe only when calling "apply" */
    BackboneFunction(final FormulaFactory f, final Formula phi, Algorithm algorithm) {
        this.f = f;
        this.phi = phi;
        if (algorithm == Algorithm.CHUNKING)
            throw new IllegalStateException("Chunk size missing.");
        this.algorithm = algorithm;
        this.chunkSize = -1;
    }

    /**
     * Constructor for backbone computation with chunking algorithm.
     * @param f           the factory which created this instance
     * @param phi         formula to compute backbone of
     * @param algorithm   number of algorithm to use for backbone computation
     * @param chunkSize   size of chunks used for algorithm 5 as integer
     * <p>
     * Remark: If this constructor is called with a different algorithm than
     *         the Chunking one, the chunkSize parameter is simply ignored.
     */
    BackboneFunction(final FormulaFactory f, final Formula phi, Algorithm algorithm, int chunkSize) {
        this.f = f;
        this.phi = phi;
        this.algorithm = algorithm;
        this.chunkSize = chunkSize;
    }

    @Override
    public Collection<Literal> apply(final Formula formula, boolean cache) {
        switch (this.algorithm) {
            case ENUMERATION:
                return algorithm1();
            case ITERATIVE_TWO_TESTS:
                return algorithm2();
            case ITERATIVE_ONE_TEST:
                return algorithm3();
            case ITERATIVE_COMPLEMENT:
                return algorithm4();
            case CHUNKING:
                return algorithm5(chunkSize);
            default:
                throw new IllegalStateException("No valid number for algorithm to use. Allowed: 1 - 5");
        }
    }


    /**
     * Computes backbone with enumeration-based algorithm.
     * @return the backbone of the formula phi.
     */
    public Collection<Literal> algorithm1() {

        /* Backbone estimate */
        Collection<Literal> bb = this.phi.literals();
        for (Literal l : this.phi.literals()) {
            bb.add(l.negate());
        }

        SATSolver solver = MiniSat.miniSat(this.f);
        solver.add(this.phi);
        if(solver.sat() == Tristate.FALSE) {
            return Collections.emptySortedSet();
        }
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
    public Collection<Literal> algorithm2() {
        /* Initialize backbone as empy set */
        Collection<Literal> bb = new TreeSet<>();

        SATSolver solver = MiniSat.miniSat(this.f);
        solver.add(this.phi);

        /* Check each literal whether it is in the backbone */
        for (Literal l : this.phi.variables()) {
            Tristate out1 = solver.sat(l);
            Tristate out0 = solver.sat(l.negative());
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
    public Collection<Literal> algorithm3() {
        /* Initialization */
        SATSolver solver = MiniSat.miniSat(this.f);
        solver.add(this.phi);
        if(solver.sat() == Tristate.FALSE) {
            return Collections.emptySortedSet();
        }
        SortedSet<Literal> lambda = solver.model().literals();
        Collection<Literal> bb = new TreeSet<>();

        while(!lambda.isEmpty()) {
            Literal l = lambda.first();
            Tristate out = solver.sat(l.negate());
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

    /**
     * Computes backbone with iterative algorithm
     * and complement of backbone estimate.
     * @return backbone of formula phi.
     */
    public Collection<Literal> algorithm4() {
        /* Initialization */
        SATSolver solver = MiniSat.miniSat(this.f);
        solver.add(this.phi);
        solver.sat();
        if(solver.sat() == Tristate.FALSE) {
            return Collections.emptySortedSet();
        }
        Collection<Literal> bb = solver.model().literals();

        while(!bb.isEmpty()) {
            SolverState before = solver.saveState();
            List<Formula> complement = new ArrayList<>();
            for(Literal l : bb) {
                complement.add(l.negate());
            }
            solver.add(f.or(complement));
            if(solver.sat() == Tristate.FALSE) {
                return bb;
            }
            /* Refine backbone estimate */
            bb.retainAll(solver.model().literals());
            /* Set back to state before disjunction was added to phi */
            solver.loadState(before);
        }

        return bb;
    }

    /**
     * Computes backbone with chunking algorithm.
     * @param chunksize size of literal-set to be tested in each iteration.
     * @return backbone of formula phi.
     */
    public Collection<Literal> algorithm5(int chunksize) {
        /* Initialization */
        SATSolver solver = MiniSat.miniSat(this.f);
        solver.add(this.phi);
        if(solver.sat() == Tristate.FALSE) {
            return Collections.emptySortedSet();
        }
        SortedSet<Literal> lambda = solver.model().literals();
        Collection<Literal> bb = new TreeSet<>();

        while(!lambda.isEmpty()) {
            /* Pick chunk */
            SolverState before = solver.saveState();
            int k = Math.min(chunksize, lambda.size());
            final Iterator<Literal> it = lambda.iterator();
            Formula gamma = it.next().negate();
            for(int i = 0; i < k - 1; i++) {
                gamma = f.or(gamma, it.next().negate());
            }
            solver.add(gamma);
            if(solver.sat() == Tristate.FALSE) {
                /* All literals in chunk are backbones */
                bb.addAll(gamma.negate().cnf().literals());
                lambda.removeAll(gamma.negate().cnf().literals());
                solver.loadState(before);
                solver.add(gamma.negate().cnf());
            } else {
                /* Refine set of literals to be tested */
                lambda.retainAll(solver.model().literals());
                solver.loadState(before);
            }
        }

        return bb;
    }
}
