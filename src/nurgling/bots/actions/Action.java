package nurgling.bots.actions;

import nurgling.NGameUI;

public interface Action {
    Results run ( NGameUI gui )
            throws InterruptedException;
}
