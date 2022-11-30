package nurgling.bots.actions;

import nurgling.NGameUI;

public class WaitAction implements Action {
    public WaitAction(Expression exp, int delay) {
        _exp = exp;
        this.delay = delay;
    }

    public interface Expression {
        public boolean isTrue()
                throws InterruptedException;
    }

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        int counter = 0;
        while ((_exp.isTrue()) || counter < 20) {
            counter++;
            Thread.sleep(delay);
        }
        if(_exp.isTrue()){
            return new Results(Results.Types.WAIT_FAIL);
        }
        return new Results(Results.Types.SUCCESS);
    }

    Expression _exp;
    int delay;
}
