package co.neweden.speedy;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static co.neweden.speedy.Connection.db;

public class Commands extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        String rank = event.getMember().getRoles().toString();
        String author = event.getAuthor().getName();

        if (!message.startsWith(">b ")) return;

        if (!rank.contains("Senior")) {
            event.getTextChannel().sendMessage("```diff\n- Error : You must be senior or higher to execute this command\n```").queue();
            return;
        }

        if (message.startsWith(">b wwt")) {
            wwtCommand(event);
            return;
        }

        if (message.startsWith(">b add ")) {
            addCommand(event, message, author);
            return;
        }

        if (message.startsWith(">b remove ")) {
            removeCommand(event, message);
            return;
        }

    }

    private void wwtCommand(MessageReceivedEvent event) {
        Factoid f = mcmessages.getLastFactoid();
        String info = "```md\n" + "[ID] " + f.getID() + " | [Factoid] " + f.getFactoid() + " | [Type] " + f.getMessageType() + " | [Response] " + f.getResponse() + " | [Author] " + f.getAuthor() + "\n```";
        event.getTextChannel().sendMessage(info).queue();
    }

    private void addCommand(MessageReceivedEvent event, String message, String author) {
        message = message.substring(7, message.length());
        String AddFactoid = "";
        String AddResponse = "";
        String Addtype = "";
        if (message.contains("<reply>") || message.contains("<action>")) {
            if (message.contains("<reply>")) {
                String[] parts = message.split(" <reply> ", 2);
                AddFactoid = parts[0];
                AddResponse = parts[1];
                Addtype = "reply";
            }
            if (message.contains("<action>")) {
                String[] parts = message.split(" <action> ", 2);
                AddFactoid = parts[0];
                AddResponse = parts[1];
                Addtype = "action";
            }
        }
        else {
            event.getTextChannel().sendMessage("```Factoid failed to be added. Error: Invalid Arguments```").queue();
            return;
        }

        try {
            PreparedStatement st = db.prepareStatement("INSERT INTO `factoids` (`factoid`,`type`, `response`, `author`) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, AddFactoid);
            st.setString(2, Addtype);
            st.setString(3, AddResponse);
            st.setString(4, author);
            st.executeUpdate();
            System.out.println("the following string were added to the database: " + AddFactoid + " | " + AddResponse + " | " + author);
            ResultSet genkeys = st.getGeneratedKeys();
            genkeys.next();
            Integer AddId = genkeys.getInt(1);
            event.getTextChannel().sendMessage("```Factoid has been successfully added with the ID: " + AddId + "```").queue();
        } catch (SQLException e) {
            System.out.println("There was an error with MySQL - >b add");
            System.out.println(e);
            event.getTextChannel().sendMessage("``` Factoid has not been added : error: " + e + "```").queue();
        }
    }

    private void removeCommand(MessageReceivedEvent event, String message) {
        message = message.substring(10, message.length());
        try {
            PreparedStatement st = db.prepareStatement("DELETE FROM `factoids` WHERE id=?");
            st.setString(1, message);
            st.executeUpdate();
            event.getTextChannel().sendMessage("```The factoid with the ID: " + message + " Has been successfully removed ```").queue();
        }
        catch (SQLException e) {
            System.out.println("There was an error with MYSQL - >b remove");
            System.out.println(e);
            event.getTextChannel().sendMessage("``` Factoid has not been removed : error: " + e + "```");
        }

    }
}





