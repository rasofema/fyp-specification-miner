package org.example.controller;

import de.learnlib.algorithm.LearningAlgorithm;
import de.learnlib.algorithm.c3al.C3AL;
import de.learnlib.algorithm.c3al.EventHandler;
import de.learnlib.algorithm.c3al.LatestWithMaxEventHandler;
import de.learnlib.algorithm.c3al.LearningFinishedException;
import de.learnlib.algorithm.kv.dfa.KearnsVaziraniDFABuilder;
import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.algorithm.lstar.dfa.ExtensibleLStarDFABuilder;
import de.learnlib.algorithm.ttt.dfa.TTTLearnerDFABuilder;
import de.learnlib.oracle.MembershipOracle;
import de.learnlib.oracle.equivalence.AbstractTestWordEQOracle;
import de.learnlib.oracle.equivalence.DFAWpMethodEQOracle;
import de.learnlib.query.DefaultQuery;
import de.learnlib.ralib.automata.RegisterAutomaton;
import de.learnlib.ralib.automata.tree.AdaptiveRATreeBuilder;
import de.learnlib.ralib.data.Constants;
import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.data.SymbolicDataValue;
import de.learnlib.ralib.learning.ralambda.RaDT;
import de.learnlib.ralib.learning.ralambda.RaLambda;
import de.learnlib.ralib.learning.rastar.RaStar;
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
import net.automatalib.alphabet.Alphabet;
import net.automatalib.automaton.Automaton;
import net.automatalib.automaton.concept.Output;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.incremental.dfa.tree.AdaptiveDFATreeBuilder;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.word.Word;
import org.example.helper.SupportedLearningAlgorithm;
import org.example.model.Functions;
import org.example.model.IterOracle;
import org.example.helper.RandomWalk;
import org.example.view.MainFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;


public class Controller {

    private final Alphabet<PSymbolInstance> alphabet;
    private final MainFrame frame;
    private final Functions functions;
    private C3AL<?, PSymbolInstance, Boolean> learner;
    private SupportedLearningAlgorithm algorithm;
    private final int maxQueries = 200000;
    public Controller() {
        this.functions = new Functions();
        this.alphabet = this.functions.getAlphabet();
        this.frame = new MainFrame(this);
        this.algorithm = SupportedLearningAlgorithm.DFA_ClassicLStar;
    }

