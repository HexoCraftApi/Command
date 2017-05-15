package com.github.hexocraftapi.command.type;

import com.github.hexocraftapi.command.CommandInfo;
import org.apache.commons.lang.StringUtils;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArgTypeMaterialList implements ArgType<List<MaterialData>>
{
	private ArgTypeMaterialList() {};
	private static ArgTypeMaterialList t = new ArgTypeMaterialList();
	public static ArgTypeMaterialList get() { return t; }

	@Override
	public boolean check(String materialDataList)
	{
		return get(materialDataList)!=null;
	}

	@Override
	public List<MaterialData> get(String materialDataList)
	{
		List<String> sl = Arrays.asList(StringUtils.split(materialDataList, ","));
		List<MaterialData> ml = new ArrayList<>(sl.size());

		for(String md : sl)
			ml.add(ArgTypeMaterial.get().get(md));

		return ml;
	}

	@Override
	public List<String> tabComplete(CommandInfo commandInfo)
	{
		return null;
	}
}
