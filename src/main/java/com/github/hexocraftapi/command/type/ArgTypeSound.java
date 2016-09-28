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
import com.github.hexocraftapi.sounds.Sounds;
import org.bukkit.Sound;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class ArgTypeSound implements ArgType<Sound>
{
	private ArgTypeSound() {};
	private static ArgTypeSound t = new ArgTypeSound();
	public static ArgTypeSound get() { return t; }

	@Override
	public boolean check(String sound)
	{
		return get(sound)!=null;
	}

	@Override
	public Sound get(String sound)
	{
		return Sounds.get(sound);
	}

	@Override
	public List<String> tabComplete(CommandInfo commandInfo)
	{
		String lastWord = commandInfo.numArgs() == 0 ? "" : commandInfo.getArgs().get(commandInfo.numArgs()-1);

		ArrayList<String> matchedSounds = new ArrayList<String>();
		for(Sound sound : Sound.values())
		{
			String name = sound.toString();
			if(StringUtil.startsWithIgnoreCase(name, lastWord))
				matchedSounds.add(name);
		}

		return matchedSounds;
	}
}
