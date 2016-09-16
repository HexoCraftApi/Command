package com.github.hexocraftapi.command.type;

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

import com.github.hexocraftapi.command.CommandInfo;
import org.bukkit.block.Biome;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public class ArgTypeBiome implements ArgType<Biome>
{
	private ArgTypeBiome() {};
	private static ArgTypeBiome t = new ArgTypeBiome();
	public static ArgTypeBiome get() { return t; }

	@Override
	public boolean check(String BiomeName)
	{
		return get(BiomeName) != null;
	}

	@Override
	public Biome get(String biomeName)
	{
		for(Biome biome : Biome.values())
		{
			if(biome.toString().equals(biomeName))
				return biome;
		}
		return null;
	}

	@Override
	public List<String> tabComplete(CommandInfo commandInfo)
	{
		String lastWord = commandInfo.numArgs() == 0 ? "" : commandInfo.getArgs().get(commandInfo.numArgs()-1);

		ArrayList<String> matchedBiomes = new ArrayList<String>();
		for(Biome biome : Biome.values())
		{
			String name = biome.toString();
			if(StringUtil.startsWithIgnoreCase(name, lastWord))
				matchedBiomes.add(name);
		}

		return matchedBiomes;
	}
}
