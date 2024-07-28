package cn.taskeren.novelty;

import cn.taskeren.novelty.init.NoveltyId;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {

	public static String greeting = "Hello World";

	public static void synchronizeConfiguration(File configFile) {
		Configuration configuration = new Configuration(configFile);

		greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");
		NoveltyId.ID_BASE = configuration.getInt("id-base", Configuration.CATEGORY_GENERAL, NoveltyId.ID_BASE, 0, Short.MAX_VALUE, "The base id of Novelty machines");

		if(configuration.hasChanged()) {
			configuration.save();
		}
	}
}
