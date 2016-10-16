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

import com.github.hexocraftapi.chat.MessageBuilder;
import com.github.hexocraftapi.chat.event.ClickEvent;
import com.github.hexocraftapi.chat.event.HoverEvent;
import com.github.hexocraftapi.command.errors.CommandErrorType;
import com.github.hexocraftapi.command.message.MessageHelp;
import com.github.hexocraftapi.message.Sentence;
import com.github.hexocraftapi.message.locale.Locale;
import com.github.hexocraftapi.message.predifined.MessageColor;
import com.github.hexocraftapi.message.predifined.message.ErrorMessage;
import com.github.hexocraftapi.message.predifined.message.WarnPermissionMessage;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * This file is part of HexocubeItems
 */
public abstract class Command<PluginClass extends JavaPlugin> extends org.bukkit.command.Command implements PluginIdentifiableCommand
{
	/**
	 * The plugin that created this object.
	 */
	protected PluginClass plugin;
	/**
	 * @return The logger used by the plugin
	 */
	public PluginClass getPlugin()
	{
		return plugin;
	}


	/**
	 * The parent command
	 */
	private Command<?> parentCommand;

	/**
	 * List of sub commands
	 */
	private final Map<String,Command<?>> subCommands = new LinkedHashMap<String,Command<?>>();

	/**
	 * List of arguments used for the command
	 */
	private final List<CommandArgument<?>> arguments = new ArrayList<CommandArgument<?>>();

	/**
	 * Indicate that the last argument is an instance of {@link Collection}
	 */
	private boolean hasCollection = false;



	/**
	 * @param name Name of the command.
	 * @param plugin The plugin that this listener belongs to.
	 */
	public Command(String name, PluginClass plugin)
	{
		super(name);
		this.plugin = plugin;
	}

	/**
	 * @param name Name of the command.
	 * @param description Description of the command.
	 * @param usageMessage Usage help.
	 * @param aliases Possible aliases.
	 * @param plugin The plugin that this listener belongs to.
	 */
	public Command(String name, String description, String usageMessage, List<String> aliases, PluginClass plugin)
	{
		super(name, description, usageMessage, aliases);
		this.plugin = plugin;
	}



	/**
	 * Define the parent of the command
	 *
	 * @param parentCommand The parent command
	 */
	protected void setParentCommand(Command<?> parentCommand)
	{
		this.parentCommand = parentCommand;
	}

	/**
	 * @return Parent command
	 */
	public Command<?> getParentCommand()
	{
		return parentCommand;
	}

	/**
	 * @return Main command
	 */
	public Command<?> getMainCommand()
	{
		Command<?> main = getParentCommand();

		while(main!=null && main.getParentCommand()!=null)
			main = main.getParentCommand();

		return main;
	}

	/**
	 * @param subCommand Sub command to add to the actual command
	 *
	 * @return the added sub command
	 */
	public Command<?> addSubCommand(Command<?> subCommand)
	{
		subCommand.setParentCommand(this);
		this.subCommands.put(subCommand.getName(), subCommand);
		return this;
	}

	/**
	 * @param subCommandName Name of the sub command
	 *
	 * @return A sub command by its name
	 */
	private Command<?> getSubCommand(String subCommandName)
	{
		for(Map.Entry<String,Command<?>> entry : this.subCommands.entrySet())
		{
			String commandName = entry.getKey();
			Command<?> command = entry.getValue();

			if(commandName.toLowerCase().equals(subCommandName.toLowerCase()))
				return command;

			for(String alias : command.getAliases())
			{
				if(alias.equals(subCommandName))
					return command;
			}
		}
		return null;
	}

	/**
	 * @return List of all sub commands
	 */
	public Map<String,Command<?>> getSubCommands()
	{
		return this.subCommands;
	}

	/**
	 * Add an @{link CommandArgument} to the command
	 *
	 * @param argument Argument to add
	 *
	 * @return The command
	 */
	public Command<?> addArgument(CommandArgument<?> argument)
	{
		// Can't add a mandatory argument after an optional argument
		if(	(argument.isMandatory() || argument.isMandatoryForConsole())
			&& this.arguments.size()>0
			&& this.arguments.get(this.arguments.size()-1).isOptional())
			throw new IllegalArgumentException("You can't add mandatory argument after an optional argument.");

		// Can't add an argument after an argument implement a collection
		if(	this.arguments.size()>0
			&& this.arguments.get(this.arguments.size()-1).isCollection())
			throw new IllegalArgumentException("You can't add argument after a hasCollection.");

		// Add the argument to the list
		this.arguments.add(argument);

		// Check if this arg is a collection
		this.hasCollection = argument.isCollection();

		return this;
	}

