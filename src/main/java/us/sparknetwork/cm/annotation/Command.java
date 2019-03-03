package us.sparknetwork.cm.annotation;

import org.bukkit.ChatColor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String[] names();

    boolean onlyPlayer() default false;

    String usage() default "";

    String desc() default "";

    int min() default 0;

    int max() default -1;

    char[] flags() default {};

    String permission() default "";

    String permissionMessage() default "No Permission.";
}
