package org.example.controller;

import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.oracle.equivalence.DFAWpMethodEQOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.ralib.words.PSymbolInstance;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import net.automatalib.words.Alphabet;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.serialization.dot.GraphDOT;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.example.model.Functions;
import org.example.model.IterOracle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Controller {

    private final Alphabet<PSymbolInstance> alphabet;
    //private final MainFrame frame;
    public Controller() {
        this.alphabet = new Functions().getAlphabet();
        //this.frame = new MainFrame(this);
    }

    public Alphabet<PSymbolInstance> getAlphabet() {
        return alphabet;
    }

    private DFA<?, PSymbolInstance> learn(MembershipOracle.DFAMembershipOracle<PSymbolInstance> mqOracle) {
        // construct L* instance
        ClassicLStarDFA<PSymbolInstance> learner =
                new ClassicLStarDFABuilder<PSymbolInstance>().withAlphabet(alphabet) // input alphabet
                        .withOracle(mqOracle) // membership oracle
                        .create();
        DFAWpMethodEQOracle<PSymbolInstance> eqOracle = new DFAWpMethodEQOracle<>(mqOracle, 2);

        learner.startLearning();
        while (true) {
            DFA<?, PSymbolInstance> hyp = learner.getHypothesisModel();

            // search for counterexample
            @Nullable DefaultQuery<PSymbolInstance, Boolean> o = eqOracle.findCounterExample(hyp, alphabet);
            System.out.println("Counterexample: " + o);

            // no counter example -> learning is done
            if (o == null) break;

            // return counter example to the learner
            learner.refineHypothesis(o);
        }

        return learner.getHypothesisModel();
    }

    public void showResults(DFA<?, PSymbolInstance> dfa, String file) {
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
//        showResults(learn(new IterOracle()), "exception");

        final IterOracle iterOracle = new IterOracle();
        //iterOracle.setSilentFailAddTrue();
        //showResults(learn(iterOracle), "silent");


    }
}

