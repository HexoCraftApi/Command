package com.github.hexocraftapi.command;

/*
 * Copyright 2015 hexosse
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import com.github.hexocraftapi.chat.MessageBuilder;
import com.github.hexocraftapi.chat.event.HoverEvent;
import com.github.hexocraftapi.command.type.ArgType;
import com.github.hexocraftapi.command.type.ArgTypeStringList;
import com.github.hexocraftapi.message.Sentence;
import com.github.hexocraftapi.message.locale.Locale;
import com.github.hexocraftapi.message.predifined.MessageColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Collection;

/**
 * This class describe a command argument
 *
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public class CommandArgument<T>
{
	private String name;
	private ArgType<T> type;
	private T defaultValue = null;
	boolean mandatory = true;
	boolean mandatoryForConsole = true;
	String description;

	public CommandArgument(String name, ArgType<T> type, boolean mandatory)
	{
		this(name, type, null, mandatory, mandatory, null);
	}

	public CommandArgument(String name, ArgType<T> type, boolean mandatory, boolean mandatoryForConsole)
	{
		this(name, type, null, mandatory, mandatoryForConsole, null);
	}

	public CommandArgument(String name, ArgType<T> type, boolean mandatory, boolean mandatoryForConsole, String description)
	{
		this(name, type, null, mandatory, mandatoryForConsole, description);
	}

	public CommandArgument(String name, ArgType<T> type, T defaultValue, boolean mandatory)
	{
		this(name, type, defaultValue, mandatory, mandatory, null);
	}

	public CommandArgument(String name, ArgType<T> type, T defaultValue, boolean mandatory, boolean mandatoryForConsole)
	{
		this(name, type, defaultValue, mandatory, mandatoryForConsole, null);
	}

	public CommandArgument(String name, ArgType<T> type, T defaultValue, boolean mandatory, boolean mandatoryForConsole, String description)
	{
		// Null checks
		if (name == null) throw new IllegalArgumentException("name must be different from null");

		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.mandatory = mandatory;
		this.mandatoryForConsole = mandatoryForConsole;
		this.description = description;
	}

	/**
	 * @return The name of the argument
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return The type of the argument
	 */
	public ArgType<T> getType()
	{
		return this.type;
	}

	/**
	 * @return The default value of the argument
	 */
	public T getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * @return true if a default value exist
	 */
	public boolean hasDefaultValue()
	{
		return defaultValue != null;
	}

	/**
	 * @return true if the argument is mandatory (required)
	 */
	public boolean isMandatory()
	{
		return mandatory;
	}

	/**
	 * @return true if the argument is mandatory for the console (required)
	 */
	public boolean isMandatoryForConsole()
	{
		return mandatoryForConsole;
	}

	/**
	 * @return true if the argument is mandatory for the specified sender
	 */
	public boolean isMandatory(CommandSender sender)
	{
		// A mandatory argument with a default value is considered
		// as optional
		if(defaultValue!= null)
			return false;

		if(sender instanceof ConsoleCommandSender)
			return isMandatoryForConsole();
		else
			return isMandatory();
	}

	/**
	 * @return true if the argument is optionnal (not mandatory)
	 */
	public boolean isOptional()
	{
		return !this.isMandatory();
	}

	/**
	 * @return true if the argument is optionnal (not mandatory) for the specified sender
	 */
	public boolean isOptional(CommandSender sender) { return !this.isMandatory(sender); }

	/**
	 * @return true if the argument is an instance of {@link Collection}
	 */
	public boolean isCollection()
	{
		// Only ArgTypeStringList class is supported
		return this.type instanceof ArgTypeStringList;
	}

	/**
	 * @return The description of the argument
	 */
	public String getDescription()
	{
		return description;
	}

	public String getTemplate(CommandArgument<?> argument)
	{
		return argument.isMandatory() ? ("<" + argument.getName() + ">") : ("[" + argument.getName() + "]");
	}

	public Sentence getHelp()
	{
		// Hover text
		MessageBuilder argumentHoverText = new MessageBuilder("");
		// Argument
		argumentHoverText.append(Locale.argument_argument + " : ").color(MessageColor.INFO.color()).append(getName()).color(isMandatory()? MessageColor.MANDATORY_ARGUMENT.color():MessageColor.OPTIONAL_ARGUMENT.color());
		// Description
		if(getDescription()!=null && getDescription().isEmpty()==false)
			argumentHoverText.append("\n").append(Locale.argument_description + " : ").color(MessageColor.INFO.color()).append(getDescription()).color(MessageColor.DESCRIPTION.color());
		// Mandatory
		if(isMandatory())
			argumentHoverText.append("\n").append(Locale.argument_mandatory).color(MessageColor.ERROR.color());

		// Hover event
		HoverEvent argumentHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,argumentHoverText.create());
		// Add full command to the mine
		return new Sentence(" " + getTemplate(this)).color(isMandatory()?MessageColor.MANDATORY_ARGUMENT.color():MessageColor.OPTIONAL_ARGUMENT.color()).event(argumentHoverEvent);
	}
}
