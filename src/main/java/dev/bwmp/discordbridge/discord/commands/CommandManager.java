package dev.bwmp.discordbridge.discord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final String prefix;
    private final Map<String, Command> commands = new HashMap<>();

    public CommandManager(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Register a command
     */
    public void register(Command command) {
        commands.put(command.getName().toLowerCase(), command);
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    /**
     * Handle an incoming message, returns true if a command was executed
     */
    public boolean handleMessage(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();

        if (!content.startsWith(prefix)) {
            return false;
        }

        String[] parts = content.substring(prefix.length()).split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return false;
        }

        String commandName = parts[0].toLowerCase();
        Command command = commands.get(commandName);

        if (command == null) {
            return false;
        }

        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        command.execute(event, args);
        return true;
    }

    /**
     * Get all registered commands (unique, no aliases)
     */
    public Collection<Command> getCommands() {
        return commands.values().stream()
                .distinct()
                .toList();
    }

    /**
     * Get a command by name or alias
     */
    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public String getPrefix() {
        return prefix;
    }
}
