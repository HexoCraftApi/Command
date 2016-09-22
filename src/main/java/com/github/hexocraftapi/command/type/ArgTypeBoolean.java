package com.github.hexocraftapi.command.type;

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

import com.github.hexocraftapi.command.CommandInfo;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class ArgTypeBoolean implements ArgType<Boolean>
{
	private ArgTypeBoolean() {};
	private static ArgTypeBoolean t = new ArgTypeBoolean();
	public static ArgTypeBoolean get() { return t; }

	private static Set<String> trueSet = new HashSet<String>(Arrays.asList("1", "true", "t", "yes", "y"));
	private static Set<String> falseSet = new HashSet<String>(Arrays.asList("0", "false", "f", "no", "n"));


	@Override
	public boolean check(String bool)
	{
		return get(bool)!=null;
	}

	@Override
	public Boolean get(String bool)
	{
		if (trueSet.contains(bool.toLowerCase()))
			return true;
		if (falseSet.contains(bool.toLowerCase()))
			return false;
		return false;
	}

	@Override
	public List<String> tabComplete(CommandInfo commandInfo)
	{
		if(commandInfo.numArgs() == 0)
		{
			ArrayList<String> words =  new ArrayList<String>(Arrays.asList("true", "false"));
			return words;
		}
		else
		{
			String lastWord = commandInfo.getArgs().get(commandInfo.numArgs()-1);

			ArrayList<String> matchedWorlds = new ArrayList<String>();
			for(String sTrue : trueSet)
			{
				if(StringUtil.startsWithIgnoreCase(sTrue, lastWord))
					matchedWorlds.add(sTrue);
			}
			for(String sFalse : falseSet)
			{
				if(StringUtil.startsWithIgnoreCase(sFalse, lastWord))
					matchedWorlds.add(sFalse);
			}

			return matchedWorlds;
		}
	}
}
