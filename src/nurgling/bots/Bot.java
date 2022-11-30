package nurgling.bots;

import haven.Coord;
import nurgling.NGameUI;
import nurgling.NOCache;
import nurgling.NWindow;
import nurgling.bots.actions.Action;
import nurgling.bots.actions.Results;

import java.util.ArrayList;

/// Базовый класс бота
public class Bot implements Runnable {
    /**
     * Базовый класс ботов
     *
     * @param gameUI Интерфейс клиента
     */
    public Bot(NGameUI gameUI) {
        this.gameUI = gameUI;
    }

    /**
     * Основная функция запуска потока
     */
    @Override
    public void run() {
        /// Получаем ссылку на текущий поток
        thread = Thread.currentThread();
        /// Вызываем функцию инициализации бота
        window = gameUI.add(new NWindow(this, this.win_title, win_sz));
        try {
            initAction();
            runAction();
            for (Action action : runActions) {
                if (action.run(gameUI).type != Results.Types.SUCCESS) {
                    thread.interrupt();
                }
            }
        } catch (InterruptedException e) {
        } finally {
            endAction();
        }
    }

    /**
     * Процедура работы бота
     *
     * @throws InterruptedException Исключение прерывания потока
     */
    public void runAction()
            throws InterruptedException {
    }

    /**
     * Процедура инициализации компонентов бота
     */
    public void initAction()
            throws InterruptedException {
    }

    /**
     * Процедура завершения работы
     */
    public void endAction() {
        if (((NOCache) gameUI.ui.sess.glob.oc).paths != null)
            ((NOCache) gameUI.ui.sess.glob.oc).paths.pflines = null;
//        NUtils.destroyOverlays();
        /// Убиваем окно состояния бота
        window.destroy();
    }

    public void closeAction() {
    }

    public void close() {
        closeAction();
        /// Если поток все еще активен, то прерываем его
        if (Thread.currentThread().isAlive()) {
            thread.interrupt();
        }
    }

    /// Интерфейс клиента
    protected NGameUI gameUI;
    /// Виджет-окно бота
    protected NWindow window;
    /// Связанный поток
    protected Thread thread;
    /// Параметры свойств окна отсоединены от окна, т.к. оно не изменяемом после создания и эти параметры
    // используются для создания окна
    /// Заголовок виджета состояния бота
    protected String win_title = "Bot";
    /// Параметры виджета состояния окна
    protected Coord win_sz = new Coord(80, 200);
    protected ArrayList<Action> initActions = new ArrayList<>();
    protected ArrayList<Action> runActions = new ArrayList<>();
}
