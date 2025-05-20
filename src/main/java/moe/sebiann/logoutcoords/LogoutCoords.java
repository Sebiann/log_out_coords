package moe.sebiann.logoutcoords;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LogoutCoords implements ModInitializer {
    private static Logger COORD_LOGGER;

    @Override
    public void onInitialize() {
        setupCustomLogger();

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            var pos = player.getPos();
            COORD_LOGGER.info("{} disconnected at X: {}, Y: {}, Z: {}",
                    player.getName().getString(), pos.x, pos.y, pos.z);
        });
    }

    private void setupCustomLogger() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        Path logPath = Paths.get("logs/disconnect-coords.log");
        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("[%d{yyyy-MM-dd HH:mm:ss}] [%level] %msg%n")
                .build();

        FileAppender appender = FileAppender.newBuilder()
                .withFileName(logPath.toString())
                .withAppend(true)
                .setName("CoordFileAppender")
                .setLayout(layout)
                .setConfiguration(config)
                .build();

        appender.start();
        config.addAppender(appender);

        LoggerConfig loggerConfig = new LoggerConfig("CoordLogger", org.apache.logging.log4j.Level.INFO, false);
        loggerConfig.addAppender(appender, null, null);
        config.addLogger("CoordLogger", loggerConfig);

        context.updateLoggers();
        COORD_LOGGER = LogManager.getLogger("CoordLogger");
    }
}
