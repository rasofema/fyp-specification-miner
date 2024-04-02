package org.example.model;

import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.words.PSymbolInstance;
import de.learnlib.ralib.words.ParameterizedSymbol;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.GrowingMapAlphabet;

import java.util.*;

class FunctionSymbol extends ParameterizedSymbol {
    public FunctionSymbol(String name) {
        super(name, new DataType("INT", Integer.class));
    }
}

public class Functions {
    private final Map<PSymbolInstance, Function> map = new LinkedHashMap<>();
    private final Map<ParameterizedSymbol, Function> map2 = new LinkedHashMap<>();
    private final Map<String, PSymbolInstance> mapString = new LinkedHashMap<>();
    private final Map<String, PSymbolInstance> mapStringWithoutData = new LinkedHashMap<>();
    public enum Function {
        hasNextTrue,
        hasNextFalse,
        next,
        remove,
        add,
//        init
    }
    public Functions() {
        for (Function f : Function.values()) {
            FunctionSymbol fs = new FunctionSymbol(f.toString());
            map2.put(fs, f);
            map.put(new PSymbolInstance(fs), f);
            mapString.put(f.name(), new PSymbolInstance(fs, new DataValue<>(fs.getPtypes()[0], 0)));
            mapStringWithoutData.put(f.name(), new PSymbolInstance(fs));
        }
    }

    public Function getFunction(ParameterizedSymbol i) {
        return map2.get(i);
    }

    public PSymbolInstance getFunctionFromNameWithoutData(String name) {
        return mapStringWithoutData.get(name);
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
