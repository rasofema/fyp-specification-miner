package org.example.controller;

import de.learnlib.api.query.DefaultQuery;
import de.learnlib.ralib.automata.RegisterAutomaton;
import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.equivalence.IOEquivalenceOracle;
import de.learnlib.ralib.learning.*;
import de.learnlib.ralib.learning.ralambda.RaLambda;
import de.learnlib.ralib.learning.rastar.RaStar;
import de.learnlib.ralib.oracles.DataWordOracle;
import de.learnlib.ralib.oracles.SDTLogicOracle;
import de.learnlib.ralib.oracles.TreeOracle;
import de.learnlib.ralib.oracles.TreeOracleFactory;
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
import net.automatalib.automata.Automaton;
import net.automatalib.serialization.dot.GraphDOT;
import org.example.model.Functions;
import org.example.model.IterOracle;
import org.example.rahelper.RandomWalk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class Runner {

    private static Hypothesis ralib_learn() {
        PSymbolInstance[] array = new Functions().getArray();
        ParameterizedSymbol[] functions = new ParameterizedSymbol[array.length];
        for (int i=0; i<array.length; i++) {
            functions[i] = array[i].getBaseSymbol();
        }


        Map<DataType, Theory> teachers = new LinkedHashMap<>();
        DataType dataType = new DataType("INTEGER", Integer.class);
        teachers.put(dataType, new IntegerEqualityTheory(dataType));
        ConstraintSolver solver = new SimpleConstraintSolver();
        Constants consts = new Constants();
        DataWordOracle dwOracle = new IterOracle();
        Measurements mes = new Measurements();

        TreeOracle mto = new MeasuringOracle(new MultiTheoryTreeOracle(
                dwOracle, teachers, consts, solver), mes);

        SDTLogicOracle slo = new MultiTheorySDTLogicOracle(consts, solver);

        TreeOracleFactory hypFactory = (RegisterAutomaton hyp) ->
                new MultiTheoryTreeOracle(dwOracle, teachers,
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
            if (ce.getOutput().equals(hyp.accepts(ce.getInput()))) {
                throw new RuntimeException("Should not accept");
            }
            learner.addCounterexample(ce);
        }

        Hypothesis hyp = learner.getHypothesis();

        return hyp;

    }

    /*private static void ralib_learn() {
        ClassAnalyzer analyzer = new ClassAnalyzer();
        try {
            analyzer.setup(new Configuration(new File("config")));
            analyzer.run();
        } catch (Exception e) {
            throw new RuntimeException(e);⁄
        }
    }*/

    public static void main(String[] args) {
//        Controller app = new Controller();
//        app.start();
        Hypothesis hyp = ralib_learn();
        System.out.println("-------------------------------------------------------");
        System.out.println();
        System.out.println("Model: ");
        PSymbolInstance[] array = new Functions().getArray();
        ParameterizedSymbol[] functions = new ParameterizedSymbol[array.length];
        for (int i=0; i<array.length; i++) {
            functions[i] = array[i].getBaseSymbol();
        }
        try {
            GraphDOT.write(hyp, Arrays.stream(functions).toList(), new FileWriter("ralearn.dot"));
            Graphviz.fromFile(new File("ralearn.dot")).render(Format.PNG).toFile(new File("ra-out.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
