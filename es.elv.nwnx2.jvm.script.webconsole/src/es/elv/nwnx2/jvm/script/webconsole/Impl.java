package es.elv.nwnx2.jvm.script.webconsole;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.nwnx.nwnx2.jvm.NWObject;

import es.elv.nwnx2.jvm.script.ScriptHost;
import es.elv.nwnx2.jvm.script.VerifiedScript;
import es.elv.nwnx2.jvm.script.api.IObject;
import es.elv.osgi.base.Autolocate;
import es.elv.osgi.base.SCRHelper;

public class Impl extends SCRHelper implements Servlet {
	@Autolocate
	private ScriptHost sh;

	@Override
	public void destroy() {
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void init(ServletConfig arg0) throws ServletException {
	}

	@Override
	synchronized public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {

		PrintWriter w = response.getWriter();

		w.write("<pre>");
		w.write("Active scripts:\n");
		for (Entry<VerifiedScript<?>, Set<NWObject>> s : sh.getVerifiedScriptMap().entrySet()) {
			w.write("\t" + s.getKey().toString() + "\n");
			for (NWObject o : s.getValue())
				w.write("\t\t" + o.toString() + "\n");
		}
		w.write("\n\n");
		w.write("IManaged:\n");
		for (IObject o : sh.getMappedObjects()) {
			w.write("\t" + o.toString() + "\n");
		}
		// + s.getValue().toString() + "\n");
		
		w.write("</pre>");
	}
}
