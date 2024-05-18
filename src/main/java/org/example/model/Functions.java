package org.example.model;

import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.GrowingMapAlphabet;

import java.util.*;

import static org.example.model.Functions.Function.*;

class FunctionSymbol extends ParameterizedSymbol {
    public FunctionSymbol(String name) {
        super(name);
    }
}

public class Functions {
    private final Map<ParameterizedSymbol, Function> map = new LinkedHashMap<>();
    private final Map<String, PSymbolInstance> mapString = new LinkedHashMap<>();
    public enum Function {
        hasNextTrue,
        hasNextFalse,
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
        for (ParameterizedSymbol sym : map.keySet()) {
            if (sym.getName().equals(name))
                return new PSymbolInstance(sym);
        }
        return null;
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
