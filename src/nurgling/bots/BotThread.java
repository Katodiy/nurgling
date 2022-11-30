package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.Action;

/// Базовый класс бота
public class BotThread implements Runnable {
    /**
     * Базовый класс ботов
     *
     * @param gameUI Интерфейс клиента
     */
    public BotThread(
            NGameUI gameUI,
            Action action
    ) {
        this.gameUI = gameUI;
        this.action = action;
    }
    
    /**
     * Основная функция запуска потока
     */
    @Override
    public void run () {
        /// Получаем ссылку на текущий поток
        thread = Thread.currentThread ();
        try {
            action.run ( gameUI );
        }
        catch ( InterruptedException ignored ) {
        }
    }
    
    protected NGameUI gameUI;
    protected Thread thread;
    protected Action action;
    
}
