package org.example.model;

import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.GrowingMapAlphabet;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

class FunctionSymbol extends ParameterizedSymbol {
    public FunctionSymbol(String name) {
        super(name);
    }
}

public class Functions {
    private final Map<PSymbolInstance, Function> map = new LinkedHashMap<>();
    public enum Function {
        hasNextTrue,
        hasNextFalse,
        next,
        remove,
        add
    }
    public Functions() {
        for (Function f : Function.values()) {
            map.put(new PSymbolInstance(new FunctionSymbol(f.toString())), f);
        }
    }

    public Function getFunction(PSymbolInstance i) {
        return map.get(i);
    }

    public Alphabet<PSymbolInstance> getAlphabet() {
        Alphabet<PSymbolInstance> alphabet = new GrowingMapAlphabet<>();
        alphabet.addAll(Arrays.stream(getArray()).toList());
        return alphabet;
    }

    public PSymbolInstance[] getArray() {
        return map.keySet().toArray(new PSymbolInstance[0]);
    }

};
