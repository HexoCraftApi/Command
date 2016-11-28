package com.github.hexocraftapi.command.predifined;

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

import com.github.hexocraftapi.chat.Chat;
import com.github.hexocraftapi.chat.MessageBuilder;
import com.github.hexocraftapi.chat.event.ClickEvent;
import com.github.hexocraftapi.chat.event.HoverEvent;
import com.github.hexocraftapi.command.Command;
import com.github.hexocraftapi.command.CommandArgument;
import com.github.hexocraftapi.command.CommandInfo;
import com.github.hexocraftapi.command.errors.CommandErrorType;
import com.github.hexocraftapi.command.message.MessageHelp;
import com.github.hexocraftapi.command.type.ArgTypeInteger;
import com.github.hexocraftapi.message.Line;
import com.github.hexocraftapi.message.Sentence;
import com.github.hexocraftapi.message.locale.Locale;
import com.github.hexocraftapi.message.predifined.MessageColor;
import com.github.hexocraftapi.message.predifined.line.Title;
import com.github.hexocraftapi.message.predifined.message.EmptyMessage;
import com.github.hexocraftapi.message.predifined.message.TitleMessage;
import com.google.common.collect.Lists;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class CommandHelp<PluginClass extends JavaPlugin> extends Command<PluginClass>
{
	static String HELP = Locale.command_help;
	static String PAGE = Locale.command_page;

	private boolean displayArguments = true;
	private boolean displayDescription = true;
	private boolean displayInlineDescription = false;

	public CommandHelp(PluginClass plugin)
	{
		super("help", plugin);
		this.setAliases(Lists.newArrayList(HELP, "help", "h", "?"));
		this.addArgument(new CommandArgument<Integer>(PAGE, ArgTypeInteger.get(), 1, false, false, Locale.help_page_number));
	}

	public void setDisplayArguments(boolean displayArguments) { this.displayArguments = displayArguments; }
	public void setDisplayDescription(boolean displayDescription) { this.displayDescription = displayDescription; }
	public void setDisplayInlineDescription(boolean displayInlineDescription) { this.displayInlineDescription = displayInlineDescription; }

	/**
	 * Executes the given command, returning its success
	 *
	 * @param commandInfo Info about the command
	 *
	 * @return true if a valid command, otherwise false
	 */
	@Override
	public boolean onCommand(CommandInfo commandInfo)
	{
		//List<Data> datas = Lists.newArrayList();
		HelpLines helpLines = new HelpLines(Chat.CHAT_PAGE_HEIGHT-1);

		// Command
		Command<?> command = commandInfo.getCommand();

		// Parent command
		Command<?> parentCommand = command.getParentCommand();

		// Main command
		Command<?> mainCommand = command.getName().toLowerCase().equals("help") ? parentCommand : command;
		if(mainCommand.getMaxArgs() > 0)
		{
			CommandInfo mainCommandInfo = new CommandInfo(commandInfo.getSender(), mainCommand, mainCommand.getName(), new String[0], null);
			MessageHelp mainHelp = new MessageHelp(mainCommandInfo);
			mainHelp.setDisplayArguments(this.displayArguments);
			mainHelp.setDisplayDescription(this.displayDescription);
			mainHelp.setDisplayInlineDescription(this.displayInlineDescription);

			if(command.getPermission()==null)
				helpLines.add(new HelpLine(mainCommandInfo, mainHelp));
			else if(command.getPermission()!=null && command.getPermission().isEmpty()==false && commandInfo.getSender().hasPermission(command.getPermission())==true)
				helpLines.add(new HelpLine(mainCommandInfo, mainHelp));
		}

		// Sub command
		for(Map.Entry<String,Command<?>> entry : mainCommand.getSubCommands().entrySet())
		{
			Command<?> subCommand = entry.getValue();

			CommandInfo subCommandInfo = new CommandInfo(commandInfo.getSender(), subCommand, subCommand.getName(), new String[0], null);
			MessageHelp subHelp = new MessageHelp(subCommandInfo);
			subHelp.setDisplayArguments(this.displayArguments);
			subHelp.setDisplayDescription(this.displayDescription);
			subHelp.setDisplayInlineDescription(this.displayInlineDescription);

			if(subCommandInfo.getCommand().getPermission()==null || subCommandInfo.getCommand().getPermission().isEmpty()==true)
				helpLines.add(new HelpLine(subCommandInfo, subHelp));
			else if(subCommandInfo.getCommand().getPermission().isEmpty()==false && commandInfo.getSender().hasPermission(subCommandInfo.getCommand().getPermission())==true)
				helpLines.add(new HelpLine(subCommandInfo, subHelp));
		}

		// Page requested
		int page = Integer.parseInt(commandInfo.hasNamedArg(PAGE)==true?commandInfo.getNamedArg(PAGE):"1");
		int maxPages = helpLines.currentPage;

		// Check page number
		page = ((page>=maxPages) ? maxPages : ((page<=0) ? 1 : page));

		// Title line
		Sentence prev = getPrev(command, page - 1, maxPages);
		Sentence help = new Sentence(Locale.help_for_command + " \"" + parentCommand.getName() + "\"").color(MessageColor.DESCRIPTION.color());
		Sentence index = getIndex(command, page, maxPages);
		Sentence next = getNext(command, page + 1, maxPages);
		Title title = new Title('-', MessageColor.COMMAND.color(), prev, help, index, next );

		EmptyMessage.toSenders(commandInfo.getSenders());
		TitleMessage.toSenders(commandInfo.getSenders(), title);

		// Help lines
		for(HelpLine line : helpLines.lines)
		{
			if(line.page==page)
				line.message.send(line.commandInfo.getSenders());
		}

		return true;
	}

	@Override
	public void onCommandHelp(CommandErrorType error, CommandInfo commandInfo)
	{
	}

	private Sentence getPrev(Command<?> command, int pageNumber, int totalPage)
	{
		if(totalPage <= 1 || pageNumber <= 0)
			return new Sentence("");

		Sentence prev = new Sentence(" [<] ");

		// Command
		String helpCommand = "";
		while(command.getParentCommand()!=null)
		{
			command = command.getParentCommand();
			helpCommand = command.getName() + " " + helpCommand;
		}
		helpCommand = "/" + helpCommand + " " + HELP + " " + Integer.toString(pageNumber);

		MessageBuilder prevHoverText = new MessageBuilder("");
		prevHoverText.append(Locale.help_page + " " + Integer.toString(pageNumber)).color(MessageColor.SUBCOMMAND.color());
		ClickEvent prevClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, helpCommand);
		HoverEvent prevHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, prevHoverText.create());

		return prev.color(MessageColor.COMMAND.color()).event(prevClickEvent).event(prevHoverEvent);
	}

	private Sentence getNext(Command<?> command, int pageNumber, int totalPage)
	{
		if(totalPage <= 1 || pageNumber <= 0 || pageNumber > totalPage)
			return new Sentence("");

		Sentence next = new Sentence(" [>] ");

		// Command
		String helpCommand = "";
		while(command.getParentCommand()!=null)
		{
			command = command.getParentCommand();
			helpCommand = command.getName() + " " + helpCommand;
		}
		helpCommand = "/" + helpCommand + " " + HELP + " " + Integer.toString(pageNumber);


		MessageBuilder nextHoverText = new MessageBuilder("");
		nextHoverText.append(Locale.help_page + " " + Integer.toString(pageNumber)).color(MessageColor.SUBCOMMAND.color());
		ClickEvent nextClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, helpCommand);
		HoverEvent nextHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, nextHoverText.create());

		return next.color(MessageColor.COMMAND.color()).event(nextClickEvent).event(nextHoverEvent);
	}

	private Sentence getIndex(Command<?> command, int pageNumber, int totalPage)
	{
		if(totalPage <= 1 || pageNumber <= 0 || pageNumber > totalPage)
			return new Sentence("");

		Sentence index = new Sentence(" (" + Integer.toString(pageNumber) + "/" + Integer.toString(totalPage) + ") ");

		return index.color(MessageColor.DESCRIPTION.color());
	}


	class HelpLine
	{
		public CommandInfo commandInfo;
		public MessageHelp message;
		public int lines;
		public int page;

		public HelpLine(CommandInfo commandInfo, MessageHelp message)
		{
			this.commandInfo = commandInfo;
			this.message = message.build();
			this.lines = getLines(commandInfo, message);
		}

		private int getLines(CommandInfo commandInfo, MessageHelp message)
		{
			if(commandInfo.getPlayer()!=null)
			{
				int nbLines = 0;
				for(Line line : message.getLines())
					nbLines += Chat.wordWrap(line.toLegacyText(), Chat.NO_WRAP_CHAT_PAGE_WIDTH).length;
				return nbLines;
			}
			else
				return message.getLines().size();
		}
	}

	class HelpLines
	{
		List<HelpLine> lines = new ArrayList<>();
		int nbLinePerPage;
		int currentPage;
		int currentPageLines;

		public HelpLines(int nbLinePerPage)
		{
			this.nbLinePerPage = nbLinePerPage;
			this.currentPage = 1;
			this.currentPageLines = 0;
		}

		public void add(HelpLine line)
		{
			// Check if the number of line feet in the current page
			if(this.currentPageLines + line.lines > this.nbLinePerPage)
			{
				this.currentPage++;
				this.currentPageLines = 0;
			}

			// Update the line. It will know it page number
			this.currentPageLines += line.lines;
			line.page = this.currentPage;

			// Add fontLine to the list
			this.lines.add(line);
		}
	}
}
