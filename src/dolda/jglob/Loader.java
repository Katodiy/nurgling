package dolda.jglob;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;

public class Loader {
    private final Class<? extends Annotation> an;
    private final ClassLoader cl;

    private Loader(Class<? extends Annotation> annotation, ClassLoader loader) {
	this.an = annotation;
	this.cl = loader;
    }

    public Iterable<String> names() {
	return(new Iterable<String>() {
		public Iterator<String> iterator() {
		    return(new Iterator<String>() {
			    private Enumeration<URL> rls;
			    private Iterator<String> cur = null;

			    private Iterator<String> parse(URL url) {
				try {
				    List<String> buf = new LinkedList<String>();
				    InputStream in = url.openStream();
				    try {
					BufferedReader r = new BufferedReader(new InputStreamReader(in, "utf-8"));
					String ln;
					while((ln = r.readLine()) != null) {
					    ln = ln.trim();
					    if(ln.length() < 1)
						continue;
					    buf.add(ln);
					}
					return(buf.iterator());
				    } finally {
					in.close();
				    }
				} catch(IOException e) {
				    throw(new GlobAccessException(e));
				}
			    }

			    public boolean hasNext() {
				if((cur == null) || !cur.hasNext()) {
				    if(rls == null) {
					try {
					    rls = cl.getResources("META-INF/glob/" + an.getName());
					} catch(IOException e) {
					    throw(new GlobAccessException(e));
					}
				    }
				    if(!rls.hasMoreElements())
					return(false);
				    URL u = rls.nextElement();
				    cur = parse(u);
				}
				return(true);
			    }

			    public String next() {
				if(!hasNext())
				    throw(new NoSuchElementException());
				String ret = cur.next();
				return(ret);
			    }

			    public void remove() {throw(new UnsupportedOperationException());}
			});
		}
	    });
    }

    public Iterable<Class<?>> classes() {
	return(new Iterable<Class<?>>() {
		public Iterator<Class<?>> iterator() {
		    return(new Iterator<Class<?>>() {
			    private final Iterator<String> names = names().iterator();
			    private Class<?> n = null;

			    public boolean hasNext() {
				while(n == null) {
				    if(!names.hasNext())
					return(false);
				    String nm = names.next();
				    Class<?> c;
				    try {
					c = cl.loadClass(nm);
				    } catch(ClassNotFoundException e) {
					continue;
				    }
				    if(c.getAnnotation(an) == null)
					continue;
				    n = c;
				}
				return(true);
			    }

			    public Class<?> next() {
				if(!hasNext())
				    throw(new NoSuchElementException());
				Class<?> r = n;
				n = null;
				return(r);
			    }

			    public void remove() {throw(new UnsupportedOperationException());}
			});
		}
	    });
    }

    public <T> Iterable<T> instances(final Class<T> cast) {
	return(new Iterable<T>() {
		public Iterator<T> iterator() {
		    return(new Iterator<T>() {
			    private final Iterator<Class<?>> classes = classes().iterator();
			    private T n = null;

			    public boolean hasNext() {
				while(n == null) {
				    if(!classes.hasNext())
					return(false);
				    Class<?> cl = classes.next();
				    T inst;
				    try {
					inst = cast.cast(cl.newInstance());
				    } catch(InstantiationException e) {
					throw(new GlobInstantiationException(e));
				    } catch(IllegalAccessException e) {
					throw(new GlobInstantiationException(e));
				    }
				    n = inst;
				}
				return(true);
			    }

			    public T next() {
				if(!hasNext())
				    throw(new NoSuchElementException());
				T r = n;
				n = null;
				return(r);
			    }

			    public void remove() {throw(new UnsupportedOperationException());}
			});
		}
	    });
    }

    public Iterable<?> instances() {
	return(instances(Object.class));
    }

    public static Loader get(Class<? extends Annotation> annotation, ClassLoader loader) {
	return(new Loader(annotation, loader));
    }

    public static Loader get(Class<? extends Annotation> annotation) {
	return(get(annotation, annotation.getClassLoader()));
    }
}
