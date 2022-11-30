package dolda.jglob;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.*;

@SupportedAnnotationTypes({"*"})
public class Collector extends AbstractProcessor {
    private ProcessingEnvironment cfg;
    private Elements eu;
    private boolean verbose = false;

    public void init(ProcessingEnvironment cfg) {
	this.cfg = cfg;
	eu = cfg.getElementUtils();
    }

    private String tn(TypeElement el) {
	return(eu.getBinaryName(el).toString());
    }

    private Set<String> getprev(TypeElement annotation) {
	Set<String> prev = new HashSet<String>();
	try {
	    FileObject lf = cfg.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/glob/" + tn(annotation));
	    InputStream in;
	    try {
		in = lf.openInputStream();
	    } catch(FileNotFoundException | NoSuchFileException e) {
		return(prev);
	    }
	    try {
		BufferedReader r = new BufferedReader(new InputStreamReader(in, "utf-8"));
		String ln;
		while((ln = r.readLine()) != null)
		    prev.add(ln);
		return(prev);
	    } finally {
		in.close();
	    }
	} catch(IOException e) {
	    cfg.getMessager().printMessage(Diagnostic.Kind.ERROR, "could not read previous globlist for " + tn(annotation) + ": " + e);
	    return(Collections.emptySet());
	}
    }

    private void writenew(TypeElement annotation, Collection<String> names) {
	try {
	    FileObject lf = cfg.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/glob/" + tn(annotation));
	    OutputStream out = lf.openOutputStream();
	    try {
		Writer w = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
		for(String nm : names)
		    w.write(nm + "\n");
		w.flush();
	    } finally {
		out.close();
	    }
	} catch(IOException e) {
	    cfg.getMessager().printMessage(Diagnostic.Kind.ERROR, "could not write new globlist for " + tn(annotation) + ": " + e);
	}
    }

    private void process(TypeElement annotation, RoundEnvironment round, TypeMap types) {
	Set<String> prev = getprev(annotation);
	Set<String> carry = new HashSet<String>(prev);
	Set<String> found = new HashSet<String>();
	for(Element e : round.getElementsAnnotatedWith(annotation)) {
	    if(!(e instanceof TypeElement)) {
		cfg.getMessager().printMessage(Diagnostic.Kind.ERROR, tn(annotation) + " must annotate types", e);
		continue;
	    }
	    TypeElement type = (TypeElement)e;
	    String nm = tn(type);
	    if(!prev.contains(nm) && verbose)
		cfg.getMessager().printMessage(Diagnostic.Kind.NOTE, "added " + nm, type);
	    found.add(nm);
	    carry.remove(nm);
	}
	for(Iterator<String> i = carry.iterator(); i.hasNext();) {
	    String nm = i.next();
	    TypeElement el = types.get(nm);
	    if(el != null) {
		i.remove();
		if(verbose)
		    cfg.getMessager().printMessage(Diagnostic.Kind.NOTE, "removed " + nm, el);
	    }
	}
	List<String> all = new ArrayList<String>();
	all.addAll(carry);
	all.addAll(found);
	Collections.sort(all);
	writenew(annotation, all);
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {
	for(TypeElement a : annotations) {
	    if(a.getAnnotation(Discoverable.class) != null)
		process(a, round, new TypeMap(round.getRootElements(), eu));
	}
	return(false);
    }

    public SourceVersion getSupportedSourceVersion() {
	return(SourceVersion.latest());
    }
}
