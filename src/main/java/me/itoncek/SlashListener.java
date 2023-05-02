package me.itoncek;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.hooks.*;
import net.dv8tion.jda.api.interactions.commands.*;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;

import static me.itoncek.Main.*;

public class SlashListener extends ListenerAdapter {
	private final List<String> exts = Arrays.asList("jpeg", "jpg", "png", "tif", "fit");
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("solve")) {
			event.deferReply(true).queue();
			Message.Attachment attachment = event.getOptionsByType(OptionType.ATTACHMENT).get(0).getAsAttachment();
			if(exts.contains(attachment.getFileExtension())) {
				long epoch = System.currentTimeMillis() / 1000;
				String ec = "Image extension: " + attachment.getFileExtension() + "\nSubmitted <t:" + epoch + ":R> \nMaximum time: 10 minutes";
				event.getHook()
						.sendMessageEmbeds(buildEmbed("🔭Astrometry request🔭", ec, Color.GREEN))
						.setEphemeral(true)
						.queue();
				try {
					File file = attachment.getProxy().downloadToFile(new File("D:\\#astro\\astrometry\\dbot\\" + attachment.getFileName())).get();
					deleteFiles.add(file);
				} catch (InterruptedException | ExecutionException ex) {
					ec = ec + "\n\nOur bot has fell asleep while processing your request. \nPlease message <@580098459802271744> with following info: \n`359 DISK-ERROR`";
					event.getHook().editOriginalEmbeds(buildEmbed("💥SOLVING ERROR💥", ec, Color.RED)).queue();
					return;
				}
				File f = new File("D:\\#astro\\astrometry\\dbot\\" + attachment.getFileName() + ".sh");
				deleteFiles.add(f);
				try (FileWriter fw = new FileWriter(f)) {
					fw.write("cd /mnt/d/#astro/astrometry/dbot/");
					fw.write("solve-field --downsample 4 --overwrite " + attachment.getFileName());
				} catch (IOException e) {
					ec = ec + "\n\nOur bot has fell asleep while processing your request. \nPlease message <@580098459802271744> with following info: \n`359 DISK-ERROR`";
					event.getHook().editOriginalEmbeds(buildEmbed("💥SOLVING ERROR💥", ec, Color.RED)).queue();
					return;
				}
				ec = ec + "\n\nFiles downloaded, starting processing!";
				event.getHook()
						.editOriginalEmbeds(buildEmbed("🔭Astrometry request🔭", ec, Color.GREEN))
						.queue();
				
				Runtime.getRuntime().exec("wsl -d Ubuntu-22.04 -e chmod +x /mnt/d/#astro/astrometry");
			} else {
				event.getHook().sendMessage("Sorry, I cannot process filetype `" + attachment.getFileExtension() + "`").setEphemeral(true).queue();
			}
		}
	}
	
	public static MessageEmbed buildEmbed(String title, String desc, Color color) {
		return new EmbedBuilder()
				.setAuthor(jda.getSelfUser().getName(), jda.getSelfUser().getAvatarUrl(), jda.getSelfUser().getEffectiveAvatarUrl())
				.setTitle(title)
				.setDescription(desc)
				.setColor(color)
				.setFooter("Running code from https://astrometry.net")
				.build();
	}
	
}