	/**
	 * remove an @{link CommandArgument} to the command
	 *
	 * @param name Argument to remove
	 *
	 * @return The command
	 */
	public Command<?> removeArgument(String name)
	{
		for(CommandArgument<?> argument : this.arguments)
		{
			if(argument.getName().toLowerCase().equals(name.toLowerCase()))
			{
				// Remove the argument
				this.arguments.remove(argument);
				// Check if it's a collection
				if(argument.isCollection()) this.hasCollection = false;
				//
				return this;
			}
		}

		return this;
	}

	/**
	 * @param sender Sender
	 *
	 * @return Minimum arguments used by the command
	 */
	public int getMinArgs(CommandSender sender)
	{
		// Count the number of optional arguments
		int minArgs = 0;
		for(CommandArgument arg : arguments)
			minArgs += arg.isMandatory(sender)?1:0;
		return minArgs;
	}

	/**
	 * @return Maximum arguments used by the command
	 */
	public int getMaxArgs()
	{
		return this.hasCollection ? Integer.MAX_VALUE : arguments.size();
	}

	public List<CommandArgument<?>> getArguments()
	{
		return arguments;
	}

	public Sentence getHelp()
	{
		// Full command
		String fullCommand = getName();
		Command<?> parent = getParentCommand();
		while(parent != null)
		{
			fullCommand = parent.getName() + " " + fullCommand;
			parent = parent.getParentCommand();
		}
		fullCommand = "/" + fullCommand;

		// Hover text
		MessageBuilder fullCommandHoverText = new MessageBuilder("");
		// Command
		fullCommandHoverText.append(Locale.command_command + " : ").color(MessageColor.INFO.color()).append(fullCommand).color(MessageColor.COMMAND.color());
		// Aliases
		if(getAliases()!=null && getAliases().isEmpty()==false)
		{
			String aliases[] = getAliases().toArray(new String[0]);

			fullCommandHoverText.append("\n").append(Locale.command_aliases + " : ").color(MessageColor.INFO.color()).append(aliases[0]).color(MessageColor.COMMAND.color());
			for(int i=1; i<aliases.length; i++)
				fullCommandHoverText.append(", ").append(aliases[i]).color(MessageColor.COMMAND.color());
		}
		// Description
		if(getDescription()!=null && getDescription().isEmpty()==false)
		{
			String descriptions[] = getDescription().split("\\r?\\n");

			fullCommandHoverText.append("\n").append(Locale.command_description + " : ").color(MessageColor.INFO.color()).append(descriptions[0]).color(MessageColor.DESCRIPTION.color());
			for(int i=1; i<descriptions.length; i++)
				fullCommandHoverText.append("\n").append(descriptions[i]).color(MessageColor.DESCRIPTION.color());
		}

		fullCommandHoverText.append("\n");
		fullCommandHoverText.append("\n").append(Locale.command_click_copy_command).color(MessageColor.ERROR.color()).bold(true);

		ClickEvent fullCommandClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,fullCommand);
		HoverEvent fullCommandHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,fullCommandHoverText.create());

		// full command sentence
		return new Sentence(fullCommand).color(MessageColor.COMMAND.color()).event(fullCommandClickEvent).event(fullCommandHoverEvent);
	}

	/* Internal use */
	private String getStringArg(int index, String[] args)
	{
		return (args.length>index)?args[index]:null;
	}

	private String getStringListArg(int index, String[] args)
	{
		return StringUtils.join(args," ",index, args.length);
	}

	/**
	 * Executes the given command, returning its success
	 * Override this method in your command
	 *
	 * @param commandInfo Info about the command
	 *
	 * @return true if a valid command, otherwise false
	 */
	public boolean onCommand(CommandInfo commandInfo) { return false; }

	/**
	 * Requests a list of possible completions for a command argument.
	 * Override this method in your command
	 *
	 * @param commandInfo Info about the command
	 *
	 * @return A List of possible completions for the final argument, or null
	 * to default to the command executor
	 */
	public List<String> onTabComplete(CommandInfo commandInfo)
	{
		List<String> completions = Lists.newArrayList();

		if(commandInfo.numArgs() == 0)
		{
			for(Map.Entry<String,Command<?>> entry : this.subCommands.entrySet())
			{
				String commandName = entry.getKey();
				completions.add(commandName);
			}

			if(this.arguments.size()>0)
				completions = this.arguments.get(0).getType().tabComplete(commandInfo);
		}
		else
		{
			for(Map.Entry<String,Command<?>> entry : this.subCommands.entrySet())
			{
				if(entry.getKey().toLowerCase().startsWith(commandInfo.getLastArg().toLowerCase()))
				{
					String commandName = entry.getKey();
					completions.add(commandName);
				}
			}

			if(this.arguments.size()>=commandInfo.numArgs())
				completions= this.arguments.get(commandInfo.numArgs() - 1).getType().tabComplete(commandInfo);
		}

		if(completions != null && completions.size() > 0)
			Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);

		return completions;
	}

	/**
	 * Called when sender does not have necessary permissions.
	 * Override this method in your command
	 *
	 * @param sender Source object which is executing this command
	 */
	public void onPermissionRefused(CommandSender sender)
	{
		if(this.getPermissionMessage()!=null && this.getPermissionMessage().isEmpty()==false)
		{
			for (String line : this.getPermissionMessage().replace("<permission>", this.getPermission()).split("\n")) {
				new ErrorMessage(line).send(sender);
			}
		}
		else
			new WarnPermissionMessage().send(sender);
	}

	public void onCommandHelp(CommandErrorType error, CommandInfo commandInfo)
	{
		boolean isMainCommand = this.parentCommand == null;
		boolean isSubCommand = this.parentCommand != null;

		// Display help for the main command
		if(isMainCommand==true)
		{
			// Send Usage Message
			new MessageHelp(error, commandInfo).send(commandInfo.getSenders());
		}

		// Display help of sub command
		else if(isSubCommand==true)
		{
			// Send Usage Message
			new MessageHelp(error, commandInfo).send(commandInfo.getSenders());
		}
	}

	private String[] reArgs(String[] args)
	{
		List<String> newArgs = new ArrayList<>();

		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];

			if(arg.startsWith("\""))
			{
				String tempArg = arg.substring(1);
				int j;

				for(j = i + 1; j < args.length; j++)
				{
					arg = args[j];
					if(arg.endsWith("\""))
					{
						tempArg += " ";
						tempArg += arg.substring(0, arg.length() - 1);
						break;
					}
					else
					{
						tempArg += " ";
						tempArg += arg;
					}
				}

				newArgs.add(tempArg);
				i = j;
			}
			else if(arg.startsWith("'"))
			{
				String tempArg = arg.substring(1);
				int j;

				for(j = i + 1; j < args.length; j++)
				{
					arg = args[j];
					if(arg.endsWith("'"))
					{
						tempArg += " ";
						tempArg += arg.substring(0, arg.length() - 1);
						break;
					}
					else
					{
						tempArg += " ";
						tempArg += arg;
					}
				}

				newArgs.add(tempArg);
				i = j;
			}
			else
				newArgs.add(arg);
		}

		return newArgs.toArray(new String[newArgs.size()]);
	}

	/**
	 * Executes the command, returning its success
	 *
	 * @param sender       Source object which is executing this command
	 * @param commandLabel The alias of the command used
	 * @param args         All arguments passed to the command, split via ' '
	 *
	 * @return true if the command was successful, otherwise false
	 */
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args)
	{
		// Reorganise args when detecting string " or '
		args  = reArgs(args);

		boolean success = false;
		int minArgs = getMinArgs(sender);

		if(!this.plugin.isEnabled())
			return false;

		// Remove unnecessary arg
		if(args.length>0 && args[0].equals(""))
			args = Arrays.copyOfRange(args, 1, args.length);

		// Not enough parameters for the command
		if(args.length == 0 && minArgs > 0)
		{
			// Check permissions
			if(!testPermissionSilent(sender))
			{
				this.onPermissionRefused(sender);
				return false;
			}

			// Help command
			this.onCommandHelp(CommandErrorType.NOT_ENOUGH_ARGUMENTS, new CommandInfo(sender, this, commandLabel, args, null));
			return false;
		}
		// Main command call
		else if(args.length == 0 && minArgs == 0)
		{
			// Check permissions
			if(!testPermissionSilent(sender))
			{
				this.onPermissionRefused(sender);
				return false;
			}

			// Get mandatory arguments with default values:
			Map<String,String> namedArgs = new LinkedHashMap<String,String>();
			// Loop through attended args
			int index = 0;
			for(CommandArgument<?> argument : this.arguments)
			{
				String argName = argument.getName();

				if(argument.isMandatory() && argument.hasDefaultValue())
					namedArgs.put(argName, argument.getDefaultValue().toString());
			}


			// Execute the command
			try
			{
				success = this.onCommand(new CommandInfo(sender, this, commandLabel, args, namedArgs));
			}
			catch(Throwable ex)
			{
				throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.plugin.getDescription().getFullName(), ex);
			}
		}
		// With multiple args it could be a SubCommand or the main command
		else if(args.length > 0)
		{
			// Sub or Base ???
			// First we check if the first arg correspond to a Sub command
			String firstArg = args[0].toLowerCase();
			// Check if a sub command exist for this arg
			Command<?> subCommand = getSubCommand(firstArg);
			// If yes, this a sub command
			if(subCommand != null)
			{
				String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
				return subCommand.execute(sender,firstArg, subArgs);
			}
			// Else, it could be the command with args
			else
			{
				// Check permissions
				if(!testPermissionSilent(sender))
				{
					this.onPermissionRefused(sender);
					return false;
				}

				// Check that the numbers of arguments correspond
				// if not, show the help command
				if(args.length < getMinArgs(sender))
				{
					this.onCommandHelp(CommandErrorType.NOT_ENOUGH_ARGUMENTS, new CommandInfo(sender, this, commandLabel, args, null));
					return false;
				}
				if(args.length > (getMaxArgs() == -1 ? args.length : getMaxArgs()))
				{
					this.onCommandHelp(CommandErrorType.TOO_MANY_ARGUMENTS, new CommandInfo(sender, this, commandLabel, args, null));
					return false;
				}

				// Now that the number of arguments correspond, we need to check the validity of each args

				// Don't forget the rule :
				// 		You can't add a mandatory argument after an optional argument
				Map<String,String> namedArgs = new LinkedHashMap<String,String>();

				// Loop through attended args
				int index = 0;
				for(CommandArgument<?> argument : this.arguments)
				{
					String argName = argument.getName();
					String value = argument.isCollection() ? getStringListArg(index, args) : getStringArg(index, args);

					// If mandatory, the argument MUST correspond
					if(argument.isMandatory(sender))
					{
						// Check the string
						if(argument.getType().check(value))
						{
							namedArgs.put(argName, argument.getType().get(value).toString());

							index++;
							continue;
						}
						else
						{
							this.onCommandHelp(CommandErrorType.MISMATCH_ARGUMENTS, new CommandInfo(sender, this, commandLabel, args, null));
							return false;
						}
					}

					// Else, here begin the problems
					else if(argument.isOptional(sender))
					{
						// Check the string
						if(argument.getType().check(value))
						{
							namedArgs.put(argName, value);

							index++;
							continue;
						}
						else if(argument.hasDefaultValue() && argument.getType().check(argument.getDefaultValue().toString()))
						{
							namedArgs.put(argName, argument.getDefaultValue().toString());

							continue;
						}
					}
				}

				// We have reach the ends of possible arguments but some args are still in the queue
				if(index < args.length && this.hasCollection == false)
				{
					this.onCommandHelp(CommandErrorType.MISMATCH_ARGUMENTS, new CommandInfo(sender, this, commandLabel, args, null));
					return false;
				}

				// Finally, execute the command
				try
				{
					success = this.onCommand(new CommandInfo(sender, this, commandLabel, args, namedArgs));
				}
				catch(Throwable ex)
				{
					throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.plugin.getDescription().getFullName(), ex);
				}
			}
		}

		return success;
	}

    /**
	 * {@inheritDoc}
	 *
	 * Delegates to the tab completer if present.
	 *
	 * If it is not present or returns null, will delegate to the current
	 * command executor if it implements {@link TabCompleter}. If a non-null
	 * list has not been found, will default to standard player name
	 * completion in {@link
	 * org.bukkit.command.Command#tabComplete(CommandSender, String, String[])}.
	 *
	 * This method does not consider permissions.
	 *
	 * @throws CommandException         if the completer or executor throw an
	 *                                  exception during the process of tab-completing.
	 * @throws IllegalArgumentException if sender, alias, or args is null
	 */
	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args)
	throws CommandException, IllegalArgumentException
	{
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(args, "Arguments cannot be null");
		Validate.notNull(alias, "Alias cannot be null");

		//
		if(args.length > 0 && args[0].equals(""))
			args = Arrays.copyOfRange(args, 1, args.length);

		List<String> completions = null;
		try
		{
			if(args.length>0)
			{
				// Sub or Base ???
				// First we check if the first arg correspond to a Sub command
				String firstArg = args[0].toLowerCase();
				// Check if a sub command exist for this arg
				Command<?> subCommand = getSubCommand(firstArg);
				// If yes, this a sub command
				if(subCommand != null)
				{
					String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
					if(subArgs.length > 0) return subCommand.tabComplete(sender, alias, subArgs);
				}
			}

			completions = this.onTabComplete(new CommandInfo(sender, this, alias, args, null));
		}
		catch(Throwable ex)
		{
			StringBuilder message = new StringBuilder();
			message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
			for(String arg : args)
				message.append(arg).append(' ');
			message.deleteCharAt(message.length() - 1).append("' in plugin ").append(plugin.getDescription().getFullName());
			throw new CommandException(message.toString(), ex);
		}

		return completions;
	}
}