package us.sparknetwork.cm.command.arguments.transformers;

import us.sparknetwork.cm.command.arguments.ParameterTransformer;

public class DoubleTransformer implements ParameterTransformer<Double> {
    @Override
    public Double transform(String param) {
        try{
            return Double.parseDouble(param);
        } catch(NumberFormatException e){
            return 0D;
        }
    }
}
