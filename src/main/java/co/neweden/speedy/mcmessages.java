package co.neweden.speedy;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


import static co.neweden.speedy.Connection.db;


public class mcmessages extends ListenerAdapter {

    private static Factoid lastFactoid;

    public void onMessageReceived(MessageReceivedEvent event) {
        //Message and nickname grab from discord
        String message = event.getMessage().getContent();
        String nickname = event.getMember().getEffectiveName();
        String name = event.getAuthor().getName();


        //Checker if mc server is talking
        if (event.getJDA().getSelfUser().equals(event.getAuthor())) return;
        if (nickname.startsWith("[MC] ") && nickname.length() >= 5) {
            nickname = nickname.substring(5, nickname.length());
        }
        //Checking the factoid and putting in a response
        Factoid factoid = getRandomFactoid(message);
        if (factoid == null) return;
        if (factoid.getMessageType().toString() == "reply") {
            event.getTextChannel().sendMessage(factoid.getResponse()).queue();
        }
        if (factoid.getMessageType().toString() == "action") {

            event.getTextChannel().sendMessage("_`" + factoid.getResponse() + "`_").queue();
        }
    }


    public static Factoid getRandomFactoid(String message) {
        List<Factoid> responses = getFactoids(message);
        if (responses.size() <= 0) return null;

        int randomNum = 0;
        if (responses.size() > 1)
            randomNum = ThreadLocalRandom.current().nextInt(0, responses.size());

        lastFactoid = responses.get(randomNum);
        return lastFactoid;
    }

    public static Factoid getLastFactoid() { return lastFactoid; }

    public static List<Factoid> getFactoids(String factoid) {
        factoid = factoid.toLowerCase();
        List<Factoid> responses = new ArrayList<>();
        try {
            PreparedStatement st = db.prepareStatement("SELECT * FROM factoids WHERE factoid=?");
            st.setString(1, factoid);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Factoid f = new Factoid();
                f.id = rs.getInt("id");
                f.factoid = rs.getString("factoid");
                f.type = MessageType.valueOf(rs.getString("type"));;
                f.response = rs.getString("response");
                f.author = rs.getString("author");
                responses.add(f);
            }
        } catch (SQLException e) {
            System.out.println("There was an error with MySQL with mcmessages");
        }
        return responses;
    }

}

