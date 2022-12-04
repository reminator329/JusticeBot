package fr.reminator.justicebot.commands.rolemenu.view.listeners;

import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleView;
import fr.reminator.justicebot.commands.rolemenu.view.MenuRoleViewList;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;

public class ButtonMenuRolesListener implements ButtonClickListener {

    private final MenuRoleViewList view;

    public ButtonMenuRolesListener(MenuRoleViewList menuRoleViewList) {
        this.view = menuRoleViewList;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction buttonInteraction = event.getButtonInteraction();
        String customId = buttonInteraction.getCustomId();

        if (!MenuRoleViewList.buttonCustomId.equals(customId)) {
            return;
        }

        view.sendMenuList(buttonInteraction);
    }
}
