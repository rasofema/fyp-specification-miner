package org.example.controller;

import de.learnlib.query.DefaultQuery;
import de.learnlib.ralib.automata.RegisterAutomaton;
import de.learnlib.ralib.automata.util.RAToDot;
import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.data.SymbolicDataValue;
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
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import net.automatalib.serialization.dot.GraphDOT;
import org.example.model.Functions;
import org.example.model.IterOracle;
import org.example.rahelper.RandomWalk;

import java.io.*;
import java.util.*;


public class Runner {

    private static Hypothesis ralib_learn() {
        PSymbolInstance[] array = new Functions().getArray();
        ParameterizedSymbol[] functions = new ParameterizedSymbol[array.length];
        for (int i=0; i<array.length; i++) {
            functions[i] = array[i].getBaseSymbol();
        }


        Map<DataType, Theory> teachers = new LinkedHashMap<>();
        DataType dataType = new DataType("INT", Integer.class);
        teachers.put(dataType, new IntegerEqualityTheory(dataType));
        ConstraintSolver solver = new SimpleConstraintSolver();
        Constants consts = new Constants();
        consts.put(new SymbolicDataValue.Constant(dataType, 0), new DataValue<>(dataType, 0));
        DataWordOracle dwOracle = new IterOracle();
        Measurements mes = new Measurements();

        TreeOracle mto = new MeasuringOracle(new MultiTheoryTreeOracle(
                dwOracle, teachers, consts, solver), mes);

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
                dwOracle,
                10000,
                teachers,
                consts,
                Arrays.stream(functions).toList());


//      LEARN
        int check = 0;
        while (check < 100) {
            check++;
            learner.learn();
            Hypothesis hyp = learner.getHypothesis();
            ce = eqOracle.findCounterExample(hyp, null);
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
