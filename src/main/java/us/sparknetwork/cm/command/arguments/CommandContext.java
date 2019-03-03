package us.sparknetwork.cm.command.arguments;


import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import us.sparknetwork.cm.command.arguments.transformers.DoubleTransformer;
import us.sparknetwork.cm.command.arguments.transformers.IntegerTransformer;
import us.sparknetwork.cm.command.arguments.transformers.OfflinePlayerTransformer;
import us.sparknetwork.cm.command.arguments.transformers.PlayerTransformer;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandContext {

    public static Map<Class<?>, ParameterTransformer> transformerMap;

    static {
        transformerMap = new HashMap<>();
        transformerMap.put(Double.class, new DoubleTransformer());
        transformerMap.put(Integer.class, new IntegerTransformer());
        transformerMap.put(OfflinePlayer.class, new OfflinePlayerTransformer());
        transformerMap.put(Player.class, new PlayerTransformer());
    }

    @Getter
    private String commandName;
    @Getter
    private String currentLabel;
    private String[] originalArgs;
    private List<String> parsedArgs;
    @Getter
    private List<Character> noValueFlags;

    private static Pattern flagRegex = Pattern.compile("[-]{1,2}[a-zA-Z]{1}");
    private static Pattern removeDash = Pattern.compile("[-]{1,2}");


    /**
     * This class is a style like arguments for a command that parse the flags
     *
     * @param command - The executed command name
     * @param label   - The executed command current label
     * @param args    - The executed command not parsed args
     * @param flags   - The command flags without any value
     */
    public CommandContext(String command, String label, String[] args, List<Character> flags) {
        this.commandName = command;
        this.currentLabel = label;
        this.originalArgs = args;
        parsedArgs = new ArrayList<>();

        List<String> parsingArgs = new ArrayList<>(Arrays.asList(originalArgs));
        Iterator<String> argz = parsingArgs.iterator();

        this.noValueFlags = new ArrayList<>();

        while (argz.hasNext()) {
            String arg = argz.next();

            if (StringUtils.isNotBlank(arg)) {
                if (flagRegex.matcher(arg).matches()) {
                    String flag = removeDash.matcher(arg).replaceAll("");

                    if ((flag.length() > 0) && flags.contains(flag.toCharArray()[0])) {
                        noValueFlags.add(flag.toCharArray()[0]);
                    }
                } else {
                    parsedArgs.add(arg);
                }
            } else {
                continue;
            }
        }

    }

    public List<String> getArguments() {
        return this.parsedArgs;
    }

    public String getArgument(int index) {
        return parsedArgs.get(index);
    }

    public String getJoinedArgs(int startIndex) {
        return this.getJoinedArgs(startIndex, this.getArguments().size());
    }

    public String getJoinedArgs(int startIndex, int endIndex) {
        String joinedString = getArguments().subList(startIndex, endIndex).stream().collect(Collectors.joining(" "));

        return  joinedString;
    }

    public boolean hasFlag(char character) {
        return noValueFlags.contains(character);
    }

    public <T> T getObject(int index, Class<T> transformer) {
        if (!transformerMap.containsKey(transformer)) {
            return null;
        }
        ParameterTransformer<T> paramTransformer = transformerMap.get(transformer);
        return paramTransformer.transform(this.getArgument(index));
    }
}
