package com.github.hexocraftapi.command.message;

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

import com.github.hexocraftapi.command.Command;
import com.github.hexocraftapi.command.CommandArgument;
import com.github.hexocraftapi.command.CommandInfo;
import com.github.hexocraftapi.command.errors.CommandErrorType;
import com.github.hexocraftapi.message.Line;
import com.github.hexocraftapi.message.Message;
import com.github.hexocraftapi.message.Sentence;
import com.github.hexocraftapi.message.locale.Locale;
import com.github.hexocraftapi.message.predifined.MessageColor;

/**
 * This messgae is used to format command message send to user for help
 *
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class MessageHelp extends Message
{
	public MessageHelp(CommandInfo commandInfo)
	{
		this(null, commandInfo);
	}

	public MessageHelp(CommandErrorType error, CommandInfo commandInfo)
	{
		super();

		Command<?> command = commandInfo.getCommand();

		// Not enough parameters for the command
		if(error != null)
		{
			if(error == CommandErrorType.NOT_ENOUGH_ARGUMENTS)
			{
				add(new Line(""));
				add(new Line(new Sentence(Locale.command_not_enough_parameters).color(MessageColor.ERROR.color())));
			}
			else if(error == CommandErrorType.TOO_MANY_ARGUMENTS)
			{
				this.add(new Line(""));
				this.add(new Line(new Sentence(Locale.command_too_many_parameters).color(MessageColor.ERROR.color())));
			}
			else if(error == CommandErrorType.MISMATCH_ARGUMENTS)
			{
				this.add(new Line(""));
				this.add(new Line(new Sentence(Locale.command_error).color(MessageColor.ERROR.color())));
				this.add(new Line(new Sentence(Locale.command_use_help).color(MessageColor.WARNING.color())));
			}
		}

		// Lines of the message
		Line commandLine = new Line();
		commandLine.add(new Sentence(Character.toString('\u00BB') + " ").color(MessageColor.COMMAND.color()));
		// - Full command
		commandLine.add(command.getHelp());
		// - Arguments
		for(CommandArgument<?> argument : command.getArguments())
			commandLine.add(argument.getHelp());
		//
		this.add(commandLine);

		// - Description
		// The short text is the first line of the description
		if(command.getDescription()!=null && command.getDescription().isEmpty()==false)
		{
			// Lines of the message
			Line DescriptionLine = new Line();

			String descriptions[] = command.getDescription().split("\\r?\\n");
			DescriptionLine.add(new Sentence(" " + descriptions[0]).color(MessageColor.DESCRIPTION.color()));
			//
			this.add(DescriptionLine);
		}
	}
}
