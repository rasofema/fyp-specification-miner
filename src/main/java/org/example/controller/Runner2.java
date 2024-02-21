package org.example.controller;

import de.learnlib.query.DefaultQuery;
import de.learnlib.ralib.automata.*;
import de.learnlib.ralib.automata.guards.AtomicGuardExpression;
import de.learnlib.ralib.automata.guards.Conjunction;
import de.learnlib.ralib.automata.guards.Disjunction;
import de.learnlib.ralib.automata.guards.Relation;
import de.learnlib.ralib.automata.util.RAToDot;
import de.learnlib.ralib.data.*;
import de.learnlib.ralib.data.util.SymbolicDataValueGenerator;
import de.learnlib.ralib.equivalence.IOEquivalenceOracle;
import de.learnlib.ralib.learning.*;
import de.learnlib.ralib.learning.ralambda.RaLambda;
import de.learnlib.ralib.oracles.*;
import de.learnlib.ralib.oracles.mto.MultiTheorySDTLogicOracle;
import de.learnlib.ralib.oracles.mto.MultiTheoryTreeOracle;
import de.learnlib.ralib.solver.ConstraintSolver;
import de.learnlib.ralib.solver.simple.SimpleConstraintSolver;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.tools.theories.IntegerEqualityTheory;
import de.learnlib.ralib.words.InputSymbol;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.example.model.Functions;
import org.example.rahelper.RandomWalk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;


public class Runner2 {
    static DataType dt = new DataType("int", Integer.class);
    static InputSymbol init = new InputSymbol("init", dt);
    static InputSymbol add = new InputSymbol("add", dt);
    static InputSymbol next = new InputSymbol("next", dt);
    static SymbolicDataValue.Constant zero = new SymbolicDataValue.Constant(dt, 0);
    static SymbolicDataValue.Constant one = new SymbolicDataValue.Constant(dt, 1);

