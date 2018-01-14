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

    static public class previousId {
        static Integer str = 0;
    }

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
        String response = getRandomResponse(message);
        if(response == null) return;
        event.getTextChannel().sendMessage(response).queue();
    }
        public static List<String> getResponses (String factoid){
            factoid = factoid.toLowerCase();
            List<String> responses = new ArrayList<>();
            try {
                PreparedStatement st = db.prepareStatement("SELECT response, id FROM factoids WHERE factoid=?");
                st.setString(1, factoid);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    responses.add(rs.getString("response"));

                    previousId.str = rs.getInt("id");
                }
            } catch (SQLException e) {
                System.out.println("There was an error with MySQL with mcmessages");
            }
            return responses;
        }

    public static String getRandomResponse(String message) {
        List<String> responses = getResponses(message);
        if (responses.size() <= 0) return null;

        int randomNum = 0;
        if (responses.size() > 1)
            randomNum = ThreadLocalRandom.current().nextInt(0, responses.size());

        return responses.get(randomNum);
    }

}

