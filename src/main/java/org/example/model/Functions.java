package org.example.model;

import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.GrowingMapAlphabet;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

class FunctionSymbol extends ParameterizedSymbol {
    public FunctionSymbol(String name) {
        super(name, new DataType("INT", Integer.class));
    }
}

public class Functions {
    private final Map<PSymbolInstance, Function> map = new LinkedHashMap<>();
    private final Map<ParameterizedSymbol, Function> map2 = new LinkedHashMap<>();
    public enum Function {
//        hasNextTrue,
//        hasNextFalse,
        next,
//        remove,
        add,
        init
    }
    public Functions() {
        for (Function f : Function.values()) {
            map2.put(new FunctionSymbol(f.toString()), f);
        }
        for (Function f : Function.values()) {
            map.put(new PSymbolInstance(new FunctionSymbol(f.toString())), f);
        }
    }

    public Function getFunction(ParameterizedSymbol i) {
        return map2.get(i);
    }

    public Alphabet<PSymbolInstance> getAlphabet() {
        Alphabet<PSymbolInstance> alphabet = new GrowingMapAlphabet<>();
        alphabet.addAll(Arrays.stream(getArray()).toList());
        return alphabet;
    }

    public PSymbolInstance[] getArray() {
        return map.keySet().toArray(new PSymbolInstance[0]);
    }

}
