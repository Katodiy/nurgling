package nurgling.bots.settings;

import nurgling.NConfiguration;

import java.io.IOException;

public class DiscordWebhookWrap extends Settings {
    public void DiscordWebhookWrap(){}
    public static void Push(String message){
        String _discordWebhookUrl = NConfiguration.getInstance().discordWebhookUrl;
        if(_discordWebhookUrl.length() == 0){return;}

        String _discordWebhookUsername = NConfiguration.getInstance().discordWebhookUsername;
        if(_discordWebhookUsername.length() == 0){_discordWebhookUsername = "Default Username";}

        String _discordWebhookIcon = NConfiguration.getInstance().discordWebhookIcon;
        if(_discordWebhookIcon.length() == 0){_discordWebhookIcon = "https://raw.githubusercontent.com/Katodiy/nurgling/master/etc/icon.png";}

        nurgling.tools.DiscordWebhook webhook = new nurgling.tools.DiscordWebhook(_discordWebhookUrl);
        if(message.length() > 0){
            webhook.setContent(message);
        }else{
            webhook.setContent("Test message. Gimme some real message.");
        }
        webhook.setAvatarUrl(_discordWebhookIcon);
        webhook.setUsername(_discordWebhookUsername);
        webhook.addEmbed(new nurgling.tools.DiscordWebhook.EmbedObject()
                .setColor(java.awt.Color.RED)
                .setThumbnail(_discordWebhookIcon)
                .setAuthor("Nurgling", "https://github.com/Katodiy/nurgling", "https://raw.githubusercontent.com/Katodiy/nurgling/master/etc/icon.png")
                .setUrl("https://github.com/Katodiy/nurgling"));
        try {
            webhook.execute(); //Handle exception
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
