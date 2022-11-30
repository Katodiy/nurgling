package nurgling;

import haven.Button;
import haven.Coord;
import haven.Widget;
import haven.Window;
import nurgling.bots.Bot;

/**
 * Класс виджета окна состояния бота
 * Позволяет прервать активный поток (поток бота) при нажатии клавиши отмена
 */
public class NWindow extends Window {
    /// Размер кнопок в окне по умолчанию
    public int buttons_size = 120;
    
    /**
     * Конструктор
     * @param bot Управляемый бот
     * @param name Отображаемое в заголовке имя
     * @param sz Параметры окна
     */
    public NWindow(
            Bot bot,
            String name,
            Coord sz
    ) {
        super ( new Coord ( sz ), name );
        this.bot = bot;
        /// Добавляем кнопку отмены
        add ( new Button ( buttons_size, "Закрыть" ) {
            /**
             * Реализация слота клик
             */
            public void click () {
                /// Прерываем связанный поток
                bot.close ();
            }
        }, new Coord ( 0, sz.y - 20 ) );
        /// Добавляем окно к текущему интерфейсу
        pack ();
    }
    
    @Override
    public void wdgmsg (
            Widget sender,
            String msg,
            Object... args
    ) {
        if ( sender == cbtn ) {
            bot.close ();
        }
        else {
            super.wdgmsg ( sender, msg, args );
        }
    }
    
    private Bot bot;
    
    
}