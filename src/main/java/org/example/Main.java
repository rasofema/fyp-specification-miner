package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.datastructure.observationtable.OTUtils;
import de.learnlib.datastructure.observationtable.writer.ObservationTableASCIIWriter;
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

    public static void main(String[] args) throws IOException {

        DFAMembershipOracle<Function> mqOracle = new IterOracle();

        Alphabet<Function> alphabet = new GrowingMapAlphabet<>();
        alphabet.addAll(Arrays.asList(Function.values()));

        // construct L* instance
        ClassicLStarDFA<Function> learner =
                new ClassicLStarDFABuilder<Function>().withAlphabet(alphabet) // input alphabet
                        .withOracle(mqOracle) // membership oracle
                        .create();

        DFAWpMethodEQOracle<Function> eqOracle = new DFAWpMethodEQOracle<>(mqOracle, 2);
        boolean done = false;
        learner.startLearning();
        while (!done) {
            // stable hypothesis after membership queries
            DFA<?, Function> hyp = learner.getHypothesisModel();

            // search for counterexample
            @Nullable DefaultQuery<Function, Boolean> o = eqOracle.findCounterExample(hyp, alphabet);
            System.out.println("Counterexample: " + o);

            // no counter example -> learning is done
            if (o == null) {
                done = true;
                continue;
            }

            // return counter example to the learner, so that it can use
            // it to generate new membership queries
            learner.refineHypothesis(o);
        }


        // report results
        System.out.println("-------------------------------------------------------");

        // show model
        System.out.println();
        System.out.println("Model: ");
        GraphDOT.write(learner.getHypothesisModel(), alphabet, new FileWriter("hyp.dot")); // may throw IOException!

        Visualization.visualize(learner.getHypothesisModel(), alphabet);

        System.out.println("-------------------------------------------------------");

        System.out.println("Final observation table:");
        new ObservationTableASCIIWriter<>().write(learner.getObservationTable(), System.out);

        OTUtils.displayHTMLInBrowser(learner.getObservationTable());
    }

}