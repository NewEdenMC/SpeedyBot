package co.neweden.speedy;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import java.sql.*;
import javax.security.auth.login.LoginException;

public class Connection {
    public static java.sql.Connection db;

    public static void main(String[] args) {
        loadDBConnection();
        setupDB();
        JDA discord = null;


        try {
            discord = new JDABuilder(AccountType.BOT).setToken(Constant.discordToken).buildBlocking();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }

        discord.addEventListener(new mcmessages());
        discord.addEventListener(new Commands());
    }


    public static boolean loadDBConnection() {
        String host = "localhost";
        String port = "3306";
        String database = "database";
        if (host == null || port == null || database == null) {
            System.out.println("No database found");
            return false;
        }

        String url = String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true", host, port, database);

        try {
            db = DriverManager.getConnection(url, "harryharry6", "harry");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Connected to MySQL Database");
        return true;

    }
    public static boolean setupDB() {
        try {
            db.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS `factoids` (\n" +
                    "            `id` INT NOT NULL AUTO_INCREMENT,\n" +
                    "  `factoid` VARCHAR(140) NOT NULL,\n" +
                    "  `type` VARCHAR(140) NOT NULL,\n" +
                    "  `response` VARCHAR(140) NOT NULL,\n" +
                    "  `author` VARCHAR(70) NOT NULL,\n" +
                    "    PRIMARY KEY (`id`));");
            System.out.println("MySQL Table Created/Found");

        } catch (SQLException e) {
            System.out.println("Failed to create table");
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
