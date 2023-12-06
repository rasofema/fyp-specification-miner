package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.oracle.MembershipOracle.DFAMembershipOracle;
import de.learnlib.oracle.equivalence.DFAWpMethodEQOracle;
import de.learnlib.query.DefaultQuery;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.GrowingMapAlphabet;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.visualization.Visualization;
import org.checkerframework.checker.nullness.qual.Nullable;


public class Main {

    private static DFA<?, Function> learn(DFAMembershipOracle<Function> mqOracle, Alphabet<Function> alphabet) {
        // construct L* instance
        ClassicLStarDFA<Function> learner =
                new ClassicLStarDFABuilder<Function>().withAlphabet(alphabet) // input alphabet
                        .withOracle(mqOracle) // membership oracle
                        .create();
        DFAWpMethodEQOracle<Function> eqOracle = new DFAWpMethodEQOracle<>(mqOracle, 2);

        learner.startLearning();
        while (true) {
            DFA<?, Function> hyp = learner.getHypothesisModel();

            // search for counterexample
            @Nullable DefaultQuery<Function, Boolean> o = eqOracle.findCounterExample(hyp, alphabet);
            System.out.println("Counterexample: " + o);

            // no counter example -> learning is done
            if (o == null) break;

            // return counter example to the learner
            learner.refineHypothesis(o);
        }

        return learner.getHypothesisModel();
    }

    private static void showResults(DFA<?, Function> dfa, Alphabet<Function> alphabet, String file) throws IOException {
        // report results
        System.out.println("-------------------------------------------------------");
        System.out.println();
        System.out.println("Model: ");
        GraphDOT.write(dfa, alphabet, new FileWriter(file+".dot"));

        Visualization.visualize(dfa, alphabet);

    }

    public static void main(String[] args) throws IOException {
        Alphabet<Function> alphabet = new GrowingMapAlphabet<>();
        alphabet.addAll(Arrays.asList(Function.values()));

        showResults(learn(new ExceptionIterOracle(), alphabet), alphabet, "exception");
        showResults(learn(new BooleanIterOracle(), alphabet), alphabet, "boolean");

    }

}