package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.types.AClassType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.NaturalValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.RealValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.VoidValue;
import org.overture.typechecker.assistant.definition.ACpuClassDefinitionAssistantTC;

public class ACpuClassDefinitionAssistantInterpreter extends
		ACpuClassDefinitionAssistantTC
{

	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ACpuClassDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static CPUValue newInstance(ACpuClassDefinition node, Object object,
			ValueList argvals, Context ctxt)
	{
		NameValuePairList nvpl = PDefinitionListAssistantInterpreter.getNamedValues(node.getDefinitions(), ctxt);
		NameValuePairMap map = new NameValuePairMap();
		map.putAll(nvpl);

		RealValue sarg = (RealValue) argvals.get(1);
		if (sarg.value <= 0)
		{
			throw new ContextException(4149, "CPU frequency to slow: "
					+ sarg.value + " Hz", ctxt.location, ctxt);
		} else if (sarg.value > CPU_MAX_FREQUENCY)
		{
			throw new ContextException(4150, "CPU frequency to fast: "
					+ sarg.value + " Hz", ctxt.location, ctxt);
		}

		return new CPUValue((AClassType) node.getClasstype(), map, argvals);
	}

	public static Value deploy(ANotYetSpecifiedStm node, Context ctxt)
	{
		try
		{
			ObjectContext octxt = (ObjectContext) ctxt;
			CPUValue cpu = (CPUValue) octxt.self;
			ObjectValue obj = (ObjectValue) octxt.lookup(varName("obj"));

			redeploy(obj, cpu);
			cpu.deploy(obj);

			return new VoidValue();
		} catch (Exception e)
		{
			throw new ContextException(4136, "Cannot deploy to CPU", ctxt.location, ctxt);
		}
	}

	public static Value setPriority(ANotYetSpecifiedStm node, Context ctxt)
	{
		try
		{
			ObjectContext octxt = (ObjectContext) ctxt;
			CPUValue cpu = (CPUValue) octxt.self;
			SeqValue opname = (SeqValue) octxt.lookup(varName("opname"));
			NaturalValue priority = (NaturalValue) octxt.check(varName("priority"));

			cpu.setPriority(opname.stringValue(ctxt), priority.intValue(ctxt));
			return new VoidValue();
		} catch (Exception e)
		{
			throw new ContextException(4137, "Cannot set priority: "
					+ e.getMessage(), ctxt.location, ctxt);
		}
	}

	/**
	 * Recursively updates all transitive references with the new CPU, but without removing the parent - child relation,
	 * unlike the deploy method.
	 * 
	 * @param the
	 *            objectvalue to update
	 * @param the
	 *            target CPU of the redeploy
	 */
	private static void updateCPUandChildCPUs(ObjectValue obj, CPUValue cpu)
	{
		if (cpu != obj.getCPU())
		{
			for (ObjectValue superObj : obj.superobjects)
			{
				updateCPUandChildCPUs(superObj, cpu);
			}
			obj.setCPU(cpu);
		}

		// update all object we have created our self.
		for (ObjectValue objVal : obj.children)
		{
			updateCPUandChildCPUs(objVal, cpu);
		}
	}

	/**
	 * Will redeploy the object and all object transitive referenced by this to the supplied cpu. Redeploy means that
	 * the transitive reference to and from our creator is no longer needed, and will be removed. This only applies to
	 * this object, as it is on the top of the hierarchy, all children will be updated with the
	 * <tt>updateCPUandChildrenCPUs</tt> method which recursively updates all transitive references with the new CPU,
	 * but without removing the parent - child relation.
	 * 
	 * @param the
	 *            object value to deploy
	 * @param the
	 *            target CPU of the redeploy
	 */
	private static void redeploy(ObjectValue obj, CPUValue cpu)
	{
		updateCPUandChildCPUs(obj, cpu);

		// if we are moving to a new CPU, we are no longer a part of the transitive
		// references from our creator, so let us remove ourself. This will prevent
		// us from being updated if our creator is migrating in the
		// future.
		obj.removeCreator();
	}

	private static LexNameToken varName(String name)
	{
		return new LexNameToken("CPU", name, new LexLocation());
	}
	
	public static Value sleep(ANotYetSpecifiedStm node, Context ctxt) {
		
		ObjectContext octxt = (ObjectContext) ctxt;
		CPUValue cpu = (CPUValue) octxt.self;
		
		CPUResource cpuResc = cpu.resource;
		cpuResc.setSleeping(true);
		
		return new VoidValue();
	}

	public static Value active(ANotYetSpecifiedStm node, Context ctxt) {

		ObjectContext octxt = (ObjectContext) ctxt;
		CPUValue cpu = (CPUValue) octxt.self;
		
		CPUResource cpuResc = cpu.resource;
		cpuResc.setSleeping(false);
		
		return new VoidValue();
	}

}
