package org.example.model;

import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.GrowingMapAlphabet;

import java.util.*;

class FunctionSymbol extends ParameterizedSymbol {
    public FunctionSymbol(String name) {
        super(name);
    }
}

public class Functions {
    private final Map<ParameterizedSymbol, Function> map = new LinkedHashMap<>();
    private final Map<String, PSymbolInstance> mapString = new LinkedHashMap<>();
    public enum Function {
//        hasNextTrue,
//        hasNextFalse,
        next,
        remove,
        add
    }
    public Functions() {
        for (Function f : Function.values()) {
            FunctionSymbol fs = new FunctionSymbol(f.toString());
            map.put(fs, f);
            mapString.put(f.name(), new PSymbolInstance(fs));
        }
    }

    public Function getFunction(ParameterizedSymbol i) {
        return map.get(i);
    }

    public PSymbolInstance getPSymbolInstance(String name) {
        return mapString.get(name);
    }

    public Alphabet<PSymbolInstance> getAlphabet() {
        Alphabet<PSymbolInstance> alphabet = new GrowingMapAlphabet<>();
        alphabet.addAll(getList());
        return alphabet;
    }

    public List<PSymbolInstance> getList() {
        return map.keySet().stream().map(PSymbolInstance::new).toList();
    }

}
