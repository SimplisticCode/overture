/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.vdm2slang;

import java.util.LinkedList;
import java.util.List;

public class SlangSettings
{
	private boolean disableCloning;
	private List<String> modulesToSkip;
	private String vdmEntryExp;
	private boolean genRecsAsInnerClasses;
	private boolean formatCode;
	private boolean makeClassesSerializable;
	private boolean genSystemClass;
	private boolean printVdmLocations;

	public SlangSettings()
	{
		this.disableCloning = false;
		this.modulesToSkip = new LinkedList<String>();
		this.vdmEntryExp = null;
		this.genRecsAsInnerClasses = true;
		this.formatCode = true;
		this.makeClassesSerializable = false;
		this.printVdmLocations = false;
	}

	public List<String> getModulesToSkip()
	{
		return modulesToSkip;
	}

	public void setModulesToSkip(List<String> modulesToSkip)
	{
		if (modulesToSkip != null)
		{
			this.modulesToSkip = modulesToSkip;
		}
	}

	public boolean getDisableCloning()
	{
		return disableCloning;
	}

	public void setDisableCloning(boolean disableCloning)
	{
		this.disableCloning = disableCloning;
	}

	public String getVdmEntryExp()
	{
		return vdmEntryExp;
	}

	public void setVdmEntryExp(String vdmLaunchConfigEntryExp)
	{
		this.vdmEntryExp = vdmLaunchConfigEntryExp;
	}

	public boolean genRecsAsInnerClasses()
	{
		return genRecsAsInnerClasses;
	}

	public void setGenRecsAsInnerClasses(boolean genRecsAsInnerClasses)
	{
		this.genRecsAsInnerClasses = genRecsAsInnerClasses;
	}

	public boolean formatCode()
	{
		return formatCode;
	}

	public void setFormatCode(boolean formatCode)
	{
		this.formatCode = formatCode;
	}

	public boolean makeClassesSerializable()
	{
		return makeClassesSerializable;
	}

	public void setMakeClassesSerializable(boolean makeClassesSerializable)
	{
		this.makeClassesSerializable = makeClassesSerializable;
	}


	public boolean genSystemClass()
	{
		return genSystemClass;
	}

	public void setGenSystemClass(boolean genSystemClass)
	{
		this.genSystemClass = genSystemClass;
	}

	public boolean printVdmLocations()
	{
		return printVdmLocations;
	}

	public void setPrintVdmLocations(boolean printVdmSource)
	{
		this.printVdmLocations = printVdmSource;
	}
}
