package org.example.controller;

import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.oracle.equivalence.DFARandomWMethodEQOracle;
import de.learnlib.oracle.equivalence.RandomWordsEQOracle;
import de.learnlib.oracle.equivalence.mealy.RandomWalkEQOracle;
import de.learnlib.ralib.automata.RegisterAutomaton;
import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.equivalence.IOEquivalenceOracle;
import de.learnlib.ralib.learning.*;
import de.learnlib.ralib.learning.ralambda.RaLambda;
import de.learnlib.ralib.learning.rastar.RaStar;
import de.learnlib.ralib.oracles.DataWordOracle;
import de.learnlib.ralib.oracles.SDTLogicOracle;
import de.learnlib.ralib.oracles.TreeOracleFactory;
import de.learnlib.ralib.oracles.mto.MultiTheorySDTLogicOracle;
import de.learnlib.ralib.oracles.mto.MultiTheoryTreeOracle;
import de.learnlib.ralib.solver.ConstraintSolver;
import de.learnlib.ralib.solver.simple.SimpleConstraintSolver;
import de.learnlib.ralib.theory.Theory;
import de.learnlib.ralib.tools.ClassAnalyzer;
import de.learnlib.ralib.tools.config.Configuration;
import de.learnlib.ralib.tools.theories.IntegerEqualityTheory;
import de.learnlib.ralib.words.InputSymbol;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import org.example.rahelper.RandomWalk;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class Runner {


    /*private Hypothesis ralib_learn() {
        Map<DataType, Theory> teachers = new LinkedHashMap<>();
        DataType dataType = new DataType("INTEGER", Integer.class);
        teachers.put(dataType, new IntegerEqualityTheory(dataType));
        ConstraintSolver solver = new SimpleConstraintSolver();
        Constants consts = new Constants();
        try {
            DataWordOracle dwOracle = new IterRAOracle();
            Measurements mes = new Measurements();

            MeasuringOracle mto = new MeasuringOracle(new MultiTheoryTreeOracle(
                    dwOracle, teachers, consts, solver), mes);

            SDTLogicOracle slo = new MultiTheorySDTLogicOracle(consts, solver);

            TreeOracleFactory hypFactory = (RegisterAutomaton hyp) ->
                    new MultiTheoryTreeOracle(dwOracle, teachers,
                            new Constants(), solver);

            RaLearningAlgorithm learner = new RaStar(mto, hypFactory, slo, consts, false, I_PUSH, I_POP);
            DefaultQuery<PSymbolInstance, Boolean> ce = null;

            IOEquivalenceOracle eqOracle = new RandomWalk(random, ioCache,
                    0.1, // reset probability
                    0.8, // prob. of choosing a fresh data value
                    10000, // number of runs
                    6, // max depth
                    teachers,
                    consts, Arrays.asList(I_PUSH, I_POP));
//            Use instead
            RandomWordsEQOracle

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
        } catch (Exception e) {
            throw new RuntimeException("Learning experiment failed: " + e);
        }

    }*/

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
        Controller app = new Controller();
        app.start();
//        ralib_learn();
    }
}
