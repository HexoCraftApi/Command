package com.github.hexocraftapi.command.type;

import com.github.hexocraftapi.command.CommandInfo;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ArgTypeMaterial implements ArgType<MaterialData>
{
	private ArgTypeMaterial() {};
	private static ArgTypeMaterial t = new ArgTypeMaterial();
	public static ArgTypeMaterial get() { return t; }

	@Override
	public boolean check(String materialData)
	{
		return get(materialData)!=null;
	}

	@Override
	public MaterialData get(String materialData)
	{
		String m = materialData.split(":")[0];
		String d = (m.length()==materialData.length())?"0":materialData.split(":")[1];

		Material mat = Material.matchMaterial(m);
		return mat==null?null:mat.getNewData((byte) Integer.parseInt(d));
	}

	@Override
	public List<String> tabComplete(CommandInfo commandInfo)
	{
		String lastWord = commandInfo.numArgs() == 0 ? "" : commandInfo.getArgs().get(commandInfo.numArgs()-1);

		ArrayList<String> matchedMaterial = new ArrayList<String>();
		for(Material material : Material.values())
		{
			String name = material.toString();
			if(StringUtil.startsWithIgnoreCase(name, lastWord))
				matchedMaterial.add(name);
		}

		return matchedMaterial;
	}
}
