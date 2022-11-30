/* Preprocessed source code */
package haven.res.lib.globfx;

import haven.*;
import haven.render.*;
import haven.render.RenderTree.Slot;
import java.util.*;
import java.lang.reflect.*;
import java.lang.ref.*;

@FromResource(name = "lib/globfx", version = 12)
public abstract class GlobEffect implements Effect {
    public int hashCode() {
	return(this.getClass().hashCode());
    }

    public boolean equals(Object o) {
	return(this.getClass() == o.getClass());
    }
}
