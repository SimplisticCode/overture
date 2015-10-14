package org.overture.codegen.vdm2jml.predgen.info;

import java.util.List;

public abstract class AbstractTypeInfo
{
	protected boolean optional;

	public AbstractTypeInfo(boolean optional)
	{
		this.optional = optional;
	}

	abstract public boolean allowsNull();
	
	abstract public List<LeafTypeInfo> getLeafTypesRecursively();

	abstract public String consCheckExp(String enclosingClass, String javaRootPackage, String arg);
	
	public String consIsNullCheck(String arg)
	{
		return "(" + arg + " == null)";
	}
}