package me.itoncek;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.utils.cache.*;

import java.io.*;
import java.util.*;

public class Main {
	public static JDA jda;
	public static List<File> deleteFiles = new ArrayList<>();
	public static void main(String[] args) throws InterruptedException {
		JDABuilder builder = JDABuilder.createDefault(System.getenv("TOKEN"));
		
		// Disable parts of the cache
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		// Enable the bulk delete event
		builder.setBulkDeleteSplittingEnabled(false);
		// Set activity (like "playing Something")
		builder.setActivity(Activity.watching("stars twinkle 🌠"));
		
		
		jda = builder.build();
		jda.awaitReady();
		
		jda.updateCommands().addCommands(
				Commands.slash("solve", "Astrometrically solve attached image")
						.addOption(OptionType.ATTACHMENT,"image", "Image to solve",true)
						.addOption(OptionType.STRING, "email", "[NOT REQUIRED] Email to send results to",false)
						.setGuildOnly(true)).queue();
		jda.addEventListener(new SlashListener());
		Runtime.getRuntime().addShutdownHook(new Thread(()-> {
			for (File file : deleteFiles) {
				file.delete();
			}
		}));
	}
}