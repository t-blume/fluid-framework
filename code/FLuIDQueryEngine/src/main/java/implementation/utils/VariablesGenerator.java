package implementation.utils;

import java.util.HashMap;
import java.util.Map;

public class VariablesGenerator {
    private static VariablesGenerator instance;
    public static VariablesGenerator getInstance() {
        instance = (instance == null) ? new VariablesGenerator() : instance;
        return instance;
    }




    private Map<String, Integer> map = new HashMap<>();

    //this basically represents the counter
    private String variable = "a";

    /**
     * Gets unique variable name in a synchronised way. It basically just adds a counter which is unique to the variable.
     * @param baseName if you want the base of the variable to be a certain string.
     * @return variable name
     */
    public synchronized String getVariableName(String baseName) {
        String variable = (baseName.equals("")|| baseName.equals(null)) ? (getUniqueVariableBase()) : baseName;
        int count;
        if (map.containsKey(variable)) {
          count = map.get(variable) + 1;
          map.put(variable,count);
        }
        else {
            count = 0;
            map.put(variable,count);
        }
        variable+=count;
        return variable;
    }

    private String getUniqueVariableBase() {
        char c = variable.charAt(0);
        c++;
        String result = String.valueOf(c);
        variable = result;

        return result;
    }
}
