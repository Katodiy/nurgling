package nurgling.bots.actions;

import nurgling.NGameUI;

import java.util.ArrayList;

public class Loop implements Action {

    public Loop(ArrayList<Action> actions, Expression expression){
        _actions = actions;
        _expression = expression;
    }

    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        while (_expression.isTrue()) {
            for (Action action : _actions) {
                Results res = action.run(gui);
                if(res.type!=Results.Types.SUCCESS) {
                    System.out.println(res.toString());
                    return res;
                }
            }
        }
        return new Results(Results.Types.SUCCESS);
    }

    public interface Expression {
        boolean isTrue();
    }

    Expression _expression;
    ArrayList<Action> _actions;
}