    public void setAlgorithm(SupportedLearningAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    private Output<PSymbolInstance, Boolean> learn_ra(DataWordOracle mqOracle) {
        PSymbolInstance[] array = this.functions.getArray();
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
        consts.put(new SymbolicDataValue.Constant(dataType, 1), new DataValue<>(dataType, 1));

        SDTLogicOracle slo = new MultiTheorySDTLogicOracle(consts, solver);

        TreeOracleFactory hypFactory = (RegisterAutomaton hyp) ->
                new MultiTheoryTreeOracle(new SimulatorOracle(hyp), teachers,
                        consts, solver);

        AbstractTestWordEQOracle<RegisterAutomaton, PSymbolInstance, Boolean> eqOracle = new RandomWalk(
                new Random(1),
                mqOracle,
                1000000,
                teachers,
                consts,
                Arrays.stream(functions).toList());


        Function<MembershipOracle<PSymbolInstance, Boolean>, LearningAlgorithm<RegisterAutomaton, PSymbolInstance, Boolean>> constructor =
                switch (this.algorithm) {
                    case RaDT -> (MembershipOracle<PSymbolInstance, Boolean> memOracle) ->
                            new RaDT(new MultiTheoryTreeOracle(memOracle, teachers, consts, solver),
                                    hypFactory, slo, consts, false, functions);
                    case RaLambda -> (MembershipOracle<PSymbolInstance, Boolean> memOracle) ->
                            new RaLambda(new MultiTheoryTreeOracle(memOracle, teachers, consts, solver),
                                    hypFactory, slo, consts, false, functions);
                    case RaStar -> (MembershipOracle<PSymbolInstance, Boolean> memOracle) ->
                            new RaStar(new MultiTheoryTreeOracle(memOracle, teachers, consts, solver),
                                    hypFactory, slo, consts, false, functions);
                    default -> throw new UnsupportedOperationException();
        };

        this.learner = new C3AL_RA(constructor, mqOracle, mqOracle, eqOracle, this.alphabet,
                new AdaptiveRATreeBuilder(this.alphabet), new Random(1),
                new LatestWithMaxEventHandler<>(this.maxQueries));

//      LEARN
        learner.run();

        return learner.getHypothesisModel();

    }

    private Output<PSymbolInstance, Boolean> learn_dfa(MembershipOracle.DFAMembershipOracle<PSymbolInstance> mqOracle) {
        // construct L* instance

        DFAWpMethodEQOracle<PSymbolInstance> eqOracle = new DFAWpMethodEQOracle<>(mqOracle, 2);
        Function<MembershipOracle<PSymbolInstance, Boolean>, LearningAlgorithm<DFA<?, PSymbolInstance>, PSymbolInstance, Boolean>> constructor =
                switch (this.algorithm) {
                    case DFA_ClassicLStar -> (MembershipOracle<PSymbolInstance, Boolean> memOracle) ->
                            new ClassicLStarDFABuilder<PSymbolInstance>().withAlphabet(alphabet).withOracle(memOracle).create();
                    case DFA_ExtensibleLStar -> (MembershipOracle<PSymbolInstance, Boolean> memOracle) ->
                            new ExtensibleLStarDFABuilder<PSymbolInstance>().withAlphabet(alphabet).withOracle(memOracle).create();
                    case DFA_KV -> (MembershipOracle<PSymbolInstance, Boolean> memOracle) ->
                            new KearnsVaziraniDFABuilder<PSymbolInstance>().withAlphabet(alphabet).withOracle(memOracle).create();
                    case DFA_TTT -> (MembershipOracle<PSymbolInstance, Boolean> memOracle) ->
                            new TTTLearnerDFABuilder<PSymbolInstance>().withAlphabet(alphabet).withOracle(memOracle).create();
                    default -> throw new UnsupportedOperationException();
                };

        this.learner = new C3AL_DFA(constructor, mqOracle, mqOracle, eqOracle, this.alphabet,
                new AdaptiveDFATreeBuilder<>(this.alphabet), new Random(1),
                new LatestWithMaxEventHandler<>(this.maxQueries)
        );


        // LEARN
        learner.run();

        return learner.getHypothesisModel();


    }

    public void updateFromUser(Collection<String> cex, boolean out) {
        System.out.println("Called update from user with:");
        System.out.println(cex);
        System.out.println(out);

        try {
            learner.refineHypothesisFromUser(
                    new DefaultQuery<>(
                            Word.epsilon(),
//                          TODO: NEED TO FIX TYPE OF GETTING
                            Word.fromList(cex.stream().map(this.functions::getFunctionFromNameWithoutData).toList()),
                            out));
        } catch (LearningFinishedException ignored) {}

        learner.run();
        outputResults((Automaton<?, PSymbolInstance, ?>) learner.getHypothesisModel(), "ra-out");

    }

    public void outputResults(Automaton<?, PSymbolInstance, ?> automaton, String file) {
        System.out.println("Output model.");
        try {
            GraphDOT.write(automaton, alphabet, new FileWriter(file+".dot"));
            Graphviz.fromFile(new File(file+".dot")).render(Format.PNG).toFile(new File("out.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        IterOracle iterOracle = new IterOracle();
        if (SupportedLearningAlgorithm.isRA(this.algorithm)) {
            learn_ra(iterOracle);
        } else if (SupportedLearningAlgorithm.isDFA(this.algorithm)) {
            learn_dfa(iterOracle);
        } else {
            throw new UnsupportedOperationException();
        }

        System.out.println("Done");
        outputResults((Automaton) learner.getHypothesisModel(), "out");
        this.frame.updateImage("out.png");
    }



    private class C3AL_RA extends C3AL<RegisterAutomaton, PSymbolInstance, Boolean> {
        public C3AL_RA(Function<MembershipOracle<PSymbolInstance, Boolean>,
                LearningAlgorithm<RegisterAutomaton, PSymbolInstance, Boolean>> constructor,
                            MembershipOracle<PSymbolInstance, Boolean> memOracle,
                            MembershipOracle<PSymbolInstance, Boolean> eqOracle,
                            AbstractTestWordEQOracle<RegisterAutomaton, PSymbolInstance, Boolean> testOracle,
                            Alphabet<PSymbolInstance> alphabet, AdaptiveRATreeBuilder cache, Random random,
                            EventHandler<RegisterAutomaton, PSymbolInstance, Boolean> eventHandler) {
            super(constructor, memOracle, eqOracle, testOracle, alphabet, cache, 0.0, true, random, eventHandler);
        }
    }

    private class C3AL_DFA extends C3AL<DFA<?, PSymbolInstance>, PSymbolInstance, Boolean> {
        public C3AL_DFA(Function<MembershipOracle<PSymbolInstance, Boolean>,
                LearningAlgorithm<DFA<?, PSymbolInstance>, PSymbolInstance, Boolean>> constructor,
                        MembershipOracle<PSymbolInstance, Boolean> memOracle,
                        MembershipOracle<PSymbolInstance, Boolean> eqOracle,
                        AbstractTestWordEQOracle<DFA<?, PSymbolInstance>, PSymbolInstance, Boolean> testOracle,
                        Alphabet<PSymbolInstance> alphabet, AdaptiveDFATreeBuilder<PSymbolInstance> cache, Random random,
                        EventHandler<DFA<?, PSymbolInstance>, PSymbolInstance, Boolean> eventHandler) {
            super(constructor, memOracle, eqOracle, testOracle, alphabet, cache, 0.0, true, random, eventHandler);
        }
    }
}