    private static RegisterAutomaton createAutomaton() {
        Constants consts = new Constants();
        consts.put(zero, new DataValue<>(dt, 0));
        consts.put(one, new DataValue<>(dt, 1));
        MutableRegisterAutomaton ra = new MutableRegisterAutomaton(consts);

        RALocation l_eps = ra.addInitialState(true);
        RALocation l_init = ra.addState(true);
        RALocation l_1 = ra.addState(true);

        SymbolicDataValueGenerator.RegisterGenerator rgen = new SymbolicDataValueGenerator.RegisterGenerator();
        SymbolicDataValue.Register r1 = rgen.next(dt);
        SymbolicDataValue.Register r2 = rgen.next(dt);
        SymbolicDataValue.Register r3 = rgen.next(dt);
        SymbolicDataValueGenerator.ParameterGenerator pgen = new SymbolicDataValueGenerator.ParameterGenerator();
        SymbolicDataValue.Parameter p = pgen.next(dt);

        TransitionGuard pequal0 = new TransitionGuard(new AtomicGuardExpression(zero, Relation.EQUALS, p));
        TransitionGuard pequal1 = new TransitionGuard(new AtomicGuardExpression(one, Relation.EQUALS, p));
        TransitionGuard r2equal0And = new TransitionGuard(new Conjunction(
                new AtomicGuardExpression(p, Relation.EQUALS, r2),
                new AtomicGuardExpression(zero, Relation.EQUALS, p)));

        TransitionGuard addAnd = new TransitionGuard(new Conjunction(
                new AtomicGuardExpression(zero, Relation.EQUALS, r3),
                new AtomicGuardExpression(one, Relation.EQUALS, p)));
        TransitionGuard nextAnd = new TransitionGuard(new Conjunction(
                new AtomicGuardExpression(one, Relation.EQUALS, r2),
                new AtomicGuardExpression(zero, Relation.EQUALS, p)));

        VarMapping<SymbolicDataValue.Register, SymbolicDataValue> storeR1Mapping = new VarMapping<>();
        storeR1Mapping.put(r1, p);
        VarMapping<SymbolicDataValue.Register, SymbolicDataValue> storeR2R3Mapping = new VarMapping<>();
        storeR2R3Mapping.put(r2, p);
        storeR2R3Mapping.put(r3, p);
        VarMapping<SymbolicDataValue.Register, SymbolicDataValue> addMapping = new VarMapping<>();
        addMapping.put(r3, r2);
        addMapping.put(r2, r1);
        addMapping.put(r1, p);
        VarMapping<SymbolicDataValue.Register, SymbolicDataValue> nextMapping = new VarMapping<>();
        nextMapping.put(r1, r2);
        nextMapping.put(r2, r3);
        nextMapping.put(r3, p);
        VarMapping<SymbolicDataValue.Register, SymbolicDataValue> storeR2inR1Mapping = new VarMapping<>();
        storeR2inR1Mapping.put(r1, r2);

        Assignment storeR1 = new Assignment(storeR1Mapping);
        Assignment storeR2inR1 = new Assignment(storeR2inR1Mapping);
        Assignment storeR2R3 = new Assignment(storeR2R3Mapping);
        Assignment addAss = new Assignment(addMapping);
        Assignment nextAss = new Assignment(nextMapping);



        ra.addTransition(l_eps, init, new InputTransition(pequal0, init, l_eps, l_init, storeR2R3));

        ra.addTransition(l_init, add, new InputTransition(pequal1, add, l_init, l_1, storeR1));

        ra.addTransition(l_1, add, new InputTransition(addAnd, add, l_1, l_1, addAss));
        ra.addTransition(l_1, next, new InputTransition(nextAnd, next, l_1, l_1, nextAss));
        ra.addTransition(l_1, next, new InputTransition(r2equal0And, next, l_1, l_init, storeR2inR1));


        try {
            FileWriter writer = new FileWriter("ideal.dot");
            writer.write(new RAToDot(ra, false).toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ra;

    }

    private static Hypothesis ralib_learn() {
        ParameterizedSymbol[] functions = {init, add, next};

        Map<DataType, Theory> teachers = new LinkedHashMap<>();
        teachers.put(dt, new IntegerEqualityTheory(dt));
        ConstraintSolver solver = new SimpleConstraintSolver();
        Constants consts = new Constants();
        consts.put(zero, new DataValue<>(dt, 0));
        consts.put(one, new DataValue<>(dt, 1));
        Measurements mes = new Measurements();

        TreeOracle mto = new MeasuringOracle(new MultiTheoryTreeOracle(
                new SimulatorOracle(createAutomaton()), teachers, consts, solver), mes);

        SDTLogicOracle slo = new MultiTheorySDTLogicOracle(consts, solver);

        TreeOracleFactory hypFactory = (RegisterAutomaton hyp) ->
                new MultiTheoryTreeOracle(new SimulatorOracle(hyp), teachers,
                        consts, solver);

        RaLearningAlgorithm learner = new RaLambda(mto, hypFactory, slo, consts, false, functions);
        DefaultQuery<PSymbolInstance, Boolean> ce = null;

        /*IOEquivalenceOracle eqOracle = new RandomWordsEQOracle<RegisterAutomaton, PSymbolInstance, Boolean>
                (dwOracle, 0, 50, 10000); -- could not use, does not implement right interface*/
        IOEquivalenceOracle eqOracle = new RandomWalk(
                new Random(1),
                new SimulatorOracle(createAutomaton()),
                1000000,
                teachers,
                consts,
                Arrays.stream(functions).toList());


//      LEARN
        int check = 0;
        while (check < 100) {
            check++;
            System.out.println("Check " + check);
            learner.learn();
            Hypothesis hyp = learner.getHypothesis();
            ce = eqOracle.findCounterExample(hyp, null);
            System.out.println(ce);
            if (ce == null) {
                break;
            }
            learner.addCounterexample(ce);
        }

        Hypothesis hyp = learner.getHypothesis();

        return hyp;

    }

    public static void main(String[] args) {
//        Controller app = new Controller();
//        app.start();


        /*DataType dataType = new DataType("INT", Integer.class);
        WordBuilder<PSymbolInstance> wordBuilder = new WordBuilder<PSymbolInstance>();
        wordBuilder.add(new PSymbolInstance(new Functions().getArray()[1].getBaseSymbol(), new DataValue<>(dataType, 1)));
        wordBuilder.add(new PSymbolInstance(new Functions().getArray()[1].getBaseSymbol(), new DataValue<>(dataType, 1)));
        wordBuilder.add(new PSymbolInstance(new Functions().getArray()[0].getBaseSymbol(), new DataValue<>(dataType, 0)));
        wordBuilder.add(new PSymbolInstance(new Functions().getArray()[0].getBaseSymbol(), new DataValue<>(dataType, 0)));
        DefaultQuery<PSymbolInstance, Boolean> query = new DefaultQuery<>(wordBuilder.toWord());
        System.out.println(query);
        new IterOracle().processQuery(query);
        System.out.println(query);*/


        Hypothesis hyp = ralib_learn();
        String hypString = new RAToDot(hyp, false).toString();   // TODO: test with true
        System.out.println(hypString);
        try {
            FileWriter writer = new FileWriter("ralearn.dot");
            writer.write(hypString);
            writer.close();

            Graphviz.fromString(hypString).render(Format.PNG).toFile(new File("ra-out.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
