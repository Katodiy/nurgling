package nurgling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NAlias {
    public ArrayList<String> keys;
    public ArrayList<String> exceptions;
    
    public NAlias() {
        keys = new ArrayList<String> ();
        exceptions = new ArrayList<String> ();
    }

    public NAlias(String... args) {
        keys = new ArrayList<String> (Arrays.asList(args));
        exceptions = new ArrayList<String> ();
    }

    public NAlias(String name ) {
        keys = new ArrayList<String> ( Collections.singletonList ( name ) );
        exceptions = new ArrayList<String> ();
    }
    
    public NAlias(ArrayList<String> keys ) {
        this.keys = new ArrayList<String> ();
        exceptions = new ArrayList<String> ();
        this.keys.addAll ( keys );
    }
    
    public NAlias(
            ArrayList<String> keys,
            ArrayList<String> exceptions
    ) {
        this.keys = keys;
        this.exceptions = exceptions;
    }
    
    public NAlias(
            List<String> keys,
            List<String> exceptions
    ) {
        this.keys = new ArrayList<> ();
        this.keys.addAll ( keys );
        this.exceptions = new ArrayList<> ();
        this.exceptions.addAll ( exceptions );
    }
    
    public String getDefault () {
        return keys.get ( 0 );
    }
    
}
