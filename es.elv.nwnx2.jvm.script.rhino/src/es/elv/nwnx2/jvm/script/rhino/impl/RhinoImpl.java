package es.elv.nwnx2.jvm.script.rhino.impl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.DefaultSecureWrapFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.SecureClassShutter;
import org.mozilla.javascript.SecureScriptRuntime;
import org.mozilla.javascript.TimeoutError;
import org.mozilla.javascript.TimingContextFactory;

import es.elv.nwnx2.jvm.script.EventHandler;
import es.elv.nwnx2.jvm.script.ScriptHost;
import es.elv.nwnx2.jvm.script.ScriptLanguage;
import es.elv.nwnx2.jvm.script.api.IArea;
import es.elv.nwnx2.jvm.script.api.ICreature;
import es.elv.nwnx2.jvm.script.api.IItem;
import es.elv.nwnx2.jvm.script.api.ILocation;
import es.elv.nwnx2.jvm.script.api.IModule;
import es.elv.nwnx2.jvm.script.api.IObject;
import es.elv.nwnx2.jvm.script.api.IPersistency;
import es.elv.nwnx2.jvm.script.api.IPlaceable;
import es.elv.nwnx2.jvm.script.api.IScriptEventRegistry;
import es.elv.nwnx2.jvm.script.api.ISystem;
import es.elv.nwnx2.jvm.script.api.ITask;
import es.elv.nwnx2.jvm.script.api.IUnknown;
import es.elv.nwnx2.jvm.script.api.IVector2;
import es.elv.nwnx2.jvm.script.api.IVector3;
import es.elv.osgi.base.SCRHelper;

public class RhinoImpl extends SCRHelper implements ScriptLanguage<Function, VerifiedRhinoScript> {
	@Override
	public String getIdentifier() {
		return "js/rhino";
	}
	
	final private SecureClassShutter classShutter;
	final private DefaultSecureWrapFactory wrapFactory;
	final private TimingContextFactory cf;
	
	public RhinoImpl() {
		wrapFactory = new DefaultSecureWrapFactory();
		
		// Expose the API.
		wrapFactory.addAllowedNatives(
				IObject.class, IUnknown.class, ICreature.class,
				IArea.class, IItem.class, IModule.class, IPlaceable.class,
								
				ITask.class,
		
				ISystem.class,
				
				IPersistency.class, ILocation.class, IVector2.class, IVector3.class
		);
		
		wrapFactory.addAllowedNatives(IScriptEventRegistry.class);
		
		classShutter = new SecureClassShutter(wrapFactory);
		classShutter.addAllowedStartsWith("es.elv.nwnx2.jvm.script.api.");
		classShutter.addAllowedStartsWith("es.elv.nwnx2.jvm.script.rhino.impl.");
		
		cf = new TimingContextFactory();
	}
	
	private Context getContext(IObject hostObject, VerifiedRhinoScript script) {
		Context ctx = cf.enterContext(150);
		ctx.setWrapFactory(wrapFactory);
		ctx.setClassShutter(classShutter);
		
		//script.getScope().put("MODULE", script.getScope(), new NModule());
		script.getScope().put("host", script.getScope(), Context.javaToJS(hostObject, script.getScope()));
		script.getScope().put("script", script.getScope(), new ScriptEventRegistryImpl(script));

		return ctx;
	}
	
	@Override
	public VerifiedRhinoScript verifyScript(ScriptHost sh, String source, boolean restricted) throws Exception {
		long start = System.currentTimeMillis();
		
		try {
			final Context ctx = Context.enter();
			Scriptable scope = SecureScriptRuntime.initSecureStandardObjects(ctx, null, true);
			Script s = ctx.compileString(source, "", 0, null);
			
			return new VerifiedRhinoScript(this, scope, s, restricted);
		} finally {
			Context.exit();
			
			long end = System.currentTimeMillis();
			log.debug("Verify in " + (end - start) + "ms");
		}
	}

	@Override
	public Object executeScript(ScriptHost sh, IObject hostObject, VerifiedRhinoScript script) throws Exception {
		
		long start = System.currentTimeMillis();		
		
		try {
			final Context ctx = getContext(hostObject, script);

			try {
				return script.getCompiled().exec(ctx, script.getScope());
				
			} catch (TimeoutError tmi) {
				throw new Exception("TMI");
			}
			
		} finally {
			Context.exit();
			
			long end = System.currentTimeMillis();
			log.debug("Script in " + (end - start) + "ms");
		}
	}

	@Override
	public Object excuteEventHandler(ScriptHost sh, IObject hostObject, VerifiedRhinoScript script, EventHandler<Function> eventHandler,
			Object... va) throws Exception {
		
		long start = System.currentTimeMillis();
		
		try {
			final Context ctx = getContext(hostObject, script); 
			
			Scriptable thisObj = ctx.getWrapFactory().wrapAsJavaObject(ctx, script.getScope(),
					hostObject, hostObject.getClass());
			
			try {
				Object[] va2 = new Object[va.length];
				for (int i = 0; i < va.length; i++)
					va2[i] = Context.javaToJS(va[i], script.getScope());
				Object ret = eventHandler.getHandler().call(ctx, script.getScope(), thisObj, va2);
				
				return ret;
				
			} catch (TimeoutError tmi) {
				throw new Exception("TMI");
			}
			
		} finally {
			Context.exit();
			
			long end = System.currentTimeMillis();
			log.debug("Event handler in " + (end - start) + "ms");
		}
	}
}