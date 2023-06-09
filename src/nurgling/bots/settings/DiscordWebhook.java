package nurgling.bots.settings;

import haven.Button;
import haven.Label;
import haven.TextEntry;
import haven.Widget;
import nurgling.NConfiguration;
import nurgling.tools.AreasID;

import java.io.IOException;

public class DiscordWebhook extends Settings {
    TextEntry discordWebhookUrl;
    TextEntry discordWebhookUsername;
    TextEntry discordWebhookIcon;
    public DiscordWebhook(){
        Widget first, second, third;
        prev = add(new Label("Discord settings:"));

        prev = first = add(new Label("Webhook URL:"), prev.pos("bl").adds(0, 15));
        second = discordWebhookUrl = add(new TextEntry(50,""), first.pos("ur").adds(15, -5));
        if(NConfiguration.getInstance().discordWebhookUrl!=null) {
            discordWebhookUrl.settext(NConfiguration.getInstance().discordWebhookUrl);
        }

        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().discordWebhookUrl = discordWebhookUrl.text();
            }
        }, second.pos("ur").adds(5, -5));



        prev = first = add(new Label("Bot name:"),prev.pos("bl").adds(0, 15));
        second = discordWebhookUsername = add(new TextEntry(50,""), first.pos("ur").adds(15, -5));
        if(NConfiguration.getInstance().discordWebhookUsername!=null) {
            discordWebhookUsername.settext(NConfiguration.getInstance().discordWebhookUsername);
        }
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().discordWebhookUsername = discordWebhookUsername.text();
            }
        }, second.pos("ur").adds(5, -5));

        prev = first = add(new Label("Bot icon url:"),prev.pos("bl").adds(0, 15));
        second = discordWebhookIcon = add(new TextEntry(50,""), first.pos("ur").adds(15, -5));
        if(NConfiguration.getInstance().discordWebhookIcon!=null) {
            discordWebhookIcon.settext(NConfiguration.getInstance().discordWebhookIcon);
        }
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().discordWebhookIcon = discordWebhookIcon.text();
            }
        }, second.pos("ur").adds(5, -5));
        prev = third = add(new Button(200, "Test hook"){
            @Override
            public void click() {
                DiscordWebhookWrap.Push("This is test message!");
            }
        }, prev.pos("bl").adds(0, 10));
        pack();
    }
}
