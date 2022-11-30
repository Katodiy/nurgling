package nurgling.bots.actions;

import nurgling.NGameUI;
import nurgling.NUtils;

public class BarChangeAction<T> implements Action {
    public BarChangeAction(Expression<T> exp, Predicate pred) {
        _exp = exp;
        _pred = pred;
    }

    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        int counter = 0;
        T old;
        do {
            old = _exp.getValue();
            counter+=1;
            Thread.sleep(50);
        } while ((_exp.getValue() != old  && !_pred.isTrue() && NUtils.getProg() >=0) || counter < 20);
        if (!_pred.isTrue()) {
            System.out.println("Bar not Change");
            return new Results(Results.Types.FAIL);
        }
        return new Results(Results.Types.SUCCESS);
    }

    interface Expression<T> {
        T getValue();
    }

    interface Predicate {
        boolean isTrue();
    }


    Expression<T> _exp;

    Predicate _pred;
}
