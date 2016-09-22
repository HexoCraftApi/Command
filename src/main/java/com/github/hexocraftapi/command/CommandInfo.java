package com.github.hexocraftapi.command;

/*
 * Copyright 2016 hexosse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */

public class CommandInfo
{
    private final CommandSender sender;
    private final Player player;
    private final String commandName;
    private final Command<?> command;
    private List<String> args;
    private Map<String,String> namedArgs = new LinkedHashMap<String,String>();


    /**
     * Create a new CommandInfo representing one commandName invocation.
     * @param sender The CommandSender who invoked this (can be a console)
     * @param command The Command we're executing.
     * @param label The alias of the command used
     * @param args The commandName arguments.
     * @param namedArgs List of argument by name.
     */
    public CommandInfo(CommandSender sender, Command<?> command, String label, String[] args, Map<String,String> namedArgs)
    {
        Validate.notNull(sender);
        Validate.notNull(command);

        final Player player = (sender instanceof Player) ? (Player)sender : null;

        this.sender = sender;
        this.player = player;
        this.command = command;
        this.commandName = command.getName();
        this.args = new ArrayList<String>(Arrays.asList(args));
        this.namedArgs = namedArgs;
    }

    /**
     * Get the CommandSender who invoked this.
     * @return a CommandSender.
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Get the player who invoked this. Can be null if running at the console.
     * @return a Player, or null if this is a console commandName
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the CommandSender who invoked this.
     * @return a CommandSender.
     */
    public CommandSender[] getSenders() {
        List<CommandSender> commandSenderList = new ArrayList<CommandSender>(2);
        if(getSender() != null) commandSenderList.add(getSender());
        if(getPlayer() != null && !commandSenderList.contains(getPlayer())) commandSenderList.add(getPlayer());
        return commandSenderList.toArray(new CommandSender[commandSenderList.size()]);
    }

    /**
     * Get the base commandName which was called for this sub-commandName call.
     * @return A base commandName string.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Get the Command that invoked this call.
     * @return a Command.
     */
    public Command<?> getCommand() {
        return command;
    }

    /**
     * How many arguments we got.
     * @return Number of arguments
     */
    public int numArgs() {
        return this.args.size();
    }

    /**
     * Get the whole list of commandName arguments.
     * @return List of arguments.
     */
    public List<String> getArgs() {
        return this.args;
    }

    /**
     * Get the first argument.
     * @return The first argument.
     */
    public String getFirstArg()
    {
        if(this.args.size()>0)
            return this.args.get(0);
        return null;
    }

    /**
     * Get the last argument.
     * @return The last argument.
     */
    public String getLastArg()
    {
        if(this.args.size() > 0)
            return this.args.get(this.args.size() - 1);
        return null;
    }

    /**
     * @param name Name of the argument
     *
     * @return true if exist
     */
    public boolean hasNamedArg(String name)
    {
        return namedArgs.containsKey(name);
    }

    /**
     * @param name Name of the argument
     * @param value Value of the argument
     */
    public void setNamedArg(String name, String value)
    {
        namedArgs.put(name, value);
    }

    /**
     * @param name Name of the argument
     *
     * @return Value of the argument from its name
     */
    public String getNamedArg(String name)
    {
        return namedArgs.get(name);
    }

}

