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

import java.util.List;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class ArgTypeDouble implements ArgType<Double>
{
	private ArgTypeDouble() {};
	private static ArgTypeDouble t = new ArgTypeDouble();
	public static ArgTypeDouble get() { return t; }

	@Override
	public boolean check(String dbl)
	{
		return get(dbl)!=null;
	}

	@Override
	public Double get(String dbl)
	{
		try
		{
			return Double.parseDouble(dbl);
		}
		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public List<String> tabComplete(CommandInfo commandInfo)
	{
		return null;
	}
}
