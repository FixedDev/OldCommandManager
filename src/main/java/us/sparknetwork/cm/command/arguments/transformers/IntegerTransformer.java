package us.sparknetwork.cm.command.arguments.transformers;

import us.sparknetwork.cm.command.arguments.ParameterTransformer;

public class IntegerTransformer implements ParameterTransformer<Integer> {
    @Override
    public Integer transform(String param) {
        try{
            return Integer.parseInt(param);
        } catch(NumberFormatException e){
            return 0;
        }
    }
}
