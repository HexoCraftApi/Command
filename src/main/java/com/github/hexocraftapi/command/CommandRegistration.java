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

import com.github.hexocraftapi.reflection.util.FieldUtil;
import com.github.hexocraftapi.reflection.util.MethodUtil;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class CommandRegistration
{
	public static boolean registerCommand(JavaPlugin plugin, Command command)
	{
		try
		{
			if(isRegisteredCommand(plugin, command))
			{
				if(!unRegisterCommand(plugin, command))
					return false;
			}

			return getCommandMap(plugin.getServer()).register(plugin.getDescription().getName(),command);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public static boolean unRegisterCommand(JavaPlugin plugin, Command command)
	{
		try
		{
			Map<String,Command> knownCommands = getKnownCommands(plugin.getServer());
			knownCommands.remove(command.getName().toLowerCase().trim());
			for(String alias : command.getAliases())
			{
				alias = alias.toLowerCase().trim();
				if(knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(plugin.getName()))
					knownCommands.remove(alias);
			}

			return true;
		}
		catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isRegisteredCommand(JavaPlugin plugin, Command command) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
	{
		return getKnownCommands(plugin.getServer()).get(command.getName().toLowerCase().trim()) != null;
	}

	private static SimpleCommandMap getCommandMap(Server server) throws NoSuchFieldException, IllegalAccessException {
		//Method getCommandMapMethod = MethodUtil.getMethod(server.getClass(), "getCommandMap");
		//return (SimpleCommandMap) getCommandMapMethod.invoke(server);
        return (SimpleCommandMap) FieldUtil.getField(server.getClass(), "commandMap", server);

    }

	private static Map<String, Command> getKnownCommands(Server server) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
	{
        SimpleCommandMap simpleCommandMap = getCommandMap(server);
		return getKnownCommands(simpleCommandMap);
	}

	private static Map<String, Command> getKnownCommands(SimpleCommandMap simpleCommandMap) throws NoSuchFieldException, IllegalAccessException
	{
		return (Map<String, Command>) FieldUtil.getField(SimpleCommandMap.class, "knownCommands", simpleCommandMap);
	}
}
