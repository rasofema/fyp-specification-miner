package org.example.controller;

import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.oracle.equivalence.DFAWpMethodEQOracle;
import de.learnlib.api.query.DefaultQuery;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.GrowingMapAlphabet;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.serialization.dot.GraphDOT;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.example.model.Function;
import org.example.model.IterOracle;
import org.example.view.MainFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Controller {

    private final Alphabet<Function> alphabet;
    //private final MainFrame frame;
    public Controller() {
        this.alphabet = new GrowingMapAlphabet<>();
        this.alphabet.addAll(Arrays.asList(Function.values()));
        //this.frame = new MainFrame(this);
    }

    public Alphabet<Function> getAlphabet() {
        return alphabet;
    }

    private DFA<?, Function> learn(MembershipOracle.DFAMembershipOracle<Function> mqOracle) {
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

    private void showResults(DFA<?, Function> dfa, String file) {
        // report results
        System.out.println("-------------------------------------------------------");
        System.out.println();
        System.out.println("Model: ");
        try {
            GraphDOT.write(dfa, alphabet, new FileWriter(file+".dot"));
            Graphviz.fromFile(new File(file+".dot")).render(Format.PNG).toFile(new File("out.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Visualization.visualize(dfa, alphabet);

    }

    public void start() {
        showResults(learn(new IterOracle()), "exception");

        //final IterOracle iterOracle = new IterOracle();
        //iterOracle.setSilentFailAddTrue();
        //showResults(learn(iterOracle), "silent");


    }
}

